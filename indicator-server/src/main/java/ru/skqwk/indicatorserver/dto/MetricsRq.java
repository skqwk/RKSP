package ru.skqwk.indicatorserver.dto;

public record MetricsRq(String indicatorName,
                        String type,
                        String value) {
}
