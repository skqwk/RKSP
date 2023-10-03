package ru.skqwk.indicatorclient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

import static ru.skqwk.indicatorclient.util.RandomUtil.randomInt;

@Getter
@RequiredArgsConstructor
public enum IndicatorType {
    LAMP("cd", () -> String.valueOf(randomInt(40, 100))),
    DIODE("cd", () -> String.valueOf(randomInt(20, 80))),
    THERMOMETER("degree", () -> String.valueOf(randomInt(-50, 50))),
    BAROMETER("pa", () -> String.valueOf(randomInt(1, 5)));

    private final String metricsType;
    private final Supplier<String> valueGenerator;
}
