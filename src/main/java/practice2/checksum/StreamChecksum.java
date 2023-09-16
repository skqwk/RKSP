package practice2.checksum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StreamChecksum implements Checksum {

    public int calculate(String path) {
        File file = new File(path);
        return calculateFromFile(file);
    }

    private int calculateFromFile(File file) {
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            return sum(fileInputStream);
        } catch (IOException e) {
            System.out.println("File not found, return 0");
        }
        return 0;
    }

    private int sum(FileInputStream inputStream) throws IOException {
        int sum = 0;
        byte[] bytes = inputStream.readAllBytes();

        for (byte b : bytes) {
            if ((sum & 1) != 0)
                sum = (sum >> 1) + 0x8000;
            else
                sum >>= 1;
            sum += b & 0xff;
            sum &= 0xffff;

        }
        return sum;
    }
}
