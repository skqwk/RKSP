package practice2.copy.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesCopier extends AbstractCopier {
    @Override
    public void copy(String name) {
        try {
            Files.copy(Path.of(name), Path.of(getCopiedName(name)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPostfix() {
        return "_FILES";
    }
}
