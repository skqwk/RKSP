package com.example.rsocketserver.controller;

import com.example.rsocketcommon.AuthRq;
import com.example.rsocketcommon.LoginRs;
import com.example.rsocketcommon.MessageRq;
import com.example.rsocketcommon.MessageRs;
import com.example.rsocketserver.entity.Message;
import com.example.rsocketserver.entity.MessageRepo;
import com.example.rsocketserver.service.ChannelService;
import com.example.rsocketserver.util.Formatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChannelController {
    private final Set<String> nowUsers = new HashSet<>();
    private final ChannelService channelService;

    @MessageMapping("login")
    Mono<LoginRs> login(final AuthRq authRq) {
        if (nowUsers.contains(authRq.author())) {
            log.warn("User [{}] already logged in", authRq.author());
            return Mono.empty();
        }

        Mono<LoginRs> login = channelService.login(authRq.author())
                .map(message -> new LoginRs(String.format("Hello, %s", authRq.author())));
        nowUsers.add(authRq.author());
        return login;
    }

    @MessageMapping("logout")
    Mono<Void> logout(final AuthRq authRq) {
        if (!nowUsers.contains(authRq.author())) {
            log.warn("User [{}] wasn't log in", authRq.author());
            return Mono.empty();
        }

        Mono<Void> empty = channelService.logout(authRq.author());
        nowUsers.remove(authRq.author());
        return empty;
    }

    @MessageMapping("listen")
    public Flux<MessageRs> listen(final MessageRq messageRq) {
        return channelService.listen(toEntity(messageRq))
                .map(this::toResponse);
    }

    @MessageMapping("join")
    public Flux<MessageRs> join(final Flux<MessageRq> messages) {
        return channelService.join(messages.map(this::toEntity))
                .map(this::toResponse);
    }

    @MessageMapping("list")
    public Flux<String> list() {
        return channelService.list();
    }

    private Message toEntity(MessageRq messageRq) {
        return Message.builder()
                .channel(messageRq.channel())
                .author(messageRq.author())
                .createdAt(Instant.now())
                .body(messageRq.body())
                .build();
    }

    private MessageRs toResponse(Message message) {
        return MessageRs.builder()
                .author(message.getAuthor())
                .body(message.getBody())
                .createdAt(Formatter.format(message.getCreatedAt()))
                .build();
    }
}
