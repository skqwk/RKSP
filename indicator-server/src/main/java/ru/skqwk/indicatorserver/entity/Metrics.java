package ru.skqwk.indicatorserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_METRICS")
public class Metrics {
    @Id
    @Column("C_ID")
    private UUID id;

    /**
     * Индикатор
     */
    @Column("C_INDICATOR")
    private String indicator;

    /**
     * Тип метрики
     */
    @Column("C_TYPE")
    private String type;

    /**
     * Значение метрики
     */
    @Column("C_VALUE")
    private String value;

    /**
     * Время сохранения
     */
    @Column("C_RECORDED_AT")
    private Instant recordedAt;
}
