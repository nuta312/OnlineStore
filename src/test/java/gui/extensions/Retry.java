package gui.extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retry {

    /**
     * Максимальное количество попыток (включая первую).
     * По умолчанию: 1 (без retry).
     * Пример использования: Retry(3) над Test методом.
     */
    int value() default 1;
}