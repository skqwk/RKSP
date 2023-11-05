package ru.skqwk.indicatorserver.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IndicatorType {
    LAMP("cd", 40, 100),
    DIODE("cd", 20, 80),
    THERMOMETER("degree", -50, 50),
    BAROMETER("pa", 1, 5);

    private static final String ERROR_MESSAGE =
            "Получено значение - %s, ожидаемый интервал для %s - [%s, %s]";

    private final String metricsType;
    private final int min;
    private final int max;

    public void validate(int value) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format(ERROR_MESSAGE, value, this, min, max)
            );
        }
    }
}