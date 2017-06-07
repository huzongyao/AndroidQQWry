package com.hzy.qqwry;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class WryInputStream extends PushbackInputStream {

    public WryInputStream(InputStream in, int size) {
        super(in, size);
    }

    public void decodeStream(int key) {
        try {
            if (in.read(buf) == buf.length) {
                int length = buf.length;
                for (int i = 0; i < length; ++i) {
                    key = (key * 0x805 + 1) & 0xff;
                    buf[i] ^= key;
                }
                pos = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
