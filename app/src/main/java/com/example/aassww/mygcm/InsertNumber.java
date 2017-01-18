package com.example.aassww.mygcm;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class InsertNumber extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_number);

        Button insertButton = (Button) findViewById(R.id.insertButton);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToDatabase();
            }
        });
    }
   public void sendToDatabase()
   {
       EditText editphone = (EditText)findViewById(R.id.editNumber);
       EditText editmemo = (EditText)findViewById(R.id.editMemo);

       String number = editphone.getText().toString();
       String memo = editmemo.getText().toString();

       insertToDatabase(number,memo);

       Log.i("NUMBER",number);

       finish();

   }
    private void insertToDatabase(String number, String memo) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //    loading = ProgressDialog.show(SignupActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String num = (String) params[0];
                    String memo = (String) params[1];

                    String link = "http://tripjuvo.ivyro.net/fcm/insert_number.php";
                    String data = URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8");
                    data += "&" + URLEncoder.encode("memo", "UTF-8") + "=" + URLEncoder.encode(memo, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    Log.i("LLLL",e.getMessage());
                    return new String("Exception: " + e.getMessage());

                }

            }
        }
        InsertData task = new InsertData();
        task.execute(number,memo);
    }
}
