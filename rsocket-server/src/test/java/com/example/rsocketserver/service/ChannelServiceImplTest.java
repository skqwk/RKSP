package com.example.rsocketserver.service;

import com.example.rsocketserver.entity.Message;
import com.example.rsocketserver.entity.MessageRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceImplTest {
    private static final String AUTHOR_BOB = "bob";
    private static final String AUTHOR_ALICE = "alice";
    private static final String SESSION_CHANNEL = "session";
    private static final String LOGGED_IN_MESSAGE = "logged in";
    private static final String LOGGED_OUT_MESSAGE = "logged out";

    @Mock
    private MessageRepo messageRepo;

    @InjectMocks
    private ChannelServiceImpl channelService;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;


    @Test
    void login() {
        // GIVEN
        Message expectedMessage = mock(Message.class);
        when(messageRepo.save(messageArgumentCaptor.capture()))
                .thenReturn(Mono.just(expectedMessage));

        // WHEN
        Mono<Message> actualResult = channelService.login(AUTHOR_BOB);

        // THEN
        Message toSave = messageArgumentCaptor.getValue();
        assertEquals(AUTHOR_BOB, toSave.getAuthor());
        assertEquals(SESSION_CHANNEL, toSave.getChannel());
        assertEquals(LOGGED_IN_MESSAGE, toSave.getBody());
        assertNotNull(toSave.getCreatedAt());
        assertSame(expectedMessage, actualResult.block());
    }

    @Test
    void logout() {
        // GIVEN
        Message expectedMessage = mock(Message.class);
        when(messageRepo.save(messageArgumentCaptor.capture()))
                .thenReturn(Mono.just(expectedMessage));

        // WHEN
        Mono<Void> actualResult = channelService.logout(AUTHOR_BOB);

        // THEN
        assertNotNull(actualResult);
        Message toSave = messageArgumentCaptor.getValue();
        assertEquals(AUTHOR_BOB, toSave.getAuthor());
        assertEquals(SESSION_CHANNEL, toSave.getChannel());
        assertEquals(LOGGED_OUT_MESSAGE, toSave.getBody());
        assertNotNull(toSave.getCreatedAt());
    }

    @ParameterizedTest
    @MethodSource("listenLastDataProvider")
    void listenLast(String body,
                    String channel,
                    int expectedLimit) {
        // GIVEN
        Flux<Message> expectedMessages = Flux.just(createMessage(body, channel, AUTHOR_BOB));
        when(messageRepo.getLastInChannel(expectedLimit, channel))
                .thenReturn(expectedMessages);

        Message message = createMessage(body, channel, AUTHOR_BOB);
        when(messageRepo.save(message)).thenReturn(Mono.just(message));

        // WHEN | THEN
        assertSame(expectedMessages, channelService.listen(message));
    }

    private static Stream<Arguments> listenLastDataProvider() {
        return Stream.of(
                Arguments.of("listen last 10", "channel", 10),
                Arguments.of("listen last 5", "channel", 5)
        );
    }

    @ParameterizedTest
    @MethodSource("listenFirstDataProvider")
    void listenFirst(String body,
                     String channel,
                     int expectedLimit) {
        // GIVEN
        Flux<Message> expectedMessages = Flux.just(mock(Message.class));
        when(messageRepo.getFirstInChannel(expectedLimit, channel))
                .thenReturn(expectedMessages);

        Message message = createMessage(body, channel, AUTHOR_BOB);
        when(messageRepo.save(message)).thenReturn(Mono.just(message));

        // WHEN | THEN
        assertSame(expectedMessages, channelService.listen(message));
    }

    private static Stream<Arguments> listenFirstDataProvider() {
        return Stream.of(
                Arguments.of("listen first 10", "channel", 10),
                Arguments.of("listen first 5", "channel", 5)
        );
    }

    private static Message createMessage(String body, String channel, String author) {
        return Message.builder()
                .author(author)
                .channel(channel)
                .body(body)
                .build();
    }

    @Test
    void list() {
        // GIVEN
        Flux<String> expectedChannels = Flux.just("channel1", "channel2");
        when(messageRepo.getUniqueChannels())
                .thenReturn(expectedChannels);

        // WHEN | THEN
        assertSame(expectedChannels, channelService.list());
    }

    @Test
    void join() throws InterruptedException {
        // GIVEN
        Flux<Message> bobMessages = Flux.just(
                createMessage("1", "channel", AUTHOR_BOB),
                createMessage("2", "channel", AUTHOR_BOB),
                createMessage("3", "channel", AUTHOR_BOB)
        );

        Flux<Message> aliceMessages = Flux.just(
                createMessage("1", "channel", AUTHOR_ALICE),
                createMessage("2", "channel", AUTHOR_ALICE),
                createMessage("3", "channel", AUTHOR_ALICE)
        );

        when(messageRepo.save(any()))
                .thenReturn(Mono.just(createMessage("stub", "stub", "stub")));

        // WHEN | THEN
        CountDownLatch finish = new CountDownLatch(2);

        CompletableFuture.supplyAsync(() -> channelService.join(bobMessages))
                .thenAccept(messageFlux -> {
                    messageFlux.subscribe(message -> {
                        System.out.printf("Bob get [%s] from [%s]\n", message.getBody(), message.getAuthor());
                    });
                    System.out.println("BOB END");
                    finish.countDown();
                });

        CompletableFuture.supplyAsync(() -> channelService.join(aliceMessages))
                .thenAccept(messageFlux -> {
                    messageFlux.subscribe(message -> {
                        System.out.printf("Alice get [%s] from [%s]\n", message.getBody(), message.getAuthor());
                    });
                    System.out.println("ALICE END");
                    finish.countDown();
                });

        finish.await();
    }
}