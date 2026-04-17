package gui.extensions;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

public class RetryExtension implements TestExecutionExceptionHandler {

    private static final int MAX_RETRIES = 3;
    private final ThreadLocal<Integer> retryCount = ThreadLocal.withInitial(() -> 0);

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        
        if (!isRetryableException(throwable)) {
            throw throwable;
        }

        int currentRetry = retryCount.get();

        if (currentRetry < MAX_RETRIES) {
            retryCount.set(currentRetry + 1);
            System.out.printf("[RETRY] %s — попытка %d/%d | Причина: %s%n",
                    context.getDisplayName(),
                    currentRetry + 1,
                    MAX_RETRIES,
                    throwable.getMessage()
            );
            // Перезапускаем тест через рекурсивный выброс
            context.getRequiredTestMethod().invoke(
                    context.getRequiredTestInstance()
            );
        } else {
            retryCount.remove();
            throw throwable;
        }
    }

    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof StaleElementReferenceException
                || throwable instanceof TimeoutException
                || throwable instanceof com.codeborne.selenide.ex.ElementNotFound
                || throwable instanceof AssertionError;
    }
}