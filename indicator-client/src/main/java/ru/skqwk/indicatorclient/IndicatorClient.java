package ru.skqwk.indicatorclient;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.skqwk.indicatorclient.dto.ListRecordedMetricsRs;
import ru.skqwk.indicatorclient.dto.ListRegisteredIndicatorRs;
import ru.skqwk.indicatorclient.dto.MetricsRq;
import ru.skqwk.indicatorclient.dto.MetricsRs;
import ru.skqwk.indicatorclient.dto.RegisterRs;

import java.util.UUID;

@Component
public class IndicatorClient {
    private static final String BASE_URL = "http://localhost:8080";

    private final RestTemplate restTemplate = new RestTemplate();

    public ListRegisteredIndicatorRs getIndicators() {
        return restTemplate.getForObject(
                BASE_URL + "/indicators",
                ListRegisteredIndicatorRs.class);
    }

    public ListRecordedMetricsRs getIndicatorMetrics(UUID uuid) {
        return restTemplate.getForObject(
                BASE_URL + "/indicators/{uuid}",
                ListRecordedMetricsRs.class,
                uuid);
    }

    public RegisterRs register(String type) {
        return restTemplate.postForEntity(
                BASE_URL + "/register/{type}",
                        null,
                        RegisterRs.class,
                        type)
                .getBody();
    }

    public MetricsRs indicate(UUID uuid, MetricsRq metricsRq) {
        return restTemplate.postForEntity(
                        BASE_URL + "/indicate/{uuid}",
                        metricsRq,
                        MetricsRs.class,
                        uuid)
                .getBody();
    }
}
