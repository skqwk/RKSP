package ru.skqwk.indicatorserver.util;

/**
 * Вспомогательный класс для имитации долгой работы
 */
public class Sleeper {
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
