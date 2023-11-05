package ru.skqwk.indicatorserver.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skqwk.indicatorserver.entity.Metrics;

import java.util.UUID;

public interface RxMetricsService {
    Mono<Metrics> save(String indicatorName,
                       String type,
                       String value);

    Flux<Metrics> getMetricsByType(String type);


    Flux<Metrics> getMetricsByIndicatorName(String indicatorName);

    Flux<Metrics> getAllMetrics();

    Mono<Metrics> getMetricsById(UUID metricsId);
}
