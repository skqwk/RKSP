package com.example.rsocketclient.cmd;

import com.example.rsocketcommon.AuthRq;
import com.example.rsocketcommon.LoginRs;
import com.example.rsocketcommon.MessageRq;
import com.example.rsocketcommon.MessageRs;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class CommandLineClientV2 implements CommandLineRunner {
    //<editor-fold desc="Routes">
    private static final String LOGIN_ROUTE = "login";
    private static final String LOGOUT_ROUTE = "logout";
    private static final String JOIN_ROUTE = "join";
    private static final String LISTEN_ROUTE = "listen";
    private static final String LIST_ROUTE = "list";
    //</editor-fold>

    //<editor-fold desc="Arguments">
    private static final String NAME_ARG = "name";
    private static final String LIMIT_ARG = "limit";
    private static final String LAST_OR_FIRST_ARG = "lastOrFirst";
    private static final String CHANNEL_ARG = "channel";
    //</editor-fold>

    private static final String STOP_COMMAND = "stop";

    private final Scanner scanner = new Scanner(System.in);
    private String author;
    private final RSocketRequester socketRequester;

    private final Map<String, Consumer<Params>> commands = Map.of(
            LOGIN_ROUTE, this::login,
            LOGOUT_ROUTE, this::logout,
            LISTEN_ROUTE, this::listen,
            JOIN_ROUTE, this::join,
            LIST_ROUTE, this::list
    );

    private final Map<String, Function<String[], Params>> argProcessors = Map.of(
            LOGIN_ROUTE, this::processLoginArgs,
            LOGOUT_ROUTE, this::processEmptyArgs,
            LIST_ROUTE, this::processEmptyArgs,
            LISTEN_ROUTE, this::processListenArgs,
            JOIN_ROUTE, this::processJoinArgs
    );

    public CommandLineClientV2(@Qualifier("rSocketStrategies") RSocketStrategies strategies) {
        socketRequester = RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .tcp("localhost", 7000);
    }

    @Override
    public void run(String... args) throws Exception {
        String command = scanner.nextLine();
        while (!STOP_COMMAND.equals(command)) {
            processCommand(command);
            command = scanner.nextLine();
        }
        socketRequester.dispose();
    }

    private void processCommand(String command) {
        String[] commandParts = command.split(" ");
        String method = commandParts[0];

        if (command.contains(method)) {
            Params params = argProcessors.get(method)
                    .apply(commandParts);

            commands.get(method).accept(params);
        } else {
            log.warn("Not found command - {}", method);
        }
    }

    private void login(Params params) {
        String name = params.get(NAME_ARG);
        if (StringUtils.isBlank(name)) {
            log.warn("You can't login with empty name");
            return;
        }

        this.author = name;

        socketRequester.route(LOGIN_ROUTE)
                .data(new AuthRq(name))
                .retrieveMono(LoginRs.class)
                .subscribe(rs -> System.out.println(rs.response()));
    }

    private Params processLoginArgs(String[] args) {
        return Params.builder()
                .param(NAME_ARG, args[1])
                .build();
    }

    private void logout(Params params) {
        if (this.author == null) {
            log.warn("You can't logout before login");
            return;
        }

        socketRequester.route(LOGOUT_ROUTE)
                .data(new AuthRq(this.author))
                .send()
                .doAfterTerminate(() -> this.author = null)
                .block();
    }

    private Params processEmptyArgs(String[] args) {
        return Params.empty();
    }

    public void listen(Params params) {
        String channel = params.get(CHANNEL_ARG);
        String lastOrFirst = params.get(LAST_OR_FIRST_ARG);
        Integer limit = params.get(LIMIT_ARG);

        if (this.author == null) {
            log.warn("You can't listen to channel before login");
            return;
        }

        socketRequester.route(LISTEN_ROUTE)
                .data(new MessageRq(author, String.format("listen %s %s", lastOrFirst, limit), channel))
                .retrieveFlux(MessageRs.class)
                .subscribe(this::printMessage);
    }

    private Params processListenArgs(String[] args) {
        return Params.builder()
                .param(CHANNEL_ARG, args[1])
                .param(LAST_OR_FIRST_ARG, args[2])
                .param(LIMIT_ARG, Integer.parseInt(args[3]))
                .build();
    }

    public void join(Params params) {
        if (this.author == null) {
            log.warn("You can't join to channel before login");
            return;
        }
        String channel  = params.get(CHANNEL_ARG);

        Sinks.Many<MessageRq> messageRqProducer = Sinks.many().unicast().onBackpressureBuffer();
        Flux<MessageRq> data = messageRqProducer.asFlux();


        CompletableFuture.runAsync(() -> {
            messageRqProducer.tryEmitNext(new MessageRq(this.author, "joined", channel));
            socketRequester.route(JOIN_ROUTE)
                    .data(data)
                    .retrieveFlux(MessageRs.class)
                    .subscribe(this::printMessage);
        });

        log.info("Welcome to channel [{}]. Send 'stop' to leave ", channel);
        String body = scanner.nextLine();
        while (!STOP_COMMAND.equals(body)) {
            messageRqProducer.tryEmitNext(new MessageRq(this.author, body, channel));
            body = scanner.nextLine();
        }
        messageRqProducer.tryEmitComplete();
    }

    private Params processJoinArgs(String[] args) {
        return Params.builder()
                .param(CHANNEL_ARG, args[1])
                .build();
    }

    private void list(Params params) {
        System.out.println("Available channels:");
        socketRequester.route(LIST_ROUTE)
                .retrieveFlux(String.class)
                .subscribe(System.out::println);
    }

    private void printMessage(MessageRs messageRs) {
        System.out.printf("[%s] %s: %s\n", messageRs.createdAt(), messageRs.author(), messageRs.body());
    }
}
