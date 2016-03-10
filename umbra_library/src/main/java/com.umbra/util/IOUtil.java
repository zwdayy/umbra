package com.umbra.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangweiding on 15/9/21.
 */
public class IOUtil {

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 输入流转成字节数组
     *
     * @param instream
     * @return
     * @throws Exception
     */
    public static byte[] toByteArray(final InputStream instream)
            throws IOException {
        int i = instream.available();
        byte[] temp = new byte[i];
        try {
            int bytesRead = 0;
            int offset = 0;
            while (bytesRead != -1 && offset < i) {
                bytesRead = instream.read(temp, offset, i - offset);
                offset += bytesRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            instream.close();
        }
        return temp;
    }

}
