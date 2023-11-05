package ru.skqwk.indicatorserver.entity;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MetricsRepo extends ReactiveCrudRepository<Metrics, UUID> {
    Flux<Metrics> findMetricsByType(String type);
}
