package com.hzy.qqwry.demo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hzy.qqwry.QQWryAnd;
import com.hzy.qqwry.QQWryDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private String localDatPath;
    private QQWryAnd qqWryAnd;
    private ProgressDialog progressDialog;
    private TextView textDatInfo;
    private TextView textResult;
    private EditText editIpAddr;
    private Button buttonQuery;
    private ExecutorService mThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mThreadPool = Executors.newFixedThreadPool(3);
        localDatPath = getExternalFilesDir("qqwry")
                .getAbsolutePath() + File.separator + "qqwry.dat";
        textDatInfo = findViewById(R.id.text_qqwry_info);
        editIpAddr = findViewById(R.id.edit_input_ip);
        buttonQuery = findViewById(R.id.button_query);
        textResult = findViewById(R.id.text_result_info);
        downloadQQWryDat();
        fillWithLocalIp();
        buttonQuery.setOnClickListener(v -> {
            String ipAddr = editIpAddr.getText().toString();
            queryIpAddress(ipAddr);
        });
    }

    @Override
    protected void onDestroy() {
        mThreadPool.shutdownNow();
        super.onDestroy();
    }

    private void queryIpAddress(String ipAddr) {
        mThreadPool.submit(() -> {
            if (qqWryAnd != null) {
                String result = qqWryAnd.getIpAddr(ipAddr) + "\n"
                        + qqWryAnd.getIpRange(ipAddr);
                runOnUiThread(() -> textResult.setText(result));
            }
        });
    }

    private void fillWithLocalIp() {
        mThreadPool.submit(() -> {
            String ip = getLocalOriginIp();
            runOnUiThread(() -> editIpAddr.setText(ip));
        });
    }

    @SuppressLint("SetTextI18n")
    private void downloadQQWryDat() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Preparing Data...");
        progressDialog.show();
        mThreadPool.submit(() -> {
            File localDat = new File(localDatPath);
            String localVersion = "";
            if (localDat.exists() && localDat.isFile()) {
                if (qqWryAnd == null) {
                    qqWryAnd = new QQWryAnd(localDatPath);
                }
                localVersion = qqWryAnd.getVersion();
            }
            if (TextUtils.isEmpty(localVersion)) {
                QQWryDownloader.getInstance().downloadQQWryDat(localDatPath,
                        progress -> progressDialog.setProgress(progress));
                if (qqWryAnd != null) {
                    qqWryAnd.close();
                }
                qqWryAnd = new QQWryAnd(localDatPath);
            }
            runOnUiThread(() -> {
                progressDialog.dismiss();
                textDatInfo.setText(qqWryAnd.getVersion() + "\nCount: " + qqWryAnd.getIpCount());
            });
        });
    }

    private String getLocalOriginIp() {
        try {
            URL url = new URL("http://httpbin.org/ip");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            return jsonObject.getString("origin");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
