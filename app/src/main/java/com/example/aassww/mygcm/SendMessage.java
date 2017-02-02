package com.example.aassww.mygcm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SendMessage extends Activity {

    private final String BRIAN_LINK = "http://brian.uts-uka.com/listphonenumber/sendlist";
    String data = "";
    String g_sms_id = "";
    ArrayList<String>forward_ids = new ArrayList<String>();
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    // 전송 성공
                    insert(200);
                    Log.i("SMS status", "Send msg complete");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    // 전송 실패
                    insert(400);
                    Log.i("SMS status", "Send msg fail");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    // 서비스 지역 아님
                    insert(400);
                    Log.i("SMS status", "not service area");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    // 무선 꺼짐
                    insert(400);
                    Log.i("SMS status", "turned off wireless");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    // PDU 실패
                    insert(400);
                    Log.i("SMS status", "PDU NULL");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_message);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        Intent intent = getIntent();
        data = intent.getStringExtra("message");

        phpDown task;
        task = new phpDown();
        task.execute(data);

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(data);

    }
    public void insert(int num)
    {
        for(int i = 0; i<forward_ids.size();i++) {
            InsertToDatabase itd;
            itd = new InsertToDatabase(g_sms_id, forward_ids.get(i), Integer.toString(num), "");
            itd.insertInSms_log();
        }
        forward_ids.clear();
    }
    public void onClick(View v)
    {
        finish();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void send(String num, String ms) {
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        registerReceiver(broadcastReceiver,new IntentFilter("SMS_SENT_ACTION"));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(num, null, ms, sentIntent, deliveredIntent);

    }

    private class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... poi_id) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정String minorid = (String) params[0];
                String msg = poi_id[0];
                String link = BRIAN_LINK;
                String message = URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8");

                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(message);
                wr.flush();
                // 커넥션 객체 생성
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();

                            Log.i("Stringline4", line);

                            if(line.startsWith("<b"))
                                continue;
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    String phone = jo.getString("number_to");
                    String sms_id = jo.getString("sms_id");
                    String forward_id = jo.getString("sms_forward_id");
                    g_sms_id = sms_id;
                    forward_ids.add(forward_id);

                    send(phone,data);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}