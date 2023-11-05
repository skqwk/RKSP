package ru.skqwk.indicatorclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.skqwk.indicatorclient.dto.MetricsRs;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsClient {
    private static final int LIMIT = 1;

    private final WebClient webClient;

    public void getMetrics() {
        webClient.get()
                .uri("/metrics").retrieve()
                .bodyToFlux(MetricsRs.class)
                .subscribe(new LimitedSubscriber<>(this::logMetrics, LIMIT));
    }

    private void logMetrics(MetricsRs metricsRs) {
        log.info("Получена метрика {} индикатора {} - {}",
                metricsRs.type(), metricsRs.indicatorName(), metricsRs.value());
    }

}
