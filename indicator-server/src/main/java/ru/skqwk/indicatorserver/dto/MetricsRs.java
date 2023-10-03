package ru.skqwk.indicatorserver.dto;

import java.util.UUID;

public record MetricsRs(UUID uuid,
                        String indicatorType,
                        String type,
                        String value,
                        String recordedAt) {
}
