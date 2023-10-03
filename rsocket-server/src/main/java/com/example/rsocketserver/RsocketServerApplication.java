package com.example.rsocketserver;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class RsocketServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(RsocketServerApplication.class, args);
    }
}
