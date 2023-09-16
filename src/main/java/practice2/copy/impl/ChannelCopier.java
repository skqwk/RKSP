package practice2.copy.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ChannelCopier extends AbstractCopier {
    @Override
    public void copy(String name) {
        try(FileInputStream fileInputStream = new FileInputStream(name);
            FileOutputStream fileOutputStream = new FileOutputStream(getCopiedName(name));
                FileChannel inFileChannel = fileInputStream.getChannel();
            FileChannel outFileChannel = fileOutputStream.getChannel()){
            long count = inFileChannel.size();
            while(count > 0) {
                long transferred = inFileChannel.transferTo(inFileChannel.position(), count, outFileChannel);
                inFileChannel.position(inFileChannel.position() + transferred);
                count -= transferred;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPostfix() {
        return "_CHANNEL";
    }
}
