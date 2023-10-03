package com.example.rsocketserver.service;

import com.example.rsocketserver.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChannelService {
    /**
     * Зайти в систему
     *
     * @param author логин пользователя
     *
     * @return сообщение об успешном входе
     */
    Mono<Message> login(String author);

    /**
     * Выйти из системы
     *
     * @param author логин пользователя
     */
    Mono<Void> logout(String author);

    /**
     * Подписаться на прослушивание канала
     *
     * @param message сообщение на прослушивание канала
     *
     * @return поток сообщений из канала
     */
    Flux<Message> listen(Message message);

    /**
     * Получить список доступных каналов
     *
     * @return названия доступных каналов
     */
    Flux<String> list();

    /**
     * Присоединиться к каналу, получить возможность
     * отправлять и получать поток сообщений
     *
     * @param messages входящий поток сообщений
     *
     * @return выходящий поток сообщений
     */
    Flux<Message> join(Flux<Message> messages);
}
