package com.example.eyeattend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static int Delay_Time = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(MainActivity.this,TeacherLogin.class));
            }
        },Delay_Time);

    }
}