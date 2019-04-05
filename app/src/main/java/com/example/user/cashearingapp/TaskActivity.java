package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    TextView timeTV2;
    Button startBtn;
    TextView task1,task2,task3,task4,task5,task6,task7,task8,task9,task10,task11,task12;
    TextView finalTask;
    TextView counterId, mainBalanceId;
    int count = 0;
    int myCount = 0;
    int mainBalance = 0;

    int clickPoint = 0;
    int clickCount = 0;
    int warningCount = 0;
    int warningScore = 0;

    private InterstitialAd mInterstitialAd;

    SharedPreferences myScore;
    SharedPreferences myScore2;
    SharedPreferences myScore3;

    private ProgressBar progressBar;
    ProgressDialog dialog;

    CountDownTimer countDownTimer;
    long timeLeft = 10000;
    boolean timeRunning;
    String timeText;

    CountDownTimer countDownTimer2;
    long timeLeft2 = 29000;
    boolean timeRunning2;
    String timeText2;

    Button rawaerdVideo;
    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    String phoneNo;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;
    private RewardedVideoAd mRewardedVideoAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        timeTV2 = findViewById(R.id.timeTvId);
        startBtn = findViewById(R.id.startButtonId);

        timeTV2.setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        balanceSetUp= new BalanceSetUp();
        clickBalanceControl=new ClickBalanceControl();
        phoneNo = user.getPhoneNumber();


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

      /*  mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);*/
        //loadRewardedVideoAd();

        initialized();


        myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()){

                    progressBar.setVisibility(View.GONE);
                    String value = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(value));
                    mainBalanceId.setText("MainBalance: "+balanceSetUp.getBalance());

                }else {
                    progressBar.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        myRef.child("Users").child(phoneNo).child(uID).child("LoveBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()){

                    progressBar.setVisibility(View.GONE);
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));
                    counterId.setText("Show: "+clickBalanceControl.getBalance());

                }else {

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
        myScore = this.getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
        myCount = myScore.getInt("score",0);

        myScore3 = this.getSharedPreferences("YourWarningScore", Context.MODE_PRIVATE);
        warningCount = myScore3.getInt("warningScore",0);

        completeTask();



        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {


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


                warningMethod();

            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the interstitial ad is closed.

                if (clickBalanceControl.getBalance()==15){

                    Intent intent = new Intent(TaskActivity.this,Click_Activity.class);
                    intent.putExtra("click","love");
                    startActivity(intent);
                    finish();


                }else {
                    if (myCount >=12){

                        myCount = myCount-12;
                        myScore = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myScore.edit();
                        editor.putInt("score", myCount);
                        editor.commit();

                        mainBalanceAddPoint();
                        progressBar.setVisibility(View.VISIBLE);
                        hideWork();
                        starStop2();


                    }else {

                        mainBalanceAddPoint();

                        count++;
                        myCount++;

                        myScore = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myScore.edit();
                        editor.putInt("score", myCount);
                        editor.commit();


                        clickBalanceControl.AddBalance(count);
                        String showBalance = String.valueOf(clickBalanceControl.getBalance());
                        myRef.child("Users").child(phoneNo).child(uID).child("LoveBalance").setValue(showBalance);

                        progressBar.setVisibility(View.VISIBLE);
                        re_Loaded();


                    }

                }


            }
        });



    }

    private void mainBalanceAddPoint() {
        mainBalance = mainBalance+1;

        balanceSetUp.AddBalance(mainBalance);
        String updateBalance = String.valueOf(balanceSetUp.getBalance());
        myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(updateBalance);
    }

    private void warningMethod() {

        if (warningCount>=3){

            mainBalance = mainBalance-10;
            balanceSetUp.Withdraw(mainBalance);
            String updateBalance = String.valueOf(balanceSetUp.getBalance());
            myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(TaskActivity.this, "10 point is Minus...!\n Don't Mistake Again ok.", Toast.LENGTH_SHORT).show();
                    }else {


                    }
                }
            });

        }else {

            warningToast();
            warningScore++;
            myScore3 = getSharedPreferences("YourWarningScore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myScore3.edit();
            editor.putInt("warningScore",warningScore);
            editor.commit();

        }

    }

    private void hideWork(){

        task1.setEnabled(false);
        task2.setEnabled(false);
        task3.setEnabled(false);
        task4.setEnabled(false);
        task5.setEnabled(false);
        task6.setEnabled(false);
        task7.setEnabled(false);
        task8.setEnabled(false);
        task9.setEnabled(false);
        task10.setEnabled(false);
        task11.setEnabled(false);
        task12.setEnabled(false);
        finalTask.setEnabled(false);


    }
      private void showWork(){

        task1.setEnabled(true);
        task2.setEnabled(true);
        task3.setEnabled(true);
        task4.setEnabled(true);
        task5.setEnabled(true);
        task6.setEnabled(true);
        task7.setEnabled(true);
        task8.setEnabled(true);
        task9.setEnabled(true);
        task10.setEnabled(true);
        task11.setEnabled(true);
        task12.setEnabled(true);
        finalTask.setEnabled(true);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        task11 = findViewById(R.id.task11);
        task12 = findViewById(R.id.task12);
        finalTask = findViewById(R.id.finalTask);

        counterId = findViewById(R.id.counterId);
        mainBalanceId = findViewById(R.id.mainBalance_id);
        progressBar = findViewById(R.id.progressBarId);

        progressBar.setVisibility(View.VISIBLE);

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
        task11.setOnClickListener(this);
        task12.setOnClickListener(this);
        finalTask.setOnClickListener(this);
        counterId.setOnClickListener(this);
        hideWork();
        startStop();

   }


    private void completeTask(){



        if (myCount == 1) {
            task1.setBackgroundResource(R.drawable.full_love);

        }

        if (myCount == 2) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);

        }


        if (myCount == 3) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);

        }


        if (myCount == 4) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);

        }


        if (myCount == 5) {

            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);

        }


        if (myCount == 6) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
        }


        if (myCount == 7) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
            task7.setBackgroundResource(R.drawable.full_love);
        }


        if (myCount == 8) {
            task1.setBackgroundResource(R.drawable.full_love);
            task2.setBackgroundResource(R.drawable.full_love);
            task3.setBackgroundResource(R.drawable.full_love);
            task4.setBackgroundResource(R.drawable.full_love);
            task5.setBackgroundResource(R.drawable.full_love);
            task6.setBackgroundResource(R.drawable.full_love);
            task7.setBackgroundResource(R.drawable.full_love);
            task8.setBackgroundResource(R.drawable.full_love);

        }


        if (myCount == 9) {
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


        if (myCount == 10) {

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

        } if (myCount == 11) {

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
            task11.setBackgroundResource(R.drawable.full_love);

        } if (myCount == 12) {

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
            task11.setBackgroundResource(R.drawable.full_love);
            task12.setBackgroundResource(R.drawable.full_love);

        }



    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.task1)
        {

            if (myCount== 0){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft < 9999) {
                        mInterstitialAd.show();
                         task1.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please Check your net Connection", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Please try again ", Toast.LENGTH_SHORT).show();
            }


        }
        if (v.getId() == R.id.task2){
            if (myCount == 1){

                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
                        mInterstitialAd.show();
                        task2.setBackgroundResource(R.drawable.full_love);
                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }


        } if (v.getId() == R.id.task3){
            if (myCount ==2){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999 ) {
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
            if (myCount ==3){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
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
            if (myCount == 4) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
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

            if (myCount == 5) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
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

            if (myCount == 6) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
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

            if (myCount == 7) {
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
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
            if (myCount == 8) {
                if (mInterstitialAd.isLoaded()) {

                    if (timeLeft <9999) {
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
            if (myCount == 9) {
                if (mInterstitialAd.isLoaded()) {

                    if (timeLeft <9999) {
                        mInterstitialAd.show();
                        task10.setBackgroundResource(R.drawable.full_love);
                    }


                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }
        }if (v.getId() == R.id.task11){

            if (myCount == 10){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
                        mInterstitialAd.show();

                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }if (v.getId() == R.id.task12){

            if (myCount == 11){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
                        mInterstitialAd.show();

                    }

                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please Complete previous Task ", Toast.LENGTH_SHORT).show();
            }

        }if (v.getId() == R.id.finalTask){

            if (myCount >= 12){
                if (mInterstitialAd.isLoaded()) {
                    if (timeLeft <9999) {
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
               progressBar.setVisibility(View.GONE);
                showWork();
                Toast.makeText(TaskActivity.this, " Next Task ready for you ", Toast.LENGTH_SHORT).show();
            }
        }.start();
        timeRunning = true;
        //startBtn.setText("Pause");

    }

    private void updateTimer() {

      progressBar.setVisibility(View.VISIBLE);
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

/*    private void loadRewardedVideoAd() {

        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }



    @Override
    public void onRewardedVideoAdLoaded() {



    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        mainBalance = mainBalance+5;

        balanceSetUp.AddBalance(mainBalance);
        String updateBalance = String.valueOf(balanceSetUp.getBalance());
        myRef.child(phoneNo).child(uID).child("MainBalance").setValue(updateBalance);

        count++;
        myCount++;

        myScore = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myScore.edit();
        editor.putInt("score", myCount);
        editor.commit(); 
        
        clickBalanceControl.AddBalance(count);
        String showBalance = String.valueOf(clickBalanceControl.getBalance());
        myRef.child(phoneNo).child(uID).child("LoveBalance").setValue(showBalance);
        re_Loaded();


    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

        warningMethod();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }*/


    private void starStop2() {
        if (timeRunning2){
            stopTime2();
        }else {
            startTime2();
        }

    }

    private void startTime2() {
         countDownTimer2 = new CountDownTimer(timeLeft2,1000) {
             @Override
             public void onTick(long millisUntilFinished) {
                  timeLeft2 =millisUntilFinished;
                  updateTimer2();

             }

             @Override
             public void onFinish() {

                 Toast.makeText(TaskActivity.this, "Time is finished", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(TaskActivity.this,TaskActivity.class));
                 finish();
             }
         }.start();
         timeRunning2 = true;

    }

    private void updateTimer2() {

        timeTV2.setVisibility(View.VISIBLE);
        int minutes = (int) (timeLeft2 /60000);
        int seconds = (int) (timeLeft2 % 60000 /1000);
        timeText2 = ""+minutes;
        timeText2 += ":";
        if (seconds <10)timeText2 += "0";
        timeText2 +=seconds;
        timeTV2.setText(timeText2);


    }

    private void stopTime2() {
        countDownTimer2.cancel();
        timeRunning2 = false;
        //startBtn.setText("Start");



    }

    private void warningToast(){

        LayoutInflater inflater = getLayoutInflater();

        View toastView = inflater.inflate(R.layout.warning_layout, (ViewGroup) findViewById(R.id.warningToast_id));

        Toast toast = new Toast(TaskActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(toastView);
        toast.show();


    }




    private void re_Loaded(){

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




}
