package gui.extensions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * JUnit 5 расширение для автоматического повтора упавших UI-тестов.
 *
 * Подключается через @ExtendWith(RetryExtension.class) в BaseGUI —
 * тогда retry работает для ВСЕХ GUI-тестов автоматически.
 *
 * Количество попыток задаётся аннотацией @Retry:
 *   - На классе:  @Retry(3) — все тесты класса получат 3 попытки
 *   - На методе:  @Retry(2) — только этот тест получит 2 попытки
 *   - Метод приоритетнее класса
 *   - Без аннотации — тест выполняется 1 раз (без retry)
 *
 * Как работает:
 *   1. Тест падает — JUnit передаёт исключение в handleTestExecutionException
 *   2. Смотрим @Retry, узнаём сколько попыток осталось
 *   3. Если есть ещё попытки — вызываем тестовый метод снова через reflection
 *   4. Если все попытки исчерпаны — пробрасываем исходное исключение
 */
public class RetryExtension implements TestExecutionExceptionHandler {

    private static final ExtensionContext.Namespace NS =
            ExtensionContext.Namespace.create(RetryExtension.class);

    @Override
    public void handleTestExecutionException(ExtensionContext context,
                                             Throwable throwable) throws Throwable {
        int maxAttempts = resolveMaxAttempts(context);

        // Нет аннотации @Retry — пробрасываем исключение как есть
        if (maxAttempts <= 1) {
            throw throwable;
        }

        int currentAttempt = getCurrentAttempt(context);

        if (currentAttempt < maxAttempts) {
            incrementAttempt(context);

            String testName = context.getDisplayName();
            Allure.addAttachment(
                    "Retry #" + currentAttempt + " for: " + testName,
                    "text/plain",
                    String.format(
                            "Attempt %d/%d failed.%n" +
                                    "Cause: %s: %s%n" +
                                    "Retrying now...",
                            currentAttempt, maxAttempts,
                            throwable.getClass().getSimpleName(),
                            throwable.getMessage()
                    )
            );

            // Повторно вызываем тестовый метод через reflection
            Method testMethod = context.getRequiredTestMethod();
            Object testInstance = context.getRequiredTestInstance();

            try {
                testMethod.invoke(testInstance);
            } catch (InvocationTargetException e) {
                // reflection оборачивает исключение — достаём настоящее
                handleTestExecutionException(context, e.getCause());
            }

        } else {
            // Все попытки исчерпаны
            Allure.addAttachment(
                    "All retries exhausted: " + context.getDisplayName(),
                    "text/plain",
                    String.format(
                            "Test failed after %d attempt(s).%n" +
                                    "Final error: %s: %s",
                            maxAttempts,
                            throwable.getClass().getSimpleName(),
                            throwable.getMessage()
                    )
            );
            throw throwable;
        }
    }

    // ── Вспомогательные методы ─────────────────────────────────────────────

    /**
     * Определяет максимальное количество попыток.
     * Приоритет: аннотация на методе > аннотация на классе > 1 (без retry).
     */
    private int resolveMaxAttempts(ExtensionContext context) {
        // Сначала ищем @Retry на тестовом методе
        Retry methodRetry = context.getRequiredTestMethod()
                .getAnnotation(Retry.class);
        if (methodRetry != null) {
            return methodRetry.value();  // исправлено: было times()
        }

        // Затем на классе
        Retry classRetry = context.getRequiredTestClass()
                .getAnnotation(Retry.class);
        if (classRetry != null) {
            return classRetry.value();  // исправлено: было times()
        }

        // Аннотации нет — 1 попытка (обычный режим)
        return 1;
    }

    private int getCurrentAttempt(ExtensionContext context) {
        return context.getStore(NS)
                .getOrDefault(attemptKey(context), Integer.class, 1);
    }

    private void incrementAttempt(ExtensionContext context) {
        int current = getCurrentAttempt(context);
        context.getStore(NS).put(attemptKey(context), current + 1);
    }

    private String attemptKey(ExtensionContext context) {
        return context.getUniqueId() + "#attempt";
    }
}