package ru.skqwk.indicatorserver.service;

import ru.skqwk.indicatorserver.entity.Indicator;
import ru.skqwk.indicatorserver.entity.Metrics;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface IndicatorService {
    Indicator register(String type);

    Metrics save(UUID indicatorUuid,
                 String type,
                 String value);

    List<Indicator> getAllIndicators();

    List<Metrics> getMetricsByIndicatorType(String type);

    List<Metrics> getMetricsByIndicatorUuid(UUID indicatorUuid);
}
