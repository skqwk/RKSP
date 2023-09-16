package practice2.copy.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamCopier extends AbstractCopier {
    @Override
    public void copy(String name) {
        try(FileInputStream fileInputStream = new FileInputStream(name);
            FileOutputStream fileOutputStream = new FileOutputStream(getCopiedName(name))) {
            fileOutputStream.write(fileInputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPostfix() {
        return "_STREAM";
    }
}
