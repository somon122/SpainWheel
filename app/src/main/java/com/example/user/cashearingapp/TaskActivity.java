package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener,RewardedVideoAdListener {

    TextView timeTV;
    Button startBtn;
 /*   CountDownTimer countDownTimer;
    long timeLeft = 10000;
    boolean timeRunning;
    String timeText;*/

    TextView task1,task2,task3,task4,task5,task6,task7,task8,task9,task10;
    TextView finalTask;
    TextView counterId, mainBalanceId;
    int count = 1;
    int mainBalance = 0;
    private InterstitialAd mInterstitialAd;
    SharedPreferences myScore;
    private ProgressBar progressBar;
    private int progress;
    ProgressDialog dialog;

    CountDownTimer countDownTimer;
    long timeLeft = 10000;
    boolean timeRunning;
    String timeText;
    Button rawaerdVideo;


    FirebaseDatabase database;
    DatabaseReference myRef;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;


    private RewardedVideoAd mRewardedVideoAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);




        timeTV = findViewById(R.id.timeTvId);
        startBtn = findViewById(R.id.startButtonId);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");
        balanceSetUp= new BalanceSetUp();
        clickBalanceControl=new ClickBalanceControl();

       /* startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starStop();

            }
        });
        updateTimer();
        */


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        rawaerdVideo = findViewById(R.id.rewerdVideoButton_id);

        rawaerdVideo.setEnabled(false);

        initialized();


        myRef.child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()){

                    String value = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(value));
                    mainBalanceId.setText("MainBalance: "+balanceSetUp.getBalance());

                }else {
                    Toast.makeText(TaskActivity.this, "Data Empty..", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        myRef.child("LoveBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()){

                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));
                    counterId.setText("Show: "+clickBalanceControl.getBalance());

                  /*  myScore = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myScore.edit();
                    editor.putInt("score", clickBalanceControl.getBalance());
                    editor.commit();*/

                }else {
                    Toast.makeText(TaskActivity.this, "Data Empty..", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });





        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if (clickBalanceControl.getBalance() >=10){


                   /* count-=10;
                    myScore = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor =  myScore.edit();
                    editor.putInt("score",count);
                    editor.commit();*/


                    //counterId.setText(""+count);
                    Intent intent = new Intent(TaskActivity.this,Click_Activity.class);
                    intent.putExtra("click","love");
                    startActivity(intent);
                    finish();


                }else {



                    mainBalance = mainBalance+5;

                    balanceSetUp.AddBalance(mainBalance);
                    String updateBalance = String.valueOf(balanceSetUp.getBalance());
                    myRef.child("MainBalance").setValue(updateBalance);

                    count++;
                    clickBalanceControl.AddBalance(count);
                    String showBalance = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child("LoveBalance").setValue(showBalance);

                    progressBar.setVisibility(View.GONE);
                    startStop();


                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(TaskActivity.this,MainActivity.class));
        finish();

    }

    private void initialized(){
        dialog = new ProgressDialog(this);
        task1 = findViewById(R.id.task1);
        task2 = findViewById(R.id.task2);
        task3 = findViewById(R.id.task3);
        task4 = findViewById(R.id.task4);
        task5 = findViewById(R.id.task5);
        task6 = findViewById(R.id.task6);
        task7 = findViewById(R.id.task7);
        task8 = findViewById(R.id.task8);
        task9 = findViewById(R.id.task9);
        task10 = findViewById(R.id.task10);
        finalTask = findViewById(R.id.finalTask);
        counterId = findViewById(R.id.counterId);
        mainBalanceId = findViewById(R.id.mainBalance_id);
        progressBar = findViewById(R.id.progressBarId);

        progressBar.setVisibility(View.GONE);

        task1.setOnClickListener(this);
        task2.setOnClickListener(this);
        task3.setOnClickListener(this);
        task4.setOnClickListener(this);
        task5.setOnClickListener(this);
        task6.setOnClickListener(this);
        task7.setOnClickListener(this);
        task8.setOnClickListener(this);
        task9.setOnClickListener(this);
        task10.setOnClickListener(this);
        finalTask.setOnClickListener(this);
        counterId.setOnClickListener(this);

       /* myScore = this.getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
        count = myScore.getInt("score", 1);

*/



        if (clickBalanceControl.getBalance() >= 2) {
            task1.setBackgroundResource(R.drawable.full_love);

        }

        if (clickBalanceControl.getBalance() >= 3) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);

        }


        if (clickBalanceControl.getBalance() >= 4) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);

        }


        if (clickBalanceControl.getBalance() >= 5) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);

        }


        if (clickBalanceControl.getBalance() >= 6) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);

        }


        if (clickBalanceControl.getBalance() >= 7) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
        }


        if (clickBalanceControl.getBalance() >= 8) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
            task7.setBackgroundResource(R.drawable.full_love);
        }


        if (clickBalanceControl.getBalance() >= 9) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
            task7.setBackgroundResource(R.drawable.full_love);
            task8.setBackgroundResource(R.drawable.full_love);

        }


        if (clickBalanceControl.getBalance() >= 10) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
            task7.setBackgroundResource(R.drawable.full_love);
            task8.setBackgroundResource(R.drawable.full_love);
            task9.setBackgroundResource(R.drawable.full_love);
        }


        if (clickBalanceControl.getBalance() >= 11) {

            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
            task7.setBackgroundResource(R.drawable.full_love);
            task8.setBackgroundResource(R.drawable.full_love);
            task9.setBackgroundResource(R.drawable.full_love);
            task10.setBackgroundResource(R.drawable.full_love);

        }

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.task1)
        {

            if (clickBalanceControl.getBalance() == 1){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft > 9999) {
                        mInterstitialAd.show();
                        task1.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please Check your net Connection", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Please try again ", Toast.LENGTH_SHORT).show();
            }

            //progressBar.setVisibility(View.VISIBLE);
           /* loadTask();
          firstTask();*/

        }
        if (v.getId() == R.id.task2){
            if (clickBalanceControl.getBalance() == 2){

                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task2.setBackgroundResource(R.drawable.full_love);
                    }else {
                        Toast.makeText(this, "Not Work", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }


        } if (v.getId() == R.id.task3){
            if (clickBalanceControl.getBalance() ==3){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999 ) {
                        mInterstitialAd.show();
                        task3.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }


        }if (v.getId() == R.id.task4){
            if (clickBalanceControl.getBalance() ==4){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task4.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }



        }if (v.getId() == R.id.task5){
            if (clickBalanceControl.getBalance() == 5) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task5.setBackgroundResource(R.drawable.full_love);
                    }
                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }if (v.getId() == R.id.task6){

            if (clickBalanceControl.getBalance() == 6) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task6.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }if (v.getId() == R.id.task7){

            if (clickBalanceControl.getBalance() == 7) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task7.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }
        }if (v.getId() == R.id.task8){

            if (clickBalanceControl.getBalance() == 8) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task8.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }if (v.getId() == R.id.task9){
            if (clickBalanceControl.getBalance() == 9) {
                if (mInterstitialAd.isLoaded()) {

                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task9.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }if (v.getId() == R.id.task10){
            if (clickBalanceControl.getBalance() == 10) {
                if (mInterstitialAd.isLoaded()) {

                    if (timeLeft >9999) {
                        mInterstitialAd.show();
                        task10.setBackgroundResource(R.drawable.full_love);
                    }


                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }
        }if (v.getId() == R.id.finalTask){

            if (clickBalanceControl.getBalance() > 10){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft >9999) {
                        mInterstitialAd.show();

                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void startStop() {
        if (timeRunning){
            stopTime();
        }else {
            startTime();
        }

    }


    private void startTime() {
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft =millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                Toast.makeText(TaskActivity.this, " Next Task ready for you ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TaskActivity.this,TaskActivity.class));
            }
        }.start();
        timeRunning = true;
        //startBtn.setText("Pause");

    }

    private void updateTimer() {

        dialog.show();
        int minutes = (int) (timeLeft /60000);
        int seconds = (int) (timeLeft % 60000 /1000);
        timeText = ""+minutes;
        timeText += ":";
        if (seconds <10)timeText += "0";
        timeText +=seconds;
        // timeTV.setText(timeText);


    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        dialog.dismiss();
        // startBtn.setText("Start");



    }

    private void loadRewardedVideoAd() {

        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }



    @Override
    public void onRewardedVideoAdLoaded() {

        rawaerdVideo.setEnabled(true);


    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        loadRewardedVideoAd();

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }




    public void RawaedVideo(View view) {

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }else {
            Toast.makeText(this, "Please Try Aging.... ", Toast.LENGTH_SHORT).show();
        }


    }

    private void gameLoaded(){

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);

        builder.setMessage("Great Work ..!" +
                "\n"+ " Click Ok  For Continue Game ...")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(getApplicationContext(),TaskActivity.class));
                        finish();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }







    /*


    private void starStop() {
        if (timeRunning){
            stopTime();
        }else {
            startTime();
        }

    }

    private void startTime() {
         countDownTimer = new CountDownTimer(timeLeft,1000) {
             @Override
             public void onTick(long millisUntilFinished) {
                  timeLeft =millisUntilFinished;
                  updateTimer();

             }

             @Override
             public void onFinish() {

                 Toast.makeText(TaskActivity.this, "Time finished", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(TaskActivity.this,TaskActivity.class));
             }
         }.start();
         timeRunning = true;
         startBtn.setText("Pause");

    }

    private void updateTimer() {
        int minutes = (int) (timeLeft /60000);
        int seconds = (int) (timeLeft % 60000 /1000);
        timeText = ""+minutes;
        timeText += ":";
        if (seconds <10)timeText += "0";
        timeText +=seconds;
        timeTV.setText(timeText);


    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        startBtn.setText("Start");



    }

    public void TestMode(View view) {

        if (timeLeft < 9999){

            Toast.makeText(this, "Test is successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TaskActivity.this,TaskActivity.class));

        }else {
            Toast.makeText(this, "Test is Field", Toast.LENGTH_SHORT).show();
        }


    }
*/
}
