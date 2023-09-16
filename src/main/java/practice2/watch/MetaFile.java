package practice2.watch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MetaFile {
    private final String path;
    private final int checksum;

    private List<String> cachedContent;

    public MetaFile(String path, int checksum) {
        this.path = path;
        this.checksum = checksum;
        try {
            this.cachedContent = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            this.cachedContent = new ArrayList<>();
        }
    }

    public int getChecksum() {
        return checksum;
    }

    public long getKbSize() {
        long bytes = Path.of(path).toFile().length();
        return (bytes + 1023) / 1024;
    }

    public String getName() {
        return path;
    }

    public List<String> getCachedContent() {
        return cachedContent;
    }
}
