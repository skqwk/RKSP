package practice1.common;

import common.RandomUtil;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

public class ByteGenerator {
    private static final int MAX_BYTE = 128;
    public static byte[] generate(long limit) {
        return IntStream.generate(() -> RandomUtil.randomInt(MAX_BYTE))
                .limit(limit)
                .collect(ByteArrayOutputStream::new, (baos, i) -> baos.write((byte) i),
                        (baos1, baos2) -> baos1.write(baos2.toByteArray(), 0, baos2.size()))
                .toByteArray();
    }
}
