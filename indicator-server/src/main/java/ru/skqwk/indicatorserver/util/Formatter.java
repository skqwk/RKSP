package ru.skqwk.indicatorserver.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Вспомогательный класс для форматирования дат
 */
public class Formatter {
    private static final String PATTERN_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
            .withZone(ZoneId.systemDefault());

    public static String format(Instant instant) {
        return Optional.ofNullable(instant)
                .map(formatter::format)
                .orElse(PATTERN_FORMAT);
    }
}
