package com.hzy.qqwry.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String localDatPath;
    private QQWryAnd qqWryAnd;
    private ProgressDialog progressDialog;
    private TextView textDatInfo;
    private TextView textResult;
    private EditText editIpAddr;
    private Button buttonQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localDatPath = getExternalFilesDir("qqwry").getAbsolutePath()
                + File.separator + "qqwry.dat";
        textDatInfo = (TextView) findViewById(R.id.text_qqwry_info);
        editIpAddr = (EditText) findViewById(R.id.edit_input_ip);
        buttonQuery = (Button) findViewById(R.id.button_query);
        textResult = (TextView) findViewById(R.id.text_result_info);
        downloadQQWryDat();
        fillWithLocalIp();
        buttonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddr = editIpAddr.getText().toString();
                queryIpAddr(ipAddr);
            }
        });
    }

    private void queryIpAddr(final String ipAddr) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String result = "";
                if (qqWryAnd != null) {
                    result = qqWryAnd.getIpAddr(ipAddr);
                }
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        textResult.setText(s);
                    }
                });
    }

    private void fillWithLocalIp() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(getLocalOriginIp());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        editIpAddr.setText(o);
                    }
                });
    }

    private void downloadQQWryDat() {
        progressDialog = ProgressDialog.show(this, "title", "processing...");
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                File localDat = new File(localDatPath);
                String localVersion = "";
                if (localDat.exists() && localDat.isFile()) {
                    if (qqWryAnd == null) {
                        qqWryAnd = new QQWryAnd(localDatPath);
                    }
                    localVersion = qqWryAnd.getVersion();
                }
                if (TextUtils.isEmpty(localVersion)) {
                    QQWryDownloader.getInstance().downloadQQWryDat(localDatPath);
                    if (qqWryAnd != null) {
                        qqWryAnd.close();
                    }
                    qqWryAnd = new QQWryAnd(localDatPath);
                }
                e.onNext(qqWryAnd.getVersion() + "\nCount: " + qqWryAnd.getIpCount());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        progressDialog.dismiss();
                        textDatInfo.setText(s);
                    }
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
