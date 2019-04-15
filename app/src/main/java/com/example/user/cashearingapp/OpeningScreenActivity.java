package com.example.user.cashearingapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class OpeningScreenActivity extends AppCompatActivity {


    private int progress;
    private ProgressBar progressBar;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);




        if (HaveNetwork()){

            MobileAds.initialize(this,
                    getString(R.string.test_AppUnitId));
            mAdView = findViewById(R.id.openScreenBannerAdView_id);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            progressBar = findViewById(R.id.progressBar_id);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    doTheWork();
                    startApp();
                }
            });
            thread.start();

        }else {

            Toast.makeText(this, "Please connect your Internet first", Toast.LENGTH_SHORT).show();

        }

    }

    private void startApp() {
        startActivity(new Intent(OpeningScreenActivity.this,MainActivity.class));
        finish();
    }

    private void doTheWork() {

        for (progress = 10; progress <= 100; progress = progress+10){
            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    private boolean HaveNetwork() {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo){

            if (info.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (info.isConnected())
                {
                    have_WiFi = true;
                }
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))

            {
                if (info.isConnected())
                {
                    have_Mobile = true;
                }
            }

        }
        return have_WiFi || have_Mobile;

    }

}
