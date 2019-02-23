package com.example.hanmaku.suwanemupotter;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import static android.R.attr.editTextStyle;
import static android.R.attr.inputMethod;
import static com.example.hanmaku.suwanemupotter.R.id.DepStation;
import static com.example.hanmaku.suwanemupotter.R.id.parent;

/**
 * Created by hanmaku on 2018/10/01.
 */

public class UserInput extends FragmentActivity implements TimePickerDialog.OnTimeSetListener,View.OnClickListener  {

    /* 変数定義------------------------------------------------------------------------------------------- */
    TextView timeView;                                              /* 時間入力用                          */
    String alltime=null;                                            /* ユーザーの入力した時間と分          */
    String timehour=null;                                           /* ユーザーの入力した時間              */
    String timeminute=null;                                         /* ユーザーの入力した分                */
    String startstation=null;                                       /* ユーザーの入力した出発駅            */
    String endstation=null;                                         /* ユーザーの入力した到着駅            */
    String Depfirsttime=null;                                       /* 1ダイヤ目出発時間                   */
    String Depsecondtime=null;                                      /* 2ダイヤ目出発時間                   */
    String Depthird=null;                                           /* 3ダイヤ目出発時間                   */
    String Arryvalfirsttime=null;                                   /* 1ダイヤ目到着時間                   */
    String Arryvalsecondtime=null;                                  /* 2ダイヤ目到着時間                   */
    String Arryvalthird=null;                                       /* 3ダイヤ目到着時間                   */
    String Stationname[]={                                          /* 駅選択肢                            */
            "なかもず",
            "新金岡",
            "北花田",
            "あびこ",
            "長居",
            "西田辺",
            "昭和町",
            "天王寺",
            "動物園前",
            "大国町",
            "なんば",
            "心斎橋",
            "本町",
            "淀屋橋",
            "梅田",
            "中津",
            "西中島南方",
            "新大阪",
            "東三国",
            "江坂",
    };
    int inputok=0;              //入力がされているか
    AlertDialog.Builder builder;

    @Override

    /* onCreate,Viewの割り当て、スピナーの設定等行う。----------------------------------------------------- */
    protected void onCreate(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinput);             //view表示
        timeView = (TextView)findViewById(R.id.DepTimeText);
        findViewById(R.id.Search).setOnClickListener(this);
        //start=(EditText) findViewById(R.id.DepStation);
        /* スピナー設定 */
        Spinner Depspinner=(Spinner) findViewById(R.id.DepStation);
        Spinner Arraivalspinner=(Spinner) findViewById(R.id.ArrivalStation);
        Arraivalspinner.setSelection(19);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,Stationname);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Depspinner.setAdapter(adapter);
        Depspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Spinner spinner=(Spinner)parent;
                String item=(String)spinner.getSelectedItem();
                startstation=item.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Arraivalspinner.setAdapter(adapter);
        Arraivalspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Spinner spinner=(Spinner)parent;
                String item=(String)spinner.getSelectedItem();
                endstation=item.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void onClick(View v) {
        inputok=0;
        Log.v("my","Dep="+startstation);
        Log.v("my","arryval="+endstation);

        if(timehour==null){
            Toast.makeText(UserInput.this,"出発時間を入力してください", Toast.LENGTH_SHORT).show();
            inputok=1;
        }

        if (inputok==0){
            Intent intent=null;
            intent = new Intent(UserInput.this,DataBase.class);
            intent.putExtra("TIMEHOUR", timehour);
            intent.putExtra("TIMEMINUTE", timeminute);
            intent.putExtra("DEPSTATION", startstation);
            intent.putExtra("ALLYVALSTATION", endstation);
            Log.v("my","Dep="+startstation);
            startActivityForResult(intent,1001);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timehour = String.format(Locale.US, "%d", hourOfDay);
        timeminute = String.format(Locale.US, "%d", minute);
        alltime=timehour+":"+timeminute;
        timeView.setText( alltime );

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
        // startActivityForResult()の際に指定した識別コードとの比較
        if( requestCode == 1001 ){
            // 返却結果ステータスとの比較
            if( resultCode == 1234 ){
                Arryvalfirsttime=intent.getStringExtra("FIRST_ARRYVALTIME");
                Depfirsttime=intent.getStringExtra("FIRST_DEPTIME");
                Arryvalsecondtime=intent.getStringExtra("SECOND_ARRYVALTIME");
                Depsecondtime=intent.getStringExtra("SECOND_DEPTIME");
                String Direction = intent.getStringExtra("DEP_STATION");
                intent = new Intent(UserInput.this, VehicleSelection.class);
                intent.putExtra("FIRST_DEPFIRSTTIME", Depfirsttime);
                intent.putExtra("FIRST_ARRYVALTIME", Arryvalfirsttime);
                intent.putExtra("SECOND_DEPFIRSTTIME", Depsecondtime);
                intent.putExtra("SECOND_ARRYVALTIME", Arryvalsecondtime);
                intent.putExtra("DEP_STATION", Direction);
                startActivity(intent);
                }
            }
        }
    }

