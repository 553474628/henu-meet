package com.slj.meet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity implements DownloadFile.Listener, View.OnClickListener {
    private boolean aBoolean = true;

    private PDFPagerAdapter adapter;

    private LinearLayout bottom;

    private RadioGroup rg;

    private RadioButton button1;

    private RadioButton button2;

    private RadioButton button3;

    final OkHttpClient client = new OkHttpClient();

    private int col_num;

    private LinearLayout excel;

    private TextView excel_name;

    private RelativeLayout file;

    private int fileType;

    private int finalId = -1;

    private boolean flag = true;

    @SuppressLint({"HandlerLeak"})
    private Handler handler1 = new Handler() {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                String str = (String)param1Message.obj;
                Log.i("wqer", str);

                try {

                    JSONObject jSONObject = new JSONObject(str);
                    int i = jSONObject.getInt("status");
                    log(jSONObject.toString());
                    String str1 = jSONObject.getString("sessionName");
                    String str2 = jSONObject.getString("committee");
                    log(str1 + str2);
                    welcome_text.setText(str1);
                    welcome_text0.setText(str2);

                    if (i == 0) {
                        i = jSONObject.getInt("isVote");
                        jSONObject = new JSONObject(jSONObject.getString("info"));
                        int j = jSONObject.getInt("aid");

                        if (Main2Activity.this.finalId != j) {
                            if (!flag) {
                                welcome_root.setVisibility(View.VISIBLE);
                                flag = true;
                                log("!flag");
                                pdf_back.setVisibility(View.GONE);
                            } else {
                                Thread.sleep(3000L);   //等3s加载投票界面
                                aBoolean = true;
                                if (i == 0) {
                                    submit.setEnabled(false);
                                    submitexcel.setEnabled(false);
                                    suggess.setEnabled(false);
                                    submit.setBackgroundResource(R.drawable.radiusun);
                                    submitexcel.setBackgroundResource(R.drawable.radiusun);
                                    submit.setAlpha(0.5F);
                                    submitexcel.setAlpha(0.5F);
                                } else if (i == 1) {
                                    submit.setEnabled(true);
                                    submitexcel.setEnabled(true);
                                    suggess.setEnabled(true);
                                    submit.setAlpha(1.0F);
                                    submitexcel.setAlpha(1.0F);
                                    submit.setBackgroundResource(R.drawable.radius);
                                    submitexcel.setBackgroundResource(R.drawable.radius);
                                }

                                finalId = j;
                                fileType = jSONObject.getInt("fileType");  //0：pdf，1：exccel

                                if (flag) {
                                    welcome_root.setVisibility(View.GONE);
                                    flag = false;
                                    log("flag");
                                }

                                if (fileType == 0) {
                                    pdf_root.setVisibility(View.VISIBLE);
                                    excel.setVisibility(View.GONE);
                                    postRequest3(j);
                                } else if (fileType == 1) {
                                    excel.setVisibility(View.VISIBLE);
                                    pdf_root.setVisibility(View.GONE);
                                    postRequest4(j);
                                }
                            }
                        } else if (aBoolean) {
                            if (i == 0) {
                                submit.setEnabled(false);
                                submitexcel.setEnabled(false);
                                suggess.setEnabled(false);
                                submit.setBackgroundResource(R.drawable.radiusun);
                                submitexcel.setBackgroundResource(R.drawable.radiusun);
                                submit.setAlpha(0.5F);
                                submitexcel.setAlpha(0.5F);
                            } else if (i == 1) {
                                submit.setEnabled(true);
                                submitexcel.setEnabled(true);
                                suggess.setEnabled(true);
                                submit.setAlpha(1.0F);
                                submitexcel.setAlpha(1.0F);
                                submit.setBackgroundResource(R.drawable.radius);
                                submitexcel.setBackgroundResource(R.drawable.radius);
                            }
                        }
                    } else if (i != 1) {
                        if (i == 2) {
                            if (!flag) {
                                welcome_root.setVisibility(View.VISIBLE);
                                flag = true;
                            }
                        } else {
                            log("请求议程失败");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(param1Message);
        }
    };

    @SuppressLint({"HandlerLeak"})
    private Handler handler2 = new Handler() {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                String str = (String)param1Message.obj;
                int i = param1Message.arg1;
                bottom.setVisibility(View.VISIBLE);
                log("wqer" + str);
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (jSONObject.getInt("aid") == i) {
                        String str1 = jSONObject.getString("pdf");
                        mUrl = "http://" + PublicUtil.ip + "/conference" + str1;
                        setDownloadListener(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(param1Message);
        }
    };

    @SuppressLint({"HandlerLeak"})
    private Handler handler3 = new Handler() {
        public void handleMessage(Message param1Message) {
            if (param1Message.what == 1) {
                String str = (String)param1Message.obj;
                int i = param1Message.arg1;
                log(str);

                try {
                    JSONObject jSONObject = new JSONObject(str);
                    int j = jSONObject.getInt("aid");

                    col_num = jSONObject.getInt("col_num") + 1;

                    str = jSONObject.getString("name");//String name = jSONObject.getString("name");

                    number = jSONObject.getInt("number");
                    //log("cccccccccccccccccccccccc"+jSONObject.getInt("number"));

                    list1 = new ArrayList();
                    listcheck = new ArrayList();
                    JSONArray jSONArray = new JSONArray(jSONObject.get("list").toString());
                    log(jSONArray.toString());
                    if (jSONArray.length() < 10) {
                        bottom.setVisibility(View.INVISIBLE);
                    }
                    for (int b = 0;b < jSONArray.length(); b++) {

                            JSONArray jSONArray1 = new JSONArray(jSONArray.get(b).toString());
                            if (b == 0) {
                                for (byte b1 = 0; b1 < jSONArray1.length(); b1++)
                                    ids[b1].setText(jSONArray1.get(b1).toString());
                                ids[8].setText("操作");
                            } else {
                                ArrayList arrayList = new ArrayList();
                                for (byte b1 = 0; b1 < jSONArray1.length(); b1++)
                                    arrayList.add(jSONArray1.get(b1).toString());
                                list1.add(arrayList);
                                if (number == 0){
                                    listcheck.add(Integer.valueOf(R.id.r1));
                                } else {
                                    listcheck.add(Integer.valueOf(R.id.r2));
                                }

                            }
                        }
                            if (j == i) {
                                excel_name.setText(str);

                                listView.setAdapter(new ExcelAdapter(Main2Activity.this, list1, listcheck));
                                log((list1.get(0)).size() + "abc");
                                if ((list1.get(0)).size() > 9) {
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> param2AdapterView, View param2View, int param2Int, long param2Long) {
                                            mUrl = "http://" + PublicUtil.ip + "/conference/upload/pdf/" + (String)((List)list1.get(param2Int)).get(0) + ".pdf";
                                            pdf_root.setVisibility(View.VISIBLE);
                                            excel.setVisibility(View.GONE);
                                            setDownloadListener(true);
                                        }
                                    });
                                } else {
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> param2AdapterView, View param2View, int param2Int, long param2Long) {}
                                    });
                                }
                            }
                            super.handleMessage(param1Message);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(param1Message);
        }
    };

    @SuppressLint({"HandlerLeak"})
    private Handler handler4 = new Handler() {
        public void handleMessage(Message param1Message) {
            String str = (String)param1Message.obj;
            try {
                JSONObject jSONObject = new JSONObject(str);
                log(jSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (param1Message.what == 1){

                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                builder.setTitle("温馨提示");
                builder.setMessage("您已经提交成功");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        log("ok");
                    }
                });
                //builder.show();
                final AlertDialog dlg = builder.create();
                dlg.show();
                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        dlg.dismiss();   //提交成功窗口设置五秒消失
                        t.cancel();
                    }
                }, 5000);

                //toast("提交成功！");
                suggess.setText("");
            }
            super.handleMessage(param1Message);
        }
    };

    private TextView[] ids = new TextView[9];

    private List<List<String>> list1;

    private ListView listView;

    private List<Integer> listcheck;

    private String mUrl = "";

    private String name = "";

    private ProgressBar pb_bar;

    private Button pdf_back;

    private RelativeLayout pdf_root;

    private RemotePDFViewPager remotePDFViewPager;

    private RequestBody requestBody = null;

    private Button submit;

    private Button submitexcel;

    private EditText suggess;

    private TextView tvm1;

    private TextView tvm2;

    private TextView tvm3;

    private TextView tvm4;

    private TextView tvm5;

    private TextView tvm6;

    private TextView tvm7;

    private TextView tvm8;

    private TextView tvm9;

    private int uid = 0;

    private LinearLayout welcome_root;

    private TextView welcome_text;

    private TextView welcome_text0;

    private int number = 0;

    private void initView() {
        listView = (ListView)findViewById(R.id.listview);
        excel = (LinearLayout)findViewById(R.id.meet_excel);
        excel_name = (TextView)findViewById(R.id.excel_name);
        tvm1 = (TextView)findViewById(R.id.tvm1);
        tvm2 = (TextView)findViewById(R.id.tvm2);
        tvm3 = (TextView)findViewById(R.id.tvm3);
        tvm4 = (TextView)findViewById(R.id.tvm4);
        tvm5 = (TextView)findViewById(R.id.tvm5);
        tvm6 = (TextView)findViewById(R.id.tvm6);
        tvm7 = (TextView)findViewById(R.id.tvm7);
        tvm8 = (TextView)findViewById(R.id.tvm8);
        tvm9 = (TextView)findViewById(R.id.tvm9);
        ids[0] = tvm1;
        ids[1] = tvm2;
        ids[2] = tvm3;
        ids[3] = tvm4;
        ids[4] = tvm5;
        ids[5] = tvm6;
        ids[6] = tvm7;
        ids[7] = tvm8;
        ids[8] = tvm9;
        file = (RelativeLayout)findViewById(R.id.meet_file);
        pdf_root = (RelativeLayout)findViewById(R.id.remote_pdf_root);
        pb_bar = (ProgressBar)findViewById(R.id.pb_bar);
        submit = (Button)findViewById(R.id.submit);

        pdf_back = (Button)findViewById(R.id.pdf_back);
        submitexcel = (Button)findViewById(R.id.submitexcel);
        rg = (RadioGroup) findViewById(R.id.rg);
        button1 = (RadioButton)findViewById(R.id.button1);
        button2 = (RadioButton)findViewById(R.id.button2);
        button3 = (RadioButton)findViewById(R.id.button3);
        suggess = (EditText)findViewById(R.id.suggess);
        bottom = (LinearLayout)findViewById(R.id.bottom);
        submit.setOnClickListener(this);
        submitexcel.setOnClickListener(this);
    }

    private void initViewWelcome() {
        this.welcome_root = (LinearLayout)findViewById(R.id.meet_welcome);
        this.welcome_text = (TextView)findViewById(R.id.welcome_text);
        this.welcome_text0 = (TextView)findViewById(R.id.welcome_text0);
    }

    private void postRequest1() {
        requestBody = new FormBody.Builder().build();
        final Request request = new Request.Builder()
                .url("http://"+ PublicUtil.ip+"/conference/index/index/init")
                .post(requestBody)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        handler1.obtainMessage(1, response.body().string()).sendToTarget();
                    } else {
                        throw new IOException("Unexception code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void postRequest2(final int paramInt1, final int paramInt2, String paramString, int paramInt3) {
        JSONArray jSONArray = null;
        if (this.fileType == 1) {
            jSONArray = new JSONArray();
            int count = 0;
            ArrayList<String> list = new ArrayList<>(listcheck.size());
            for (int i = 0;i < listcheck.size(); i++) {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        switch (listcheck.get(i))  {
                            case R.id.r1:
                                jSONObject.put((String)((List)this.list1.get(i)).get(0), 1);
                                list.add(list1.get(i).get(0));
                                count ++;
                                break;
                            case R.id.r2:
                                jSONObject.put((String)((List)this.list1.get(i)).get(0), 0);
                                break;
                            case R.id.r3:
                                jSONObject.put((String)((List)this.list1.get(i)).get(0), 2);
                                break;
                            default:
                                break;
                        }

                        if (number != 0 && count > number) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Main2Activity.this);
                            dialog.setTitle("友情提示");
                            dialog.setMessage("您只能选择 "+number+" 名候选对象");
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    submit.setEnabled(true);
                                    submitexcel.setEnabled(true);
                                    suggess.setEnabled(true);
                                    submit.setAlpha(1.0F);
                                    submitexcel.setAlpha(1.0F);
                                    submit.setBackgroundResource(R.drawable.radius);
                                    submitexcel.setBackgroundResource(R.drawable.radius);
                                }
                            });
                            dialog.show();
                            return;
                        } else {
                            jSONArray.put(jSONObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            log(jSONArray.toString());

            final JSONArray finalJSONArray = jSONArray;
            AlertDialog.Builder dialog2 = new AlertDialog.Builder(Main2Activity.this);
            dialog2.setTitle("友情提示");
//            dialog2.setMessage("您已经投了 "+count+" 票同意,确定提交么");
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size()-1) str.append(list.get(i)+"号 ");
                else str.append(list.get(i) + ",");
            }
            if (str.length()==0 || str == null) {
                dialog2.setMessage("您没有选择候选对象,确定提交么？");
            } else if (count == listcheck.size()) {
                dialog2.setMessage("您全部选择了 同意 ,确定提交么？");
            } else {
                dialog2.setMessage("您已经投了 "+str+" 同意票,确定提交么？");
            }
            dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestBody = new FormBody.Builder()
                            .add("aid", "" + paramInt1).add("uid", "" + paramInt2).add("result", finalJSONArray.toString()).build();
                    final Request request = new Request.Builder()
                            .url("http://"+ PublicUtil.ip+"/conference/index/index/pollExcel")
                            .post(requestBody)
                            .build();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Response response = null;
                            try {
                                response = client.newCall(request).execute();
                                log(response.toString());
                                if (response.isSuccessful()) {
                                    handler4.obtainMessage(1, response.body().string()).sendToTarget();
                                } else {
                                    throw new IOException("Unexception code:" + response);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    submit.setEnabled(true);
                    submitexcel.setEnabled(true);
                    suggess.setEnabled(true);
                    submit.setAlpha(1.0F);
                    submitexcel.setAlpha(1.0F);
                    submit.setBackgroundResource(R.drawable.radius);
                    submitexcel.setBackgroundResource(R.drawable.radius);

                    return;
                }
            });
            dialog2.show();


        }
        if (this.fileType == 0) {
            requestBody = new FormBody.Builder()
                    .add("aid", "" + paramInt1).add("uid", "" + paramInt2).add("content", "" + paramString).add("status", "" + paramInt3).build();
            final Request request = new Request.Builder()
                    .url("http://"+ PublicUtil.ip+"/conference/index/index/poll")
                    .post(requestBody)
                    .build();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            handler4.obtainMessage(1, response.body().string()).sendToTarget();
                        } else {
                            throw new IOException("Unexception code:" + response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void postRequest3(final int aid) {
        log(aid + "aaa");
        requestBody = new FormBody.Builder()
                .add("aid", "" + aid).build();
        final Request request = new Request.Builder()
                .url("http://"+ PublicUtil.ip+"/conference/index/index/showPdf")
                .post(requestBody)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        handler2.obtainMessage(1, aid, 0, response.body().string()).sendToTarget();
                    } else {
                        throw new IOException("Unexception code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void postRequest4(final int aid) {
        requestBody = new FormBody.Builder()
                .add("aid", "" + aid).build();
        final Request request = new Request.Builder()
                .url("http://"+ PublicUtil.ip+"/conference/index/index/showExcel")
                .post(requestBody)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        handler3.obtainMessage(1, aid, 0, response.body().string()).sendToTarget();
                    } else {
                        throw new IOException("Unexception code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void updateLayout() {
        pdf_root.removeAllViewsInLayout();
        pdf_root.addView(this.remotePDFViewPager, (getResources().getDisplayMetrics()).widthPixels, (getResources().getDisplayMetrics()).heightPixels);
    }

    public void log(String paramString) { Log.e("----------------", "log: " + paramString); }

    public void onBackPressed() { toast("不能返回！"); }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            default:
                return;
            case R.id.submit:
                String str = this.suggess.getText().toString();
                int b = 0;
                if (this.button1.isChecked()) {
                    b = 1;
                } else if (this.button2.isChecked()) {
                    b = 0;
                } else if (this.button3.isChecked()) {
                    b = 2;
                }
                submit.setEnabled(false);
                submitexcel.setEnabled(false);
                suggess.setEnabled(false);
                submit.setBackgroundResource(R.drawable.radiusun);
                submitexcel.setBackgroundResource(R.drawable.radiusun);
                submit.setAlpha(0.5F);
                submitexcel.setAlpha(0.5F);
                aBoolean = false;
                postRequest2(this.finalId, this.uid, str, b);  //b表示pdf的同意不同意
                return;
            case R.id.submitexcel:
                break;
        }
        submit.setEnabled(false);
        submitexcel.setEnabled(false);
        suggess.setEnabled(false);
        submit.setBackgroundResource(R.drawable.radiusun);
        submitexcel.setBackgroundResource(R.drawable.radiusun);
        submit.setAlpha(0.5F);
        submitexcel.setAlpha(0.5F);
        aBoolean = false;
        postRequest2(this.finalId, this.uid, "", 0);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main2);
        initView();
        initViewWelcome();
        Intent intent = getIntent();

        String str2 = intent.getStringExtra("name");

        this.uid = intent.getIntExtra("id", 0);
        if (str2 != null) {
            String str = "";
            try {
                JSONObject jSONObject = new JSONObject(str2);
                this.name = jSONObject.getString("name");
                str = jSONObject.getString("committee");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.welcome_text.setText(this.name);
            this.welcome_text0.setText(str);
        }

        (new Timer()).scheduleAtFixedRate(new TimerTask() {
            public void run() { Main2Activity.this.postRequest1(); }
        },  1000L, 5000L);
    }

    public void onFailure(Exception paramException) {
        this.pb_bar.setVisibility(View.GONE);
        onPdfBack(null);
        toast("数据加载错误");
    }

    public void onPdfBack(View paramView) {
        pdf_back.setVisibility(View.GONE);
        bottom.setVisibility(View.VISIBLE);
        pdf_root.setVisibility(View.GONE);
        excel.setVisibility(View.VISIBLE);
    }

    public void onProgressUpdate(int paramInt1, int paramInt2) {}

    public void onSuccess(String paramString1, String paramString2) {
        pb_bar.setVisibility(View.GONE);
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(paramString1));
        remotePDFViewPager.setAdapter(this.adapter);
        updateLayout();
    }

    protected void setDownloadListener(boolean paramBoolean) {
        if (paramBoolean) {
            bottom.setVisibility(View.GONE);
            pdf_back.setVisibility(View.VISIBLE);
        } else {
            bottom.setVisibility(View.VISIBLE);
            pdf_back.setVisibility(View.GONE);
        }
        pb_bar.setVisibility(View.VISIBLE);
        log(this.mUrl);
        remotePDFViewPager = new RemotePDFViewPager(this, this.mUrl, this);
        remotePDFViewPager.setId(R.id.pdfViewPager);
    }


    public void toast(String paramString) { Toast.makeText(this, paramString, Toast.LENGTH_LONG).show(); }
}
