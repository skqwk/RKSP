package practice2.copy.impl;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ApacheCopier extends AbstractCopier {
    @Override
    public void copy(String name) {
        try {
            FileUtils.copyFile(new File(name), new File(getCopiedName(name)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPostfix() {
        return "_APACHE";
    }
}
