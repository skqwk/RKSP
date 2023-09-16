package practice2.copy;

import practice1.common.ByteGenerator;
import practice2.copy.impl.ApacheCopier;
import practice2.copy.impl.ChannelCopier;
import practice2.copy.impl.FilesCopier;
import practice2.copy.impl.StreamCopier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestCopy {
    private static final long MB_100 = 100 * 1000 * 1000;

    public static void main(String[] args) {
        String fileName = "test";

        witTimer(() -> generate(fileName), "Generate file");

        witTimer(() -> new ApacheCopier().copy(fileName), "Apache");
        witTimer(() -> new ChannelCopier().copy(fileName), "Channel");
        witTimer(() -> new FilesCopier().copy(fileName), "Files");
        witTimer(() -> new StreamCopier().copy(fileName), "Stream");
    }

    private static void generate(String fileName) {
        try {
            Files.write(Path.of(fileName), ByteGenerator.generate(MB_100));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void witTimer(Runnable job, String name) {
        long start = System.nanoTime();
        job.run();
        long finish = System.nanoTime();

        System.out.printf("%s: took %s ms\n", name, (finish - start) / 1_000_000);
    }
}
