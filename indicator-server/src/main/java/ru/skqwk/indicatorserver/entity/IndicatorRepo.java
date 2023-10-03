package ru.skqwk.indicatorserver.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndicatorRepo extends JpaRepository<Indicator, UUID> {
}
