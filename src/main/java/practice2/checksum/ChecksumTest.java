package practice2.checksum;

public class ChecksumTest {
    public static void main(String[] args) {
        StreamChecksum checksum = new StreamChecksum();
        System.out.println(checksum.calculate("file.txt"));
    }
}
