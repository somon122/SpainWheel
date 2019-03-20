package com.example.user.cashearingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VideoShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);





    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(VideoShowActivity.this,MainActivity.class));
        finish();

    }
}
