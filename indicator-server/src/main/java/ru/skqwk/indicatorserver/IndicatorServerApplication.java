package ru.skqwk.indicatorserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.skqwk.indicatorserver.entity.MetricsRepo;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class IndicatorServerApplication implements CommandLineRunner {
    private final MetricsRepo metricsRepo;


    public static void main(String[] args) {
        SpringApplication.run(IndicatorServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Запуск приложения...");
        metricsRepo.findAll().subscribe(System.out::println);
    }
}
