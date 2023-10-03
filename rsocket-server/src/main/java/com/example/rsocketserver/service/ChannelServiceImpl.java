package com.example.rsocketserver.service;

import com.example.rsocketserver.entity.Message;
import com.example.rsocketserver.entity.MessageRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final MessageRepo messageRepo;

    private static final String SESSION_CHANNEL = "session";
    private static final String LOGOUT_MESSAGE = "logged out";
    private static final String LOGIN_MESSAGE = "logged in";

    private final Sinks.Many<Message> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Message> login(String author) {
        log.info("New user [{}] logged in", author);
        Message loginMessage = createMessage(author, SESSION_CHANNEL, LOGIN_MESSAGE);
        return messageRepo.save(loginMessage);
    }

    @Override
    public Mono<Void> logout(String author) {
        log.info("User [{}] logged out", author);
        Message logoutMessage = createMessage(author, SESSION_CHANNEL, LOGOUT_MESSAGE);
        saveMessage(logoutMessage);
        return Mono.empty();
    }

    @Override
    public Flux<Message> listen(Message message) {
        log.info("User [{}] listen to channel [{}]",
                message.getAuthor(), message.getChannel());

        saveMessage(message);

        String[] values = message.getBody().split(" ");
        String lastOrFirst = values[1];
        int limit = Integer.parseInt(values[2]);

        if (lastOrFirst.equals("last")) {
            return messageRepo.getLastInChannel(limit, message.getChannel());
        } else if (lastOrFirst.equals("first")) {
            return messageRepo.getFirstInChannel(limit, message.getChannel());
        }
        return Flux.empty();
    }

    @Override
    public Flux<String> list() {
        return messageRepo.getUniqueChannels();
    }

    @Override
    public Flux<Message> join(Flux<Message> messages) {
        Flux<Message> shared = messages.share();

        Flux.from(shared).subscribe(this::send);

        return Flux.from(shared).take(1)
                .switchMap(first -> sink.asFlux()
                        .filter(m -> m.getChannel().equals(first.getChannel()) &&
                                !m.getAuthor().equals(first.getAuthor()))
                        .log()
                        .doOnComplete(() -> log.info("Leave channel"))
                );
    }

    private Mono<Void> send(Message message) {
        log.info("Resend [{}] message [{}] to channel [{}]",
                message.getAuthor(), message.getBody(), message.getChannel());
        saveMessage(message);
        return Mono.just(sink.tryEmitNext(message))
                .then();
    }

    private void saveMessage(Message message) {
        messageRepo.save(message)
                .subscribe(saved -> log.info("User [{}] add message [{}] to channel [{}]",
                        saved.getAuthor(), saved.getBody(), saved.getChannel())
                );
    }

    private Message createMessage(String author,
                                  String channel,
                                  String body) {
        return Message.builder()
                .author(author)
                .channel(channel)
                .body(body)
                .createdAt(Instant.now())
                .build();
    }
}
