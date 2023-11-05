package ru.skqwk.indicatorserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skqwk.indicatorserver.entity.IndicatorType;
import ru.skqwk.indicatorserver.entity.Metrics;
import ru.skqwk.indicatorserver.entity.MetricsRepo;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RxMetricsServiceImpl implements RxMetricsService {
    private final MetricsRepo metricsRepo;

    @Override
    public Mono<Metrics> save(String indicatorName, String type, String value) {
        log.info("Выполнение запроса save({}, {}, {})", indicatorName, type, value);

        try {
            IndicatorType indicatorType = convertType(type);
            int convertedValue = Integer.parseInt(value);
            indicatorType.validate(convertedValue);
        } catch (Exception e) {
            return Mono.error(e);
        }

        Metrics metrics = Metrics.builder()
                .recordedAt(Instant.now())
                .indicator(indicatorName)
                .value(value)
                .type(type)
                .build();

        Mono<Metrics> saved = metricsRepo.save(metrics);
        return saved.doOnNext(m -> log.info("Сохранена метрика {}", m.getId()));
    }

    private static IndicatorType convertType(String type) {
        log.info("Конвертация типа - {}", type);
        return IndicatorType.valueOf(type);
    }

    @Override
    public Flux<Metrics> getMetricsByType(String type) {
        log.info("Выполнение запроса getMetricsByType({})", type);
        try {
            convertType(type);
            return metricsRepo.findMetricsByType(type);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    @Override
    public Flux<Metrics> getMetricsByIndicatorName(String indicatorName) {
        log.info("Выполнение запроса getMetricsByIndicatorName({})", indicatorName);
        return metricsRepo.findAll()
                .filter(metrics -> metrics.getIndicator().equals(indicatorName));
    }

    @Override
    public Flux<Metrics> getAllMetrics() {
        log.info("Выполнение запроса getAllMetrics");
        return metricsRepo.findAll();
    }

    @Override
    public Mono<Metrics> getMetricsById(UUID metricsId) {
        log.info("Выполнение запроса getMetricsById({})", metricsId);
        return metricsRepo.findById(metricsId);
    }
}
