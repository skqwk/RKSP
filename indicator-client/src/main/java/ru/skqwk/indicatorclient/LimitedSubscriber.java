package ru.skqwk.indicatorclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.lang.NonNull;
import reactor.core.publisher.BaseSubscriber;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class LimitedSubscriber<T> extends BaseSubscriber<T> {
    private final Consumer<T> consumer;
    private final long limit;
    private int processed = 0;

    @Override
    protected void hookOnSubscribe(@NonNull Subscription subscription) {
        request(limit);
    }

    @Override
    protected void hookOnNext(@NonNull T value) {
        processed++;
        consumer.accept(value);
        if (processed == limit) {
            processed = 0;
            log.info("Запрос ещё {} значений типа {}", limit, value.getClass().getSimpleName());
            request(limit);
        }
    }
}
