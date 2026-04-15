package api.extensions;

import io.qameta.allure.Allure;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllureLogsExtension implements BeforeEachCallback, AfterEachCallback {

    private static final String APPENDER_NAME = "AllureInMemory";

    // Логгеры которые глушим — как в log4j2.xml
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

    @Override
    public void beforeEach(ExtensionContext context) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration config = loggerContext.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{[yyyy-MM-dd] HH:mm:ss:SSS} %c{1}: %L [%-5p] - %m%n")
                .build();

        appender = new InMemoryLog4j2Appender(APPENDER_NAME, layout);
        appender.start();

        // Добавляем в root без фильтра — перехватываем всё
        rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.addAppender(appender, null, null);
        loggerContext.updateLoggers();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);

        rootLoggerConfig.removeAppender(APPENDER_NAME);
        loggerContext.updateLoggers();

        String logs = buildFilteredLogs();
        appender.stop();

        if (!logs.isBlank()) {
            Allure.addAttachment(
                    "Test logs",
                    "text/plain",
                    new ByteArrayInputStream(logs.getBytes(StandardCharsets.UTF_8)),
                    ".txt"
            );
        }
    }

    // Повторяем логику фильтрации из log4j2.xml
    private String buildFilteredLogs() {
        StringBuilder sb = new StringBuilder();

        for (InMemoryLog4j2Appender.LogEntry entry : appender.getEntries()) {
            String loggerName = entry.loggerName();
            Level eventLevel = entry.level();

            // Проверяем suppressed loggers
            boolean suppressed = SUPPRESSED_LOGGERS.entrySet().stream()
                    .anyMatch(e -> loggerName.startsWith(e.getKey())
                            && !eventLevel.isMoreSpecificThan(e.getValue()));

            if (!suppressed) {
                sb.append(entry.message());
            }
        }

        return sb.toString();
    }
    @Getter
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