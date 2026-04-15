package gui.extensions;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v137.network.Network;
import org.openqa.selenium.devtools.v137.network.model.RequestId;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class AllureUIExtension implements BeforeEachCallback, AfterEachCallback {

    private static final String APPENDER_NAME = "AllureUIInMemory";

    private static final Map<String, Level> SUPPRESSED_LOGGERS = Map.of(
            "org.littleshoot",        Level.OFF,
            "io.restassured",         Level.WARN,
            "org.apache.http",        Level.WARN,
            "io.netty",               Level.WARN,
            "com.codeborne.selenide", Level.WARN,
            "org.openqa.selenium",    Level.WARN,
            "io.github.bonigarcia",   Level.WARN
    );

    private InMemoryLog4j2Appender appender;
    private LoggerConfig rootLoggerConfig;
    private DevTools devTools;

    // Thread-safe список для сетевых логов из CDP
    private final List<String> networkLogs = new CopyOnWriteArrayList<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        startLog4j2Capture();
    }

    // CDP можно подключить только после открытия браузера — вызываем из теста или BeforeEach
    // Selenide открывает браузер лениво, поэтому CDP цепляем отдельным методом
    private void attachCdpIfReady() {
        try {
            if (!WebDriverRunner.hasWebDriverStarted()) return;
            if (!(WebDriverRunner.getWebDriver() instanceof ChromeDriver chromeDriver)) return;
            if (devTools != null) return; // уже подключён

            devTools = chromeDriver.getDevTools();
            devTools.createSession();

            devTools.send(Network.enable(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            ));

            // Запрос
            devTools.addListener(Network.requestWillBeSent(), request -> {
                String log = String.format("[REQUEST]  %s %s%n",
                        request.getRequest().getMethod(),
                        request.getRequest().getUrl()
                );
                networkLogs.add(log);
            });

            // Ответ — заголовки
            devTools.addListener(Network.responseReceived(), response -> {
                String log = String.format("[RESPONSE] %s %s %s%n",
                        response.getResponse().getStatus(),
                        response.getResponse().getStatusText(),
                        response.getResponse().getUrl()
                );
                networkLogs.add(log);
            });

            // Ответ — тело (только при падении, чтобы не грузить память)
            devTools.addListener(Network.loadingFinished(), event -> {
                RequestId requestId = event.getRequestId();
                try {
                    String body = devTools.send(Network.getResponseBody(requestId)).getBody();
                    if (body != null && !body.isBlank()) {
                        networkLogs.add(String.format("[BODY]     requestId=%s%n%s%n", requestId, body));
                    }
                } catch (Exception ignored) {
                    // Не все ответы имеют тело (redirect, 204 и т.д.)
                }
            });

        } catch (Exception e) {
            networkLogs.add("[CDP ERROR] Failed to attach DevTools: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        // CDP цепляем здесь на случай если браузер открылся в BeforeEach теста
        attachCdpIfReady();

        boolean testFailed = context.getExecutionException().isPresent();

        if (testFailed) {
            takeScreenshot(context);
        }

        detachLogs(testFailed);
        closeCdp();
    }

    private void startLog4j2Capture() {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration config = loggerContext.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{[yyyy-MM-dd] HH:mm:ss:SSS} %c{1}: %L [%-5p] - %m%n")
                .build();

        appender = new InMemoryLog4j2Appender(APPENDER_NAME, layout);
        appender.start();

        rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.addAppender(appender, null, null);
        loggerContext.updateLoggers();
    }

    private void takeScreenshot(ExtensionContext context) {
        try {
            if (!WebDriverRunner.hasWebDriverStarted()) return;

            byte[] screenshot = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                    .getScreenshotAs(OutputType.BYTES);

            Allure.addAttachment(
                    "Screenshot on failure: " + context.getDisplayName(),
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        } catch (Exception e) {
            Allure.addAttachment(
                    "Screenshot error",
                    "text/plain",
                    new ByteArrayInputStream(
                            ("Failed to take screenshot: " + e.getMessage())
                                    .getBytes(StandardCharsets.UTF_8)
                    ),
                    ".txt"
            );
        }
    }

    private void detachLogs(boolean testFailed) {
        // Log4j2 логи
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        rootLoggerConfig.removeAppender(APPENDER_NAME);
        loggerContext.updateLoggers();

        String appLogs = buildFilteredLogs();
        appender.stop();

        if (!appLogs.isBlank()) {
            Allure.addAttachment(
                    "Application logs",
                    "text/plain",
                    new ByteArrayInputStream(appLogs.getBytes(StandardCharsets.UTF_8)),
                    ".txt"
            );
        }

        // Network логи — всегда, не только при падении
        if (!networkLogs.isEmpty()) {
            String netLogs = String.join("", networkLogs);
            Allure.addAttachment(
                    "Network logs (DevTools)",
                    "text/plain",
                    new ByteArrayInputStream(netLogs.getBytes(StandardCharsets.UTF_8)),
                    ".txt"
            );
        }
    }

    private void closeCdp() {
        try {
            if (devTools != null) {
                devTools.close();
                devTools = null;
            }
        } catch (Exception ignored) {}
        networkLogs.clear();
    }

    private String buildFilteredLogs() {
        StringBuilder sb = new StringBuilder();
        for (InMemoryLog4j2Appender.LogEntry entry : appender.getEntries()) {
            String loggerName = entry.loggerName();
            Level eventLevel = entry.level();

            boolean suppressed = SUPPRESSED_LOGGERS.entrySet().stream()
                    .anyMatch(e -> loggerName.startsWith(e.getKey())
                            && !eventLevel.isMoreSpecificThan(e.getValue()));

            if (!suppressed) {
                sb.append(entry.message());
            }
        }
        return sb.toString();
    }

    private static class InMemoryLog4j2Appender extends AbstractAppender {

        private final List<LogEntry> entries = new ArrayList<>();

        protected InMemoryLog4j2Appender(String name, PatternLayout layout) {
            super(name, null, layout, true, Property.EMPTY_ARRAY);
        }

        @Override
        public synchronized void append(LogEvent event) {
            byte[] bytes = getLayout().toByteArray(event);
            entries.add(new LogEntry(
                    event.getLoggerName(),
                    event.getLevel(),
                    new String(bytes, StandardCharsets.UTF_8)
            ));
        }

        public List<LogEntry> getEntries() {
            return entries;
        }

        record LogEntry(String loggerName, Level level, String message) {}
    }
}