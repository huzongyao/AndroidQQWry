package com.hzy.qqwry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.InflaterInputStream;


public class DatDownloadUtil {

    public static void downloadQQWryDat(String filePath) {
        CopyWrite copyWrite = getCopyWrite();
        if (copyWrite.getKey() != null) {
            downloadDatWithKey(copyWrite.getKey(), filePath);
        }
    }

    public static CopyWrite getCopyWrite() {
        try {
            URL url = new URL("http://update.cz88.net/ip/copywrite.rar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            CopyWrite copyWrite = new CopyWrite();
            String string = readString(inputStream, 4);
            if (string != null) {
                copyWrite.setSign(string);
            }
            Integer integer = readInteger(inputStream);
            if (integer != null) {
                copyWrite.setVersion(integer);
            }
            integer = readInteger(inputStream);
            if (integer != null) {
                copyWrite.setUnknown1(integer);
            }
            integer = readInteger(inputStream);
            if (integer != null) {
                copyWrite.setSize(integer);
            }
            integer = readInteger(inputStream);
            if (integer != null) {
                copyWrite.setUnknown2(integer);
            }
            integer = readInteger(inputStream);
            if (integer != null) {
                copyWrite.setKey(integer);
            }
            string = readString(inputStream, 128);
            if (string != null) {
                copyWrite.setProvider(string);
            }
            string = readString(inputStream, 128);
            if (string != null) {
                copyWrite.setWebsite(string);
            }
            return copyWrite;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readString(InputStream inputStream, int length) throws IOException {
        byte[] buffer = new byte[length];
        if (inputStream.read(buffer) == length) {
            return new String(buffer, "GBK");
        }
        return null;
    }

    private static Integer readInteger(InputStream inputStream) throws IOException {
        byte[] buf = new byte[4];
        if (inputStream.read(buf) == 4) {
            return (buf[0]) + (buf[1] << 8) + (buf[2] << 16) + (buf[3] << 24);
        }
        return null;
    }

    private static void downloadDatWithKey(int key, String filePath) {
        try {
            URL url = new URL("http://update.cz88.net/ip/qqwry.rar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            WryInputStream wryInputStream = new WryInputStream(inputStream, 0x200);
            wryInputStream.decodeStream(key);
            InflaterInputStream inflaterInputStream = new InflaterInputStream(wryInputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024 * 1024];
            int length;
            while ((length = inflaterInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            inflaterInputStream.close();
            wryInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
