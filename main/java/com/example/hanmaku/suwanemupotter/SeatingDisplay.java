package com.example.hanmaku.suwanemupotter;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hanmaku on 2018/10/01.
 */

public class SeatingDisplay extends AppCompatActivity implements View.OnClickListener{
    ArrayList<ImageView> seatview = new ArrayList<ImageView>();     //座席ビュー
    TextView vehileString;                                  //座席一覧に表示する車両番号
    SeatGet seatget=new SeatGet();                          //座席情報取得クラス
    int error=0;                                            //エラーチェック
    int vehicle=0;
    public String qrresult=null;

    MyAsyncTask myAsync;                                    //http通信を Async(非同期-裏)で実行するTask
    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        //コンストラクタ
        public MyAsyncTask(Context context) {
        }
        // バックグラウンド実行前処理
        protected void onPreExecute() {
            seatget.zasekilist.clear();     //全座席内容クリア
            seatget.vehiclelist.clear();    //車両内容クリア
            seatget.seatlist.clear();       //各車両の座席内容クリア
        }
        //  バックグラウンド処理本体
        protected Void doInBackground(Void... params) {
        seatget.Seathttp();
            error=seatget.Seathttp();
            return null;
        }
        //バックグラウンド実行後処理
        protected void onPostExecute(Void dummy) {
            if(error==0) {
            /* 現在車両の座席数取得 */

                int j = seatget.zasekilist.get(vehicle).size();
                Log.v("My", "vehicle=" + vehicle);
                Log.v("My", "jsize=" + j);
                ImageView target;
                /* 1なら満席、0なら空席をset */
                for (int i = 0; i < j; i++) {
                    Log.v("My", "i=" + i);
                    if (seatget.zasekilist.get(vehicle).get(i) == 1) {
                        target = seatview.get(i);
                        target.setImageResource(R.mipmap.manseki);
                    } else {
                        target = seatview.get(i);
                        target.setImageResource(R.mipmap.kara);
                    }
                }
            }else{/*
                Toast.makeText(SeatingDisplay.this,"サーバーとの通信に失敗しました。", Toast.LENGTH_SHORT).show();
                Log.v("MYDEBUG","seatingサーバーエラー");
                Intent intent=null;
                intent = new Intent(SeatingDisplay.this,UserInput.class);
                startActivity(intent);*/
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        vehicle=intent.getIntExtra("VEHICLE",0);                                          //現在選択されている車両番号
        Log.v("my","vehicle="+vehicle);
        setContentView(R.layout.seationgdisplay);   //座席一覧表示
        findViewById(R.id.qrbutton).setOnClickListener(this);
        vehileString=(TextView) findViewById(R.id.vhicletext);//車両表示
        vehileString.setText((vehicle+1) + "車両目");
        /* 座席id取得 */
        seatview.add((ImageView) findViewById(R.id.seat1));
        seatview.add((ImageView)findViewById(R.id.seat2));
        seatview.add((ImageView)findViewById(R.id.seat3));
        seatview.add((ImageView)findViewById(R.id.seat4));
        seatview.add((ImageView)findViewById(R.id.seat5));
        seatview.add((ImageView)findViewById(R.id.seat6));
        seatview.add((ImageView)findViewById(R.id.seat7));
        seatview.add((ImageView)findViewById(R.id.seat8));
        seatview.add((ImageView)findViewById(R.id.seat9));
        seatview.add((ImageView)findViewById(R.id.seat10));
        seatview.add((ImageView)findViewById(R.id.seat11));
        seatview.add((ImageView)findViewById(R.id.seat12));
        seatview.add((ImageView)findViewById(R.id.seat13));
        seatview.add((ImageView)findViewById(R.id.seat14));
        seatview.add((ImageView)findViewById(R.id.seat15));
        seatview.add((ImageView)findViewById(R.id.seat16));

        mGestureDetector = new GestureDetector(this, mOnGestureListener);// フリック処理追加
    }

    //QR読み込み、アラーム設定
    public void onClick(View v) {
        new IntentIntegrator(SeatingDisplay.this).initiateScan();//QRスタート
    }

    // タッチイベントを処理するためのインタフェース
    private GestureDetector mGestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 50;

    // X軸最低スワイプスピード
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    // Y軸の移動距離　これ以上なら横移動を判定しない
    private static final int SWIPE_MAX_OFF_PATH = 250;

    // タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // タッチイベントのリスナー
    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        // フリックイベント
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try {
                //車両数を確認
                int vehiclesize=seatget.zasekilist.size();
                Log.v("My","allseatsize="+vehiclesize);
                Log.v("My","vehicle=="+vehicle);
                // Y軸の移動距離が大きすぎる場合
                if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
                    Toast.makeText(SeatingDisplay.this,"縦の移動距離が大きすぎ", Toast.LENGTH_SHORT).show();
                }
                // 開始位置から終了位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if  (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(vehicle+1>=vehiclesize) {
                        Toast.makeText(SeatingDisplay.this,"+車両限界数", Toast.LENGTH_SHORT).show();
                        return (false);
                    }else{
                        vehicle++;
                    }
                    Log.v("my","車両="+vehicle);
                    vehileString.setText((vehicle+1) + "車両目");
                }
                // 終了位置から開始位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(vehicle<=0) {
                        Toast.makeText(SeatingDisplay.this,"-車両限界数", Toast.LENGTH_SHORT).show();
                    }else {
                        vehicle--;
                    }
                    vehileString.setText((vehicle+1) + "車両目");
                }
            } catch (Exception e) {
                // TODO
            }
            return false;
        }
    };

    //タイマハンドラー
    Timer mTimer   = null;
    Handler mHandler = new Handler();
    /* タイマキャンセル */
    protected void onPause(){
        super.onPause();
        mTimer.cancel();
        mTimer = null;
        Log.d("MYDEBUG","SeatonPause()");
    }

    /* タイマ復帰 */
    protected void onResume(){
        Log.d("MYDEBUG","SeatResume()");
        super.onResume();
        //タイマーの初期化処理
        mTimer = new Timer(true);
        mTimer.schedule( new TimerTask(){
            @Override
            public void run() {
                // mHandlerを通じてUI Threadへ処理をキューイング
                mHandler.post( new Runnable() {
                    public void run() {
                        /*
                        Intent intent=null;
                        intent = new Intent(SeatingDisplay.this,SeatGet.class);
                        startActivityForResult(intent,1001);
                        */
                        myAsync = new MyAsyncTask(SeatingDisplay.this);  // AsyncTaskは一度executeしてしまうと再利用できない.
                        // 処理を繰り返す為には、毎回Instanceを作り直す必要がある
                        myAsync.execute();                          // 非同期タスクの実行開始  ( new MyAsyncTask(this).execute(); と書いても良い)
                    }
                });
            }
        }, 0, 5000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            Log.d("Alert", "qrresult="+result.getContents());
            qrresult=result.getContents();
            try {
                Log.v("Alert","hairu");
                new Alertpost().execute(new URL(qrresult));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
