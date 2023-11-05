package ru.skqwk.indicatorserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.skqwk.indicatorserver.entity.IndicatorType;
import ru.skqwk.indicatorserver.entity.Metrics;
import ru.skqwk.indicatorserver.entity.MetricsRepo;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RxMetricsServiceTest {
    private static final UUID METRICS_ID = UUID.randomUUID();
    private static final String VALID_TYPE = IndicatorType.DIODE.name();
    private static final String INVALID_TYPE = "invalid";
    private static final String INDICATOR = "indicator";
    @Mock
    private MetricsRepo metricsRepo;

    @InjectMocks
    private RxMetricsServiceImpl metricsService;

    @Captor
    private ArgumentCaptor<Metrics> metricsArgumentCaptor;


    @Test
    void saveWithInvalidType() {
        StepVerifier.create(metricsService.save(null, INVALID_TYPE, null))
                .expectError()
                .verify();
    }

    @ParameterizedTest
    @MethodSource("saveWithInvalidValueDataProvider")
    void saveWithInvalidValue(IndicatorType type, String value) {
        StepVerifier.create(metricsService.save(null, type.name(), value))
                .expectError()
                .verify();
    }

    private static Stream<Arguments> saveWithInvalidValueDataProvider() {
        return Stream.of(
                Arguments.of(IndicatorType.LAMP, "39"),
                Arguments.of(IndicatorType.LAMP, "101"),
                Arguments.of(IndicatorType.DIODE, "19"),
                Arguments.of(IndicatorType.DIODE, "81"),
                Arguments.of(IndicatorType.BAROMETER, "0"),
                Arguments.of(IndicatorType.BAROMETER, "6"),
                Arguments.of(IndicatorType.THERMOMETER, "-51"),
                Arguments.of(IndicatorType.THERMOMETER, "51")
        );
    }

    @ParameterizedTest
    @MethodSource("saveDataProvider")
    void save(IndicatorType type, String value) {
        // GIVEN
        Metrics mocked = Metrics.builder().build();

        when(metricsRepo.save(metricsArgumentCaptor.capture()))
                .thenReturn(Mono.just(mocked));

        // WHEN | THEN
        StepVerifier.create(metricsService.save(INDICATOR, type.name(), value))
                .expectNext(mocked)
                .verifyComplete();

        Metrics metrics = metricsArgumentCaptor.getValue();
        assertEquals(value, metrics.getValue());
        assertEquals(INDICATOR, metrics.getIndicator());
        assertEquals(type.name(), metrics.getType());
    }

    private static Stream<Arguments> saveDataProvider() {
        return Stream.of(
                Arguments.of(IndicatorType.LAMP, "40"),
                Arguments.of(IndicatorType.LAMP, "100"),
                Arguments.of(IndicatorType.DIODE, "20"),
                Arguments.of(IndicatorType.DIODE, "80"),
                Arguments.of(IndicatorType.BAROMETER, "1"),
                Arguments.of(IndicatorType.BAROMETER, "5"),
                Arguments.of(IndicatorType.THERMOMETER, "-50"),
                Arguments.of(IndicatorType.THERMOMETER, "50")
        );
    }

    @Test
    void getMetricsByInvalidType() {
        StepVerifier.create(metricsService.getMetricsByType(INVALID_TYPE))
                .expectError()
                .verify();
    }

    @Test
    void getMetricsByValidType() {
        // GIVEN
        Metrics metrics1 = Metrics.builder().build();
        Metrics metrics2 = Metrics.builder().build();

        when(metricsRepo.findMetricsByType(VALID_TYPE))
                .thenReturn(Flux.just(metrics1, metrics2));

        // WHEN | THEN
        StepVerifier.create(metricsService.getMetricsByType(VALID_TYPE))
                .expectNext(metrics1)
                .expectNext(metrics2)
                .expectComplete()
                .verify();
    }

    @Test
    void getMetricsByIndicatorName() {
        // GIVEN
        String indicatorName = "indicatorName";
        String anotherName = "anotherName";

        Metrics metrics1 = Metrics.builder()
                .indicator(indicatorName)
                .build();
        Metrics metrics2 = Metrics.builder()
                .indicator(anotherName)
                .build();

        when(metricsRepo.findAll())
                .thenReturn(Flux.just(metrics1, metrics2));

        // WHEN | THEN
        StepVerifier.create(metricsService.getMetricsByIndicatorName(indicatorName))
                .expectNext(metrics1)
                .expectComplete()
                .verify();
    }

    @Test
    void getAllMetrics() {
        // GIVEN
        Metrics metrics1 = Metrics.builder().build();
        Metrics metrics2 = Metrics.builder().build();

        when(metricsRepo.findAll())
                .thenReturn(Flux.just(metrics1, metrics2));

        // WHEN | THEN
        StepVerifier.create(metricsService.getAllMetrics())
                .expectNext(metrics1)
                .expectNext(metrics2)
                .expectComplete()
                .verify();
    }

    @Test
    void getMetricsByIdWhenMetricsNotFound() {
        // GIVEN
        when(metricsRepo.findById(METRICS_ID))
                .thenReturn(Mono.empty());

        // WHEN | THEN
        StepVerifier.create(metricsService.getMetricsById(METRICS_ID))
                .expectComplete()
                .verify();
    }

    @Test
    void getMetricsByIdWhenMetricsFound() {
        // GIVEN
        Metrics metrics = Metrics.builder().build();

        when(metricsRepo.findById(METRICS_ID))
                .thenReturn(Mono.just(metrics));

        // WHEN | THEN
        StepVerifier.create(metricsService.getMetricsById(METRICS_ID))
                .expectNext(metrics)
                .expectComplete()
                .verify();
    }
}