package ru.skqwk.indicatorclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.skqwk.indicatorclient.util.Timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("consumer")
@RequiredArgsConstructor
public class ConsumerCommandLineClient implements CommandLineRunner {
    private final MetricsClient metricsClient;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Override
    public void run(String... args) {
        executorService
                .scheduleAtFixedRate(this::pollMetrics, 0, 5, TimeUnit.SECONDS);
    }

    private void pollMetrics() {
        Timer.withTimer("Опрос метрик", () -> {
            try {
                metricsClient.getMetrics();
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        });
    }
}
