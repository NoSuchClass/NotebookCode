package com.bitongchong.notebook.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author liuyuehe
 * @date 2020/4/21 18:34
 */
public class InputStreamToString {
    public static String parse(InputStream stream, int bufferCapacity) throws IOException {
        byte[] buffer = new byte[bufferCapacity];
        StringBuilder builder = new StringBuilder();
        int read = buffer.length;
        while(read == buffer.length) {
            read = stream.read(buffer);
            builder.append(new String(buffer, 0, read));
        }
        return builder.toString();
    }
}
