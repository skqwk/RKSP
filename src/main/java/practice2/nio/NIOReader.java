package practice2.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NIOReader {
    public static void main(String[] args) throws IOException {
        System.out.println(String.join("\n", Files.readAllLines(Paths.get("file.txt"))));
    }
}
