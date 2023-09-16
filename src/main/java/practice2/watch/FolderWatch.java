package practice2.watch;

import practice2.checksum.StreamChecksum;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderWatch {
    private final StreamChecksum checkSumCalculator = new StreamChecksum();

    public void watch(String dir) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(dir);
            Map<String, MetaFile> nameToFiles = initDirectory(path);

            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    String fileName = event.context().toString();
                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                        System.out.printf("CREATE %s\n", fileName);
                        nameToFiles = initDirectory(path);
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
                        System.out.printf("MODIFY %s\n", fileName);

                        MetaFile oldFile = nameToFiles.get(fileName);
                        MetaFile newFile = createFile(dir + "\\" + fileName);

                        printDiff(oldFile, newFile);
                        nameToFiles.put(fileName, newFile);
                    } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
                        MetaFile removed = nameToFiles.remove(fileName);
                        int checksum = removed.getChecksum();
                        long kbSize = removed.getKbSize();

                        System.out.printf("DELETE %s. Checksum: %s, size in Kb: %s\n", fileName, checksum, kbSize);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, MetaFile> initDirectory(Path dirPath) throws IOException {
        System.out.println("INIT DIRECTORY");

        try (Stream<Path> pathStream = Files.list(dirPath)) {
            Map<String, MetaFile> collected = pathStream
                    .collect(Collectors.toMap(path -> path.getFileName().toString(),
                            path -> createFile(path.toString())));
            for (MetaFile metaFile : collected.values()) {
                int checksum = metaFile.getChecksum();
                long kbSize = metaFile.getKbSize();
                String name = metaFile.getName();

                System.out.printf("INIT %s. Checksum: %s, size in Kb: %s\n", name, checksum, kbSize);
            }
            return collected;
        }
    }

    private void printDiff(MetaFile oldFile, MetaFile newFile) {
        List<String> oldLines = oldFile.getCachedContent();
        List<String> newLines = newFile.getCachedContent();

        int min = Math.min(oldLines.size(), newLines.size());

        for (int i = 0; i < min; i++) {
            String oldLine = oldLines.get(i);
            String newLine = newLines.get(i);

            if (!oldLine.equals(newLine)) {
                System.out.printf("Change line[%s] Old: %s New: %s\n", i, oldLine, newLine);
            }
        }

        int max = Math.max(oldLines.size(), newLines.size());
        List<String> biggest = oldLines.size() > newLines.size() ? oldLines : newLines;
        for (int i = min; i < max; i++) {
            String addedLine = biggest.get(i);
            System.out.printf("Add line[%s]: %s\n", i, addedLine);
        }
    }

    private MetaFile createFile(String path) {
        int calculate = checkSumCalculator.calculate(path);
        return new MetaFile(path, calculate);
    }
}
