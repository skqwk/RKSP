package ru.skqwk.indicatorserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skqwk.indicatorserver.entity.Indicator;
import ru.skqwk.indicatorserver.entity.IndicatorRepo;
import ru.skqwk.indicatorserver.entity.Metrics;
import ru.skqwk.indicatorserver.entity.MetricsRepo;
import ru.skqwk.indicatorserver.util.Sleeper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl implements IndicatorService {
    private static final int SLEEP_TIME_MS = 2000;

    private final IndicatorRepo indicatorRepo;
    private final MetricsRepo metricsRepo;

    @Override
    public Indicator register(String type) {
        Indicator indicator = new Indicator();
        indicator.setType(type);
        indicator.setRegisteredAt(Instant.now());
        return indicatorRepo.save(indicator);
    }

    @Override
    public Metrics save(UUID indicatorUuid, String type, String value) {
        Indicator indicator = indicatorRepo.findById(indicatorUuid)
                .orElseThrow();

        Metrics metrics = Metrics.builder()
                .recordedAt(Instant.now())
                .indicator(indicator)
                .value(value)
                .type(type)
                .build();

        Sleeper.sleep(SLEEP_TIME_MS);

        return metricsRepo.save(metrics);
    }

    @Override
    public List<Indicator> getAllIndicators() {
        return indicatorRepo.findAll();
    }

    @Override
    public List<Metrics> getMetricsByIndicatorType(String type) {
        return metricsRepo.findAllByIndicator_Type(type);
    }

    @Override
    public List<Metrics> getMetricsByIndicatorUuid(UUID indicatorUuid) {
        Sleeper.sleep(SLEEP_TIME_MS);
        return metricsRepo.findAllByIndicator_Uuid(indicatorUuid);
    }
}
