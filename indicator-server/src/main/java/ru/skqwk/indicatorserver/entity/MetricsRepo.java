package ru.skqwk.indicatorserver.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MetricsRepo extends JpaRepository<Metrics, UUID> {
    List<Metrics> findAllByIndicator_Uuid(UUID indicatorUuid);
    List<Metrics> findAllByIndicator_Type(String type);
}
