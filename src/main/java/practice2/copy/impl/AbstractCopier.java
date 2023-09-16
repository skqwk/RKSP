package practice2.copy.impl;

import practice2.copy.Copier;

public abstract class AbstractCopier implements Copier {
    String getCopiedName(String name) {
        return name + getPostfix() + ".txt";
    }
}
