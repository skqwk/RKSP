package com.example.rsocketserver.entity;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MessageRepo extends ReactiveCrudRepository<Message, UUID> {
    @Query("SELECT * FROM T_MESSAGE WHERE channel = :channel ORDER BY created_at DESC LIMIT :limit")
    Flux<Message> getLastInChannel(int limit, String channel);

    @Query("SELECT * FROM T_MESSAGE WHERE channel = :channel ORDER BY created_at LIMIT :limit")
    Flux<Message> getFirstInChannel(int limit, String channel);

    @Query("SELECT DISTINCT channel FROM t_message")
    Flux<String> getUniqueChannels();
}
