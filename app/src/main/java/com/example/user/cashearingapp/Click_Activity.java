package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Click_Activity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    String getValue;

    ProgressBar progressBar;

    private InterstitialAd mInterstitialAd;

    CountDownTimer countDownTimer;
    long timeLeft = 50000;
    boolean timeRunning;
    String timeText;

    FirebaseAuth auth;
    FirebaseUser user;

    ClickBalanceControl clickBalanceControl;
    BalanceSetUp balanceSetUp;
    InvalidClickControler invalidClickControler;
    Button clickButton;

    int clickScore=0;
    int mainScore=0;
    int invalidCount=0;

    String uID;
    String phoneNo;
    String updateInvalidScore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_);




        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        clickBalanceControl = new ClickBalanceControl();
        balanceSetUp = new BalanceSetUp();
        invalidClickControler = new InvalidClickControler();

        clickButton = findViewById(R.id.CompleteClick);
       progressBar = findViewById(R.id.clickProgressBar_id);

        uID = user.getUid();
        phoneNo = user.getPhoneNumber();



        clickButton.setVisibility(View.GONE);

        balanceControl();

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());



        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            getValue = bundle.getString("click");
            Toast.makeText(this, " Welcome to bonus point area....!", Toast.LENGTH_SHORT).show();

        }


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                adIsLoaded();

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

                rulesToast();
                startStop();


            }

            @Override
            public void onAdClosed() {


                if (clickScore==1){
                    successToast();
                   Intent intent = new Intent(Click_Activity.this,MainActivity.class);
                   intent.putExtra("completed","completed");
                   startActivity(intent);
                   finish();


                }else {

                    invalidCount++;
                    invalidClickControler.AddBalance(invalidCount);
                    updateInvalidScore = String.valueOf(invalidClickControler.getBalance());

                    myRef.child("Users").child(phoneNo).child(uID).child("InvalidClick").setValue(updateInvalidScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                String pushId = myRef.push().getKey();
                                InvalidClickClass invalidClickClass = new InvalidClickClass(phoneNo,updateInvalidScore);
                                myRef.child("InvalidClick").child(pushId).setValue(invalidClickClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            sorryToast();

                                        }else {
                                            Toast.makeText(Click_Activity.this, "Slow net Connection...", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(Click_Activity.this, "Slow net Connection...", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }else {
                                Toast.makeText(Click_Activity.this, "Slow net Connection...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Click_Activity.this, "Slow net Connection...", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });





    }



    public void CompleteButton(View view) {

    mInterstitialAd.show();

    }

    @Override
    public void onBackPressed() {
        if (timeLeft > 49999){
            super.onBackPressed();
        }

    }

    private void clickScoreControl(){

        if (getValue.equals("wheel")){
            myRef.child("Users").child(phoneNo).child(uID).child("ClickBalance").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        balanceAdd();                    }
                        else {
                        Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            });
            balanceAdd();



        }  else if (getValue.equals("love")){

            myRef.child("Users").child(phoneNo).child(uID).child("LoveBalance").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        balanceAdd();                    }
                        else {
                        Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            });




        }  else if (getValue.equals("question")){
            myRef.child("Users").child(phoneNo).child(uID).child("QuestionBalance").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        balanceAdd();

                    }else {

                        Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            });



        }else {
            Toast.makeText(this, " Did not match", Toast.LENGTH_SHORT).show();
        }



    }

    private void balanceAdd(){

        clickBalanceControl.AddBalance(clickScore);
        String updateScore= String.valueOf(clickBalanceControl.getBalance());
        myRef.child("Users").child(phoneNo).child(uID).child("ClickCount").setValue(updateScore).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    balanceSetUp.AddBalance(mainScore);
                    String MainBalance_updateScore= String.valueOf(balanceSetUp.getBalance());
                    myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(MainBalance_updateScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Click_Activity.this, "Well Done..! \nYour get 10 bonus point..", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Click_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void adIsLoaded() {

        if (mInterstitialAd.isLoaded()){

            clickButton.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check your Net Connections", Toast.LENGTH_SHORT).show();
        }

    }

    private void balanceControl() {


        myRef.child("Users").child(phoneNo).child(uID).child("ClickCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String mainValue = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(mainValue));


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        myRef.child("Users").child(phoneNo).child(uID).child("InvalidClick").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String invalidClickValue = dataSnapshot.getValue(String.class);
                    invalidClickControler.setBalance(Integer.parseInt(invalidClickValue));

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

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
                clickScore++;
                mainScore = mainScore+10;


                try {
                    clickScoreControl();

                }catch (Exception e){

                    Toast.makeText(Click_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                }

            }
        }.start();
        timeRunning = true;
        //startBtn.setText("Pause");

    }

    private void updateTimer() {

        int minutes = (int) (timeLeft /60000);
        int seconds = (int) (timeLeft % 60000 /1000);
        timeText = ""+minutes;
        timeText += ":";
        if (seconds <10)timeText += "0";
        timeText +=seconds;
        //countdownShow.setText(timeText);


    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        // startBtn.setText("Start");



    }

    private void sorryToast(){

        LayoutInflater inflater = getLayoutInflater();

        View toastView = inflater.inflate(R.layout.field_layout, (ViewGroup) findViewById(R.id.sorryToast_id));

        Toast toast = new Toast(Click_Activity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(toastView);
        toast.show();


    }

    private void successToast(){

        LayoutInflater inflater = getLayoutInflater();

        View toastView = inflater.inflate(R.layout.complete_task_layout, (ViewGroup) findViewById(R.id.successToast_id));

        Toast toast = new Toast(Click_Activity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(toastView);
        toast.show();


    }

    private void rulesToast(){

        LayoutInflater inflater = getLayoutInflater();

        View toastView = inflater.inflate(R.layout.rules_layout, (ViewGroup) findViewById(R.id.rulesToast_id));

        Toast toast = new Toast(Click_Activity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(toastView);
        toast.show();


    }


}
