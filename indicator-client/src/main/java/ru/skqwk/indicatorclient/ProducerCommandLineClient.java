package ru.skqwk.indicatorclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.skqwk.indicatorclient.dto.MetricsRq;
import ru.skqwk.indicatorclient.dto.MetricsRs;
import ru.skqwk.indicatorclient.dto.RegisterRs;
import ru.skqwk.indicatorclient.util.Timer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("producer")
public class ProducerCommandLineClient implements CommandLineRunner {
    private final IndicatorClient indicatorClient = new IndicatorClient();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private final Map<UUID, IndicatorType> registeredIndicators = new HashMap<>();


    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(IndicatorType.values()).forEach(this::registerIndicator);
        for (UUID uuid : registeredIndicators.keySet()) {
            IndicatorType indicatorType = registeredIndicators.get(uuid);
            startIndicate(uuid, indicatorType);
        }

    }

    private void registerIndicator(IndicatorType type) {
        RegisterRs indicator = Timer.withTimer("Register indicator", () -> indicatorClient.register(type.name()));
        log.info("Indicator [{}] registered at - {}", indicator.uuid(), indicator.registeredAt());
        registeredIndicators.put(indicator.uuid(), type);
    }

    private void startIndicate(UUID uuid, IndicatorType type) {
        executorService.scheduleAtFixedRate(() -> indicate(uuid, type), 0, 2, TimeUnit.SECONDS);
    }

    private void indicate(UUID uuid, IndicatorType type) {
        String metricsType = type.getMetricsType();
        String metricsValue = type.getValueGenerator().get();
        MetricsRq metricsRq = new MetricsRq(metricsType, metricsValue);

        MetricsRs metricsRs = Timer.withTimer("Indicate", () -> indicatorClient.indicate(uuid, metricsRq));
        log.info("Indicator [{}] sent metrics [{}, {}]",
                type.name(), metricsType, metricsValue);
    }
}
