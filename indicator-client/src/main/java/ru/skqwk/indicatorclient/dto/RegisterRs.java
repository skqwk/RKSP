package ru.skqwk.indicatorclient.dto;

import java.util.UUID;

public record RegisterRs(UUID uuid,
                         String type,
                         String registeredAt) {
}
