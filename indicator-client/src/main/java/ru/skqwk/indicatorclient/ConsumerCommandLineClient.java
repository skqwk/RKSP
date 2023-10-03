package ru.skqwk.indicatorclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.skqwk.indicatorclient.dto.ListRecordedMetricsRs;
import ru.skqwk.indicatorclient.dto.ListRegisteredIndicatorRs;
import ru.skqwk.indicatorclient.dto.MetricsRs;
import ru.skqwk.indicatorclient.dto.RegisterRs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static ru.skqwk.indicatorclient.util.Timer.withTimer;

@Slf4j
@Component
@Profile("consumer")
public class ConsumerCommandLineClient implements CommandLineRunner {
    private final IndicatorClient indicatorClient = new IndicatorClient();
    private Set<UUID> registeredIndicators = new HashSet<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);


    @Override
    public void run(String... args) throws Exception {
        executorService
                .scheduleAtFixedRate(this::listIndicators, 0, 5, TimeUnit.SECONDS);
    }

    private void listIndicators() {
        try {
            ListRegisteredIndicatorRs listIndicators =
                    withTimer("Get indicators", indicatorClient::getIndicators);

            listIndicators.indicators().stream()
                    .map(RegisterRs::uuid)
                    .filter(Predicate.not(registeredIndicators::contains))
                    .forEach(this::startPollIndicator);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void startPollIndicator(UUID uuid) {
        log.info("Discovered new indicator - [{}]", uuid);
        registeredIndicators.add(uuid);

        executorService
                .scheduleAtFixedRate(() -> pollIndicator(uuid),0, 2, TimeUnit.SECONDS);
    }

    private void pollIndicator(UUID uuid) {
        try {
            ListRecordedMetricsRs metricsRs =
                    withTimer(String.format("[%s] get metrics", uuid),
                            () -> indicatorClient.getIndicatorMetrics(uuid));

            List<MetricsRs> metrics = metricsRs.metrics();
            MetricsRs firstMetric = metrics.get(0);
            log.info("[{}] amount metrics = {}", firstMetric.indicatorType(), metrics.size());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
