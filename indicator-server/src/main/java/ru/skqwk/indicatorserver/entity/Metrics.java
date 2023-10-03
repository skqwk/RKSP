package ru.skqwk.indicatorserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_METRICS")
public class Metrics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    /**
     * Индикатор
     */
    @ManyToOne
    private Indicator indicator;

    /**
     * Тип метрики
     */
    private String type;

    /**
     * Значение метрики
     */
    private String value;

    /**
     * Время сохранения
     */
    private Instant recordedAt;
}
