package com.example.hanmaku.suwanemupotter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hanmaku on 2018/10/01.
 */

public class VehicleSelection extends AppCompatActivity implements View.OnClickListener {
    /* 変数定義------------------------------------------------------------------------------------------- */
    ArrayList<ImageButton> syaryoview = new ArrayList();            /* ImageButton座席ビューリスト         */
    ImageButton listevaluation;                                     /* 配列                       　       */
    TextView time1;                                                 /* 1車両目出発時間                     */
    TextView time2;                                                 /* 1車両目到着時間                     */
    TextView time3;                                                 /* 2車両目出発時間                     */
    TextView time4;                                                 /* 2車両目到着時間                     */
    TextView Directionview;                                         /* 方面を判断して表示                  */
    TextView houmenview;                                            /* ”方面”をくっつける                */
    String Direction;                                               /* 到着駅格納用       c                */
    int errorcheck=0;                                               /* ポップアップチェック                */
    int error=0;                                                    /* 0=正常,1=エラー,2=通信中            */
    /* クラス生成----------------------------------------------------------------------------------------- */
    private ProgressDialog progressDialog;                          /* Loding...のプログレスバー表示用     */
    private SeatGet seatget =new SeatGet();                         /* 通信用クラス生成                    */
    /* タイマハンドラー */
    Timer mTimer   = null;
    Handler mHandler = new Handler();
    AlertDialog.Builder builder;
    /* 非同期タスク、座席情報取得からパース、View配置まで行う--------------------------------------------- */
    MyAsyncTask myAsync;               // (時間のかかる) WEB Accessを Async(非同期-裏)で実行するTask

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        //コンストラクタ
        public MyAsyncTask(Context context) {
        }

        // バックグラウンド実行前処理
        protected void onPreExecute() {
            Log.v("MYDEBUG", "前処理");
            error=2;
        }
        //  バックグラウンド処理本体
        protected Void doInBackground(Void... params){
            Log.v("MYDEBUG", "本体処理");
            error=seatget.Seathttp();
            return null;
        }
        //バックグラウンド実行後処理
        protected void onPostExecute(Void dummy) {
            //setContentView(R.layout.zaseki1);
            Log.v("MYDEBUG", "後処理");
            if(error==0) {
               // setContentView(R.layout.vehicleselection);
                seatget.zasekilist.clear();//座席情報クリア
                /* 座席情報取得 */
                seatget.zasekilist.add(seatget.vehiclelist);
                seatget.zasekilist.add(seatget.seatlist);

                int zasekisam = 0;//一車両に座っている人の数
                int j = seatget.zasekilist.size();

                /* 座席のこみ具合をマークで表示 */
                for (int i = 0; i < j; i++) {
                    listevaluation = syaryoview.get(i);
                    for (int k = 0; k < 16; k++) {
                        zasekisam = zasekisam + seatget.zasekilist.get(i).get(k);
                    }
                    if (zasekisam <= 4) {
                        listevaluation.setImageResource(R.mipmap.kara);
                    } else if (zasekisam <= 11 && zasekisam >= 5) {
                        listevaluation.setImageResource(R.mipmap.mama);
                    } else if (zasekisam <= 15 && zasekisam >= 12) {
                        listevaluation.setImageResource(R.mipmap.kekkou);
                    } else {
                        listevaluation.setImageResource(R.mipmap.manseki);
                    }
                    zasekisam = 0;//座っている人の数をクリア
                }
                progressDialog.dismiss();
            }
            else if(error==1)
            {
                Log.v("MYDEBUG", "サーバーとの通信中にエラー");
                builder.setMessage("サーバーとの通信中にエラーが起きました。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                errorcheck=1;
                            }
                        });
                mHandler.post( new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });
                while(errorcheck==0){
                }
                Intent intent=null;
                intent = new Intent(VehicleSelection.this,UserInput.class);
                startActivity(intent);
            }else if(error==3){
                Log.v("MYDEBUG", "サーバーとマイコンの通信が確認されていません。");
                builder.setMessage("サーバーとマイコンの通信が確認されていません。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                builder.show();
                /*Intent intent=null;
                intent = new Intent(VehicleSelection.this,UserInput.class);
                startActivity(intent);*/
            }
        }
    }
    /* onCreate,プログレスダイアログの表示、ユーザー入力情報の受け取り、各ビューIdの割り当てを行う。---------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vehicleselection);              //view表示
        builder = new AlertDialog.Builder(this);
        /* プログレスダイアログ表示 */
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        /* 出発時間情報取得 */
        Intent intent = getIntent();
        String Depfirsttime = intent.getStringExtra("FIRST_DEPFIRSTTIME");
        String Arryvalfirsttime = intent.getStringExtra("FIRST_ARRYVALTIME");
        String Depsecondtime = intent.getStringExtra("SECOND_DEPFIRSTTIME");
        String Arryvalsecondtime = intent.getStringExtra("SECOND_ARRYVALTIME");
        Direction = intent.getStringExtra("DEP_STATION");
        Log.v("my","Direction="+Direction);
        /* ViewId割り当て */
        time1=(TextView) findViewById(R.id.deptime1);
        time2=(TextView) findViewById(R.id.Arryvaltime1);
        time3=(TextView) findViewById(R.id.deptime2);
        time4=(TextView) findViewById(R.id.Arryvaltime2);
        Depfirsttime = Depfirsttime + "時発";
        Arryvalfirsttime=Arryvalfirsttime+"時着";
        Depsecondtime = Depsecondtime + "時発";
        Arryvalsecondtime=Arryvalsecondtime+"時着";
        time1.setText(Depfirsttime);
        time2.setText(Arryvalfirsttime);
        time3.setText(Depsecondtime);
        time4.setText(Arryvalsecondtime);

        /* リスナー仕込む */
        findViewById(R.id.vehicle1).setOnClickListener(this);
        findViewById(R.id.vehicle2).setOnClickListener(this);
        findViewById(R.id.vehicle3).setOnClickListener(this);
        findViewById(R.id.vehicle4).setOnClickListener(this);
        findViewById(R.id.vehicle5).setOnClickListener(this);
        findViewById(R.id.vehicle6).setOnClickListener(this);
        findViewById(R.id.vehicle7).setOnClickListener(this);
        findViewById(R.id.vehicle8).setOnClickListener(this);
        findViewById(R.id.vehicle9).setOnClickListener(this);
        findViewById(R.id.vehicle10).setOnClickListener(this);

        /* ビューid取得 */
        syaryoview.add((ImageButton)findViewById(R.id.vehicle1));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle2));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle3));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle4));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle5));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle6));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle7));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle8));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle9));
        syaryoview.add((ImageButton)findViewById(R.id.vehicle10));

    }
    /* onClick,押された車両を検知してSeatingDisplayに渡す------------------------------------------------- */
    public void onClick(View v) {
        Log.v("my","v="+v);
        Log.v("my","button2="+syaryoview.get(1));
        Intent intent = null;
        intent = new Intent(VehicleSelection.this, SeatingDisplay.class);
        if(v==syaryoview.get(0)){
            intent.putExtra("VEHICLE", 0);
            Log.v("my","button1");
        }else if(v==syaryoview.get(1)){
            intent.putExtra("VEHICLE", 1);
            Log.v("my","button2");
        }
        startActivity(intent);
    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
        Log.d("MYDEBUG","VehicleonDestroy()");
    }
*/

    protected void onPause(){
        super.onPause();
        mTimer.cancel();
        mTimer = null;
        Log.d("MYDEBUG","VehicleonPause()");
    }


    protected void onResume(){
        super.onResume();
        Log.d("MYDEBUG","VehicleonResume()");
        //タイマーの初期化処理
        mTimer = new Timer(true);
        mTimer.schedule( new TimerTask(){
            @Override
            public void run() {
                // mHandlerを通じてUI Threadへ処理をキューイング
                mHandler.post( new Runnable() {
                    public void run() {
                        Log.v("MYDEBUG", "Vehicleタイマー発動");
                    if(error!=2) {
                        Log.v("MYDEBUG", "VehicleAsync起動");
                        myAsync = new MyAsyncTask(VehicleSelection.this);  // AsyncTaskは一度executeしてしまうと再利用できない.
                        // 処理を繰り返す為には、毎回Instanceを作り直す必要がある
                        // Listenerを設定
                        myAsync.execute();                          // 非同期タスクの実行開始  ( new MyAsyncTask(this).execute(); と書いても良い)
                    }
                    }
                });
            }
        }, 0, 5000);
    }

}