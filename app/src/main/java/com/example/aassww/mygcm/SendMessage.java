package com.example.aassww.mygcm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessage extends AppCompatActivity {

    public String data = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        Intent intent = getIntent();
        data = intent.getStringExtra("message");

        phpDown task;
        task = new phpDown();
        task.execute();

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(data);
    }

    private void send(String num, String ms) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(num, null, ms, null, null);
    }

    private class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... poi_id) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정String minorid = (String) params[0];
                String link = "http://tripjuvo.ivyro.net/fcm/listPhonenumber.php";
                //    String data = URLEncoder.encode("poi_id", "UTF-8") + "=" + URLEncoder.encode(poi_id[0], "UTF-8");
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                //   wr.write(data);
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

                    String phone = jo.getString("phoneNumber");
                    Log.i("SENDSMS",data);
                    send(phone,data);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
