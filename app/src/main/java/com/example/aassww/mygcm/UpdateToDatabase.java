package com.example.aassww.mygcm;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by aassw on 2017-01-25.
 */
public class UpdateToDatabase {
    private String table;
    private String col;
    private String tuple;
    private String where_col;
    private String where_tup;

    private final String BRIAN_LINK = "http://brian.uts-uka.com/sms/update_sms";

    public UpdateToDatabase(String table, String c, String t, String wc, String wt)
    {
        setTable(table);setCol(c);setTuple(t);setWhere_col(wc);setWhere_tup(wt);
    }
    public void setTable(String table) {
        this.table = table;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public void setTuple(String tuple) {
        this.tuple = tuple;
    }

    public void setWhere_col(String where_col) {
        this.where_col = where_col;
    }

    public void setWhere_tup(String where_tup) {
        this.where_tup = where_tup;
    }
    public void updateDB()
    {
        update();
    }

    private void update() {

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
//              loading.dismiss();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    String link = BRIAN_LINK;
                    String data = URLEncoder.encode("table", "UTF-8") + "=" + URLEncoder.encode(table, "UTF-8");
                    data += "&" + URLEncoder.encode("col", "UTF-8") + "=" + URLEncoder.encode(col, "UTF-8");
                    data += "&" + URLEncoder.encode("tuple", "UTF-8") + "=" + URLEncoder.encode(tuple, "UTF-8");
                    data += "&" + URLEncoder.encode("where_col", "UTF-8") + "=" + URLEncoder.encode(where_col, "UTF-8");
                    data += "&" + URLEncoder.encode("where_tup", "UTF-8") + "=" + URLEncoder.encode(where_tup, "UTF-8");

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
                }
                catch (Exception e) {
                    return new String("Exception: " + e.getMessage());

                }

            }
        }
        InsertData task = new InsertData();
        task.execute();
    }
}
