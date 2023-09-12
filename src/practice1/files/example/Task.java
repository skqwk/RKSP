package practice1.files.example;

import java.io.File;

public class Task {
    private final String name;
    private final File file;

    public Task(String name,
                File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }
}
