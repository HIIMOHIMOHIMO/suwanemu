package com.example.hanmaku.suwanemupotter;

import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by hanmaku on 2018/11/19.
 */

public class SeatGet {
    public ArrayList<Integer> vehiclelist = new ArrayList();
    public ArrayList<Integer> seatlist = new ArrayList();
    public ArrayList<ArrayList <Integer>> zasekilist = new ArrayList<>();
    public int seat=0;
    int error=0;
    int serverstatus=0;

    public int Seathttp() {
        try {
            zasekilist.clear();     //全座席内容クリア
            vehiclelist.clear();    //車両内容クリア
            seatlist.clear();       //各車両の座席内容クリア
            //http通信開始
            Log.v("MYDEBUG","tuusinnn1");
            AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent");
            //HttpResponse respdata = client.execute(new HttpGet("http://172.20.10.3:8080/seat"));
            HttpResponse respdata = client.execute(new HttpGet("http://192.168.43.81:8080/seat"));
            Log.v("MYDEBUG","tuusinnn2");
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                respdata.getEntity().writeTo(outputStream);            // JSON 形式Data を Stream形式に
                String jsonText = outputStream.toString();              //Stringで格納
                Log.v("MYDEBUG","Json="+jsonText);
                JSONObject zaseki = new JSONObject(jsonText);           //JSONオブジェクト取得
                Log.v("MYDEBUG","tuusinnn3");
                //スライス抽出
                serverstatus = zaseki.getInt("status");
                serverstatus=0;
                Log.v("MYDEBUG","status="+serverstatus);
                if(serverstatus==2){
                    error=3;
                }
                if(error!=3) {
                    seat=zaseki.getInt("seat");
                    Log.v("MYDEBUG","tuusinnn4");
                    JSONArray Slice = zaseki.getJSONArray("Seats");
                    Log.v("MYDEBUG","tuusinnn42");
                    //1車両目座席数取得
                    int vsize = Slice.getJSONArray(0).length();
                    //座席詳細判別
                    for (int i = 0; i < vsize; i++) {
                        vehiclelist.add((int) Slice.getJSONArray(0).get(i));
                    }
                    int vesize = vehiclelist.size();
                    //２車両目座席数取得
                    Log.v("MYDEBUG","tuusinnn5");
                    int ssize = Slice.getJSONArray(1).length();
                    //座席詳細判別
                    for (int i = 0; i < ssize; i++) {
                        seatlist.add((int) Slice.getJSONArray(1).get(i));
                    }
                    Log.v("MYDEBUG","tuusinnn6");
                    //各車両を一つの電車リストに格納
                    zasekilist.add(vehiclelist);
                    zasekilist.add(seatlist);
                }
            } catch (Exception e) {
                error=1;
                Log.d("MYDEBUG", "パースに失敗");
            }
        } catch (Exception e) {
            error=1;
            Log.v("MYDEBUG","通信に失敗");
            e.getStackTrace();
        }
        return error;
    }
}
