package com.example.aassww.mygcm;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Checknumber extends AppCompatActivity {

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checknumber);
        showList();
        Button insertButton = (Button) findViewById(R.id.insertNumber);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText)findViewById(R.id.editNumber);
                String number = et.getText().toString();
                InsertNumber insertnumber = new InsertNumber();
                insertnumber.sendToDatabase(number);
                et.setText("");
                arrayList.add(number);
                arrayAdapter.notifyDataSetChanged();
            }
        });

    }
    public void onClick(View v)
    {
        removeNumber rn = new removeNumber();
        SparseBooleanArray sb = list.getCheckedItemPositions();
        if(sb.size() != 0) {
            for(int i = list.getCount() - 1; i >= 0 ; i--) {
                if(sb.get(i)) {
                    rn.sendToDatabase(arrayList.get(i).toString());
                    arrayList.remove(i);
                }
            }
            list.clearChoices();
            arrayAdapter.notifyDataSetChanged();
        }
    }
    public void showList()
    {
        phpDown task;
        task = new phpDown();
        task.execute();
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
                    arrayList.add(phone);

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            inputList();
        }
    }
    public void inputList()
    {
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,arrayList);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(arrayAdapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

}
