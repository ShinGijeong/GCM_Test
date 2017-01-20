package com.example.aassww.mygcm;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by aassw on 2017-01-19.
 */
public class removeNumber {

    public void sendToDatabase(String phone)
    {
        deleteToDatabase(phone);
    }
    private void deleteToDatabase(String number) {

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
                    Log.i("removeNumber1",num);
                    String link = "http://tripjuvo.ivyro.net/fcm/remove_number.php";
                    String data = URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8");

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
                    Log.i("removeNumber1",e.getMessage().toString());
                    return new String("Exception: " + e.getMessage());

                }
            }
        }
        InsertData task = new InsertData();
        task.execute(number);
    }
}

