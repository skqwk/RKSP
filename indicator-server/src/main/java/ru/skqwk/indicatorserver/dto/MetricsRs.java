package ru.skqwk.indicatorserver.dto;

import java.util.UUID;

public record MetricsRs(UUID uuid,
                        String indicatorName,
                        String type,
                        String value,
                        String recordedAt) {
}
