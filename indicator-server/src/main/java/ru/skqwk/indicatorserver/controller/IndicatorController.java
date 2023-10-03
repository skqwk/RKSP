package ru.skqwk.indicatorserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skqwk.indicatorserver.service.IndicatorService;
import ru.skqwk.indicatorserver.dto.ListRecordedMetricsRs;
import ru.skqwk.indicatorserver.dto.ListRegisteredIndicatorRs;
import ru.skqwk.indicatorserver.dto.MetricsRq;
import ru.skqwk.indicatorserver.dto.MetricsRs;
import ru.skqwk.indicatorserver.dto.RecordedMetricsRs;
import ru.skqwk.indicatorserver.dto.RegisterRs;
import ru.skqwk.indicatorserver.entity.Indicator;
import ru.skqwk.indicatorserver.entity.Metrics;
import ru.skqwk.indicatorserver.util.Formatter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IndicatorController {
    private final IndicatorService indicatorService;

    @PostMapping("/register/{type}")
    public RegisterRs register(@PathVariable("type") String type) {
        Indicator registered = indicatorService.register(type);
        return toRegisterRs(registered);
    }

    @PostMapping("/indicate/{uuid}")
    public RecordedMetricsRs indicate(@PathVariable("uuid") UUID indicatorUuid,
                                      @RequestBody MetricsRq metricsRq) {
        Metrics saved = indicatorService.save(indicatorUuid, metricsRq.type(), metricsRq.value());
        return new RecordedMetricsRs(saved.getUuid(),
                Formatter.format(saved.getRecordedAt()));
    }

    @GetMapping("indicators")
    public ListRegisteredIndicatorRs listRegisteredIndicators() {
        List<RegisterRs> registerRsList = indicatorService.getAllIndicators().stream()
                .map(this::toRegisterRs).collect(Collectors.toList());
        return new ListRegisteredIndicatorRs(registerRsList);
    }

    @GetMapping("/indicators/type/{type}")
    public ListRecordedMetricsRs metrics(@PathVariable("type") String type) {
        List<MetricsRs> metricsRsList = indicatorService.getMetricsByIndicatorType(type).stream()
                .map(this::toMetricsRs).collect(Collectors.toList());

        return new ListRecordedMetricsRs(metricsRsList);
    }

    @GetMapping("/indicators/{uuid}")
    public ListRecordedMetricsRs metricsByIndicator(@PathVariable("uuid") UUID indicatorUuid) {
        List<MetricsRs> metricsRsList = indicatorService.getMetricsByIndicatorUuid(indicatorUuid).stream()
                .map(this::toMetricsRs).collect(Collectors.toList());

        return new ListRecordedMetricsRs(metricsRsList);
    }
    private RegisterRs toRegisterRs(Indicator registered) {
        return new RegisterRs(registered.getUuid(),
                registered.getType(),
                Formatter.format(registered.getRegisteredAt()));
    }

    private MetricsRs toMetricsRs(Metrics metrics) {
        return new MetricsRs(metrics.getUuid(),
                metrics.getIndicator().getType(),
                metrics.getType(),
                metrics.getValue(),
                Formatter.format(metrics.getRecordedAt()));
    }
}
