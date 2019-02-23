package com.example.hanmaku.suwanemupotter;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.content.Context;
import android.os.AsyncTask;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Alertpost extends AsyncTask<URL, Void, Void> {
    int error=0;
    private SeatingDisplay seat=new SeatingDisplay();

    @Override
    protected Void doInBackground(URL... urls) {
        int qrresult=0;
        Log.v("Alert","urls="+urls);
        final URL url = urls[0];
        Log.v("Alert","url="+url);
        try{
            Log.v("Alert","tuusinnn1");
            AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent");
            //HttpResponse respdata = client.execute(new HttpGet("http://172.20.10.3:8080/seat"));
            Log.v("Alert","tuusinnn1.5");
            HttpResponse respdata = client.execute(new HttpGet("http://192.168.43.81:8080/bibutest"));
            Log.v("Alert","tuusinnn2");
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                respdata.getEntity().writeTo(outputStream);            // JSON 形式Data を Stream形式に
                String jsonText = outputStream.toString();              //Stringで格納
                JSONObject QR = new JSONObject(jsonText);           //JSONオブジェクト取得
                Log.v("Alert", "tuusinnn3");
                //スライス抽出
                qrresult = QR.getInt("QR");
                Log.v("Alert","qrresult="+qrresult);
            } catch (Exception e) {
                error=1;
                Log.d("Alert", "パースに失敗");
            }
        } catch (Exception e) {
            error=1;
            Log.v("Alert","通信に失敗");
            e.getStackTrace();
        }

        final String json = "{\"seat_id\": \""+qrresult+"\"}";
        Log.v("Alert","new");
        URL posturl=null;
        try{
            posturl = new URL("http://172.20.10.8:8080/bibutest");
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) posturl.openConnection();
            con.setDoOutput(true);
            con.setChunkedStreamingMode(0);
            con.connect();

            // POSTデータ送信処理
            OutputStream out = null;
            try {
                out = con.getOutputStream();
                out.write(json.getBytes("UTF-8"));
                out.flush();
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            final int status = con.getResponseCode();
            Log.v("Alert","status="+status);
            if (status == HttpURLConnection.HTTP_OK) {
                Log.v("my","3");
                // 正常
                // レスポンス取得処理を実行
                // 本文の取得
                InputStream in = con.getInputStream();
                byte bodyByte[] = new byte[1024];
                in.read(bodyByte);
                Log.v("Alert","byte="+bodyByte.toString());
                in.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return null;
    }
}