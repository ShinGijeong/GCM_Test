package com.example.aassww.mygcm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SendMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Intent intent = getIntent();
        String data = intent.getStringExtra("message");

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(data);

    }
}
