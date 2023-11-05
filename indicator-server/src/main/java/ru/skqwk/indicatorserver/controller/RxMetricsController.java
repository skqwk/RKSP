package ru.skqwk.indicatorserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skqwk.indicatorserver.dto.MetricsRq;
import ru.skqwk.indicatorserver.dto.MetricsRs;
import ru.skqwk.indicatorserver.entity.Metrics;
import ru.skqwk.indicatorserver.service.RxMetricsService;
import ru.skqwk.indicatorserver.util.Formatter;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RxMetricsController {
    private final RxMetricsService metricsService;

    @GetMapping("/metrics")
    public Flux<MetricsRs> findAllMetrics() {
        return metricsService.getAllMetrics().map(this::toMetricsRs);
    }

    @GetMapping("/metrics/type/{type}")
    public Flux<MetricsRs> findAllMetricsByType(@PathVariable String type) {
        return metricsService.getMetricsByType(type).map(this::toMetricsRs);
    }

    @GetMapping("/metrics/name/{indicatorName}")
    public Flux<MetricsRs> findAllMetricsByIndicatorName(@PathVariable String indicatorName) {
        return metricsService.getMetricsByIndicatorName(indicatorName)
                .map(this::toMetricsRs);
    }

    @GetMapping("/metrics/{id}")
    public Mono<MetricsRs> findMetric(@PathVariable UUID id) {
        return metricsService.getMetricsById(id)
                .map(this::toMetricsRs)
                .onErrorResume(e -> Mono.error(
                                new ResponseStatusException(
                                        HttpStatusCode.valueOf(404), e.getMessage()
                                )
                        )
                );

    }

    @PostMapping("/metrics")
    public Mono<MetricsRs> saveMetric(@RequestBody MetricsRq metrics) {
        return metricsService.save(metrics.indicatorName(), metrics.type(), metrics.value())
                .map(this::toMetricsRs)
                .onErrorResume(e -> Mono.error(
                                new ResponseStatusException(
                                        HttpStatusCode.valueOf(400), e.getMessage()
                                )
                        )
                );
    }

    private MetricsRs toMetricsRs(Metrics metrics) {
        return new MetricsRs(metrics.getId(),
                metrics.getIndicator(),
                metrics.getType(),
                metrics.getValue(),
                Formatter.format(metrics.getRecordedAt()));
    }

}

