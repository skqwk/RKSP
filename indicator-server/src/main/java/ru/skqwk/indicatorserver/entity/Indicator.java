package ru.skqwk.indicatorserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "T_INDICATOR")
public class Indicator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    /**
     * Тип индикатора
     */
    private String type;

    /**
     * Время регистрации индикатора
     */
    private Instant registeredAt;

    /**
     * Метрики
     */
    @OneToMany
    private List<Metrics> metrics;
}
