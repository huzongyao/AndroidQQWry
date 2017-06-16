package com.hzy.qqwry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.InflaterInputStream;


public class QQWryDownloader {

    private static QQWryDownloader mInstance;

    public static QQWryDownloader getInstance() {
        if (mInstance == null) {
            synchronized (QQWryDownloader.class) {
                if (mInstance == null)
                    mInstance = new QQWryDownloader();
            }
        }
        return mInstance;
    }

    private CopyWrite mCopyWrite;

    public void updateCopyWrite() {
        try {
            URL url = new URL("http://update.cz88.net/ip/copywrite.rar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
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
            mCopyWrite = copyWrite;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CopyWrite getCopyWrite() {
        if (mCopyWrite == null) {
            updateCopyWrite();
        }
        return mCopyWrite;
    }

    public interface ProgressCallback {
        void onProgress(int progress);
    }

    public void downloadQQWryDat(String filePath, ProgressCallback callback) {
        CopyWrite copyWrite = getCopyWrite();
        if (copyWrite.getKey() != null) {
            downloadDatWithKey(copyWrite.getKey(), filePath, callback);
        }
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

    private void downloadDatWithKey(int key, String filePath, ProgressCallback callback) {
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
            int total = connection.getContentLength();
            int already = 0;
            while ((length = inflaterInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
                if (callback != null) {
                    already += length;
                    int progress = already * 100 / total;
                    callback.onProgress(progress);
                }
            }
            fileOutputStream.close();
            inflaterInputStream.close();
            wryInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
