package practice1.files.example;

import practice1.common.ByteGenerator;
import practice1.common.Sleeper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Producer {
    private static final Random RANDOM = new Random();
    private static final String[] TYPES = {".xml", ".json", ".txt"};
    private static final int MIN_SIZE = 10;
    private static final int MAX_SIZE = 100;


    private static final String PATTERN_FORMAT = "dd-MM-yyyy hh-mm-ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
            .withZone(ZoneId.systemDefault());

    Task produce() {
        try {
            Path path = Files.createFile(Path.of(createFilename()));
            Files.write(path, bytes());
            File file = path.toFile();
            Sleeper.sleep(1000);
            return new Task(Instant.now().toString(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private byte[] bytes() {
        return ByteGenerator.generate(RANDOM.nextLong(MIN_SIZE, MAX_SIZE));
    }

    private String createFilename() {
        int type = RANDOM.nextInt(TYPES.length);
        String typeName = TYPES[type];

        return FORMATTER.format(Instant.now()) + typeName;
    }
}
