package practice3.files;

public class File {
    private final FileType type;
    private final int size;
    private final int number;

    public File(FileType type, int size, int number) {
        this.type = type;
        this.size = size;
        this.number = number;
    }

    public FileType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }
}
