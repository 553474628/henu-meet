package com.slj.meet;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    private List<Button> btnList;
    private GridLayout gridLayout;
    private Map<Integer, People> nameList = null;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridLayout = findViewById(R.id.container);
        postRequest("张三");
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                Log.i("wqer", ReturnMessage);
                try {
                    JSONArray jsonArray = new JSONArray(ReturnMessage);

                    nameList = new HashMap<Integer, People>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        nameList.put(i, new People(id,name));
                    }
                    setbtn();  //设置显示参会人员姓名
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(msg);
        }
    };
    class People{
        int id;
        String name;
        public People(int id,String name){
            this.id=id;
            this.name=name;
        }
    }

    private void setbtn() {
        for (int i = 0; i < nameList.size(); i++) {

            Button button = new Button(this);
            button.setPadding(10,10,10,10);
            button.setText(nameList.get(i).name);
            button.setBackgroundResource(R.drawable.btnbg);

            GridLayout.Spec row = GridLayout.spec(i%8);
            GridLayout.Spec column = GridLayout.spec(i/8+1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(column,row);
            params.setMargins(20,20,20,20);
            gridLayout.addView(button,params);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button buttoandroid=(Button) view;
                    String name= (String) buttoandroid.getText();
                    int id=0;
                    for (int i=0;i<nameList.size();i++){
                        if (name.equals(nameList.get(i).name)){
                            id=nameList.get(i).id;
                        }
                    }
                    postRequest0(id);
                }
            });
        }
    }

    private void postRequest(String name) {
        RequestBody requestBody = new FormBody.Builder().build();
        final Request request = new Request.Builder()
                .url("http://"+PublicUtil.ip+"/conference/index/index/index")
                .post(requestBody)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        handler.obtainMessage(1, response.body().string()).sendToTarget();
                    } else {
                        throw new IOException("Unexception code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    @SuppressLint("HandlerLeak")
    public void postRequest0(final int id) {

        RequestBody requestBody = new FormBody.Builder().add("id",""+id).build();
        final Request request = new Request.Builder()
                .url("http://"+PublicUtil.ip+"/conference/index/index/pic")
                .post(requestBody)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        handler0.obtainMessage(1,id,0, response.body().string()).sendToTarget();

                    } else {
                        throw new IOException("Unexception code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler0 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                int id=msg.arg1;
                Log.e("wqer", ReturnMessage+id);
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("name", ReturnMessage);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            super.handleMessage(msg);
        }
    };
}
