package com.example.rsocketserver.entity;


import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@Table("T_MESSAGE")
@RequiredArgsConstructor
public class Message {
    @Id
    private final UUID uuid;
    private final String body;
    private final String author;
    private final Instant createdAt;
    private final String channel;
}
