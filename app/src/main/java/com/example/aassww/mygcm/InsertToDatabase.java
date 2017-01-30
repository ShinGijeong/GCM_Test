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
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aassw on 2017-01-30.
 */

public class InsertToDatabase {
    final String BRIAN_LINK = "http://brian.uts-uka.com/sms/add_sms_log";
    private String sms_id;
    private String date;
    private String status;

    public InsertToDatabase(String id, String d, String st) {
        if(d == "") {
            Calendar cal = Calendar.getInstance();
            d = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE)+" "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
            setDate(d);
        }

        else
            setDate(d);

        setSms_id(id);
        setStatus(st);

        insertInSms_log();

    }

    public String getSms_id() {
        return sms_id;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setSms_id(String sms_id) {
        this.sms_id = sms_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void insertInSms_log() {

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
                //loading.dismiss();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    String link = BRIAN_LINK;
                    String data = URLEncoder.encode("sms_id", "UTF-8") + "=" + URLEncoder.encode(sms_id, "UTF-8");
                    data += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");
                    data += "&" + URLEncoder.encode("reg_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");

                    Log.i("datadata",data);
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
                    Log.i("ASDFASDF",sb.toString());
                    return sb.toString();
                }
                catch (Exception e) {
                    Log.i("ASDFASDF1",e.getMessage());
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute();
    }
}
