package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.util.Random;

public class QuestionWorkActivity extends AppCompatActivity implements View.OnClickListener {


    private Button answerButtonNo1,answerButtonNo2,answerButtonNo3,answerButtonNo4;
    private TextView scoreTV,questionTV,showScoreTV,noticeBoardTV;

    private Questions questions = new Questions();
     private String mAnswer;
     private int invalidCount = 0;
     private int mQuestionsLenght = questions.mQuestions.length ;
     Random r;
    private InterstitialAd mInterstitialAd;

    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;

    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;
    InvalidClickControler invalidClickControler;

    int mainBalance = 0 ;
    String uID;
    String phoneNo;

    SharedPreferences myScore3;
    int warningCount = 0;
    int warningScore = 0;

    CountDownTimer countDownTimer;
    long timeLeft = 10000;
    boolean timeRunning;
    String timeText;

    ImageView reLoad;
    ConstraintLayout constraintLayout;

    String updateInvalidScore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_work);

        reLoad = findViewById(R.id.questionPageReLoadImage_id);
        constraintLayout = findViewById(R.id.questionPageReLoad_id);

        reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QuestionWorkActivity.class));

            }
        });


        if (haveNetwork()){

            constraintLayout.setVisibility(View.GONE);
            initialized();
            progressBar.setVisibility(View.VISIBLE);
            phoneNo = user.getPhoneNumber();
            questionLoadMethod();

        }else {
            Toast.makeText(this, "Please Check Your Net Connection ..ok!", Toast.LENGTH_SHORT).show();
        }



    }


    private boolean haveNetwork() {
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



    private void questionLoadMethod(){


        myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    String value = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(value));
                    scoreTV.setText("Score : "+balanceSetUp.getBalance());

                }/*else {
                    Toast.makeText(QuestionWorkActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
                }*/


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        myRef.child("Users").child(phoneNo).child(uID).child("QuestionBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));
                    showScoreTV.setText("Show : "+clickBalanceControl.getBalance());

                }/*else {
                    Toast.makeText(QuestionWorkActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
                }
*/

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        myRef.child("NoticeBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    noticeBoardTV.setVisibility(View.VISIBLE);
                    String notice = dataSnapshot.getValue(String.class);
                    noticeBoardTV.setText(notice);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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




        myScore3 = this.getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
        warningCount = myScore3.getInt("warningScore",0);



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

                warningMethod();


            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the interstitial ad is closed.
                //mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if (clickBalanceControl.getBalance() >=30){

                    questionTV.setVisibility(View.GONE);
                    answerButtonNo1.setVisibility(View.GONE);
                    answerButtonNo2.setVisibility(View.GONE);
                    answerButtonNo3.setVisibility(View.GONE);
                    answerButtonNo4.setVisibility(View.GONE);
                    gameOver();

                }else {

                    mainBalance++;
                    balanceSetUp.AddBalance(mainBalance);
                    String updateScore = String.valueOf(balanceSetUp.getBalance());
                    myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(updateScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){

                              clickBalanceControl.AddBalance(mainBalance);
                              String updateShowScore = String.valueOf(clickBalanceControl.getBalance());
                              myRef.child("Users").child(phoneNo).child(uID).child("QuestionBalance").setValue(updateShowScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful()){

                                          updateQuestion(r.nextInt(mQuestionsLenght));
                                          answerButtonNo1.setEnabled(false);
                                          answerButtonNo2.setEnabled(false);
                                          answerButtonNo3.setEnabled(false);
                                          answerButtonNo4.setEnabled(false);
                                          gameLoaded();
                                      }else {
                                          Toast.makeText(QuestionWorkActivity.this, "Slow Net Connection..", Toast.LENGTH_SHORT).show();

                                      }

                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(QuestionWorkActivity.this, "Slow Net Connection..", Toast.LENGTH_SHORT).show();

                                  }
                              });

                          } else {

                              Toast.makeText(QuestionWorkActivity.this, "Slow Net Connection..", Toast.LENGTH_SHORT).show();
                          }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(QuestionWorkActivity.this, "Slow Net Connection..", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });


    }

    private void warningMethod() {


        if (warningCount>=3){

            balanceSetUp.Withdraw(10);
            String updateBalance = String.valueOf(balanceSetUp.getBalance());
            myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        invalidClickMethod();
                        Toast.makeText(QuestionWorkActivity.this, "10 point is Minus...!\n Don't Mistake Again ok.", Toast.LENGTH_SHORT).show();
                    }else {


                    }
                }
            });

        }else {

            warningToast();
            invalidClickMethod();
            warningScore++;
            myScore3 = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myScore3.edit();
            editor.putInt("warningScore",warningScore);
            editor.commit();

        }

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(QuestionWorkActivity.this,MainActivity.class));
        finish();

    }

    private void adIsLoaded() {

        if (mInterstitialAd.isLoaded()){

        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check your Net Connections", Toast.LENGTH_SHORT).show();
        }

    }

    private void initialized() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        balanceSetUp= new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();
        invalidClickControler =new InvalidClickControler();


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        r = new Random();
        answerButtonNo1 = findViewById(R.id.answerNo1_id);
        answerButtonNo2 = findViewById(R.id.answerNo2_id);
        answerButtonNo3 = findViewById(R.id.answerNo3_id);
        answerButtonNo4 = findViewById(R.id.answerNo4_id);
        showScoreTV = findViewById(R.id.showScore_Id);
        scoreTV = findViewById(R.id.score_Id);
        questionTV = findViewById(R.id.question_id);
        progressBar = findViewById(R.id.questionProgressBar_id);
        noticeBoardTV = findViewById(R.id.noticeBoardQuestion_id);
        noticeBoardTV.setVisibility(View.GONE);

        answerButtonNo1.setOnClickListener(this);
        answerButtonNo2.setOnClickListener(this);
        answerButtonNo3.setOnClickListener(this);
        answerButtonNo4.setOnClickListener(this);
        updateQuestion(r.nextInt(mQuestionsLenght));

        answerButtonNo1.setEnabled(false);
        answerButtonNo2.setEnabled(false);
        answerButtonNo3.setEnabled(false);
        answerButtonNo4.setEnabled(false);

        startStop();



    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.answerNo1_id) {

            if (answerButtonNo1.getText()==mAnswer)
            {
                mInterstitialAd.show();

            }else {

                Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();

            }



        }if (v.getId()==R.id.answerNo2_id) {


            if (answerButtonNo2.getText()==mAnswer)
            {
                mInterstitialAd.show();

            }else {

                Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();

            }


        }if (v.getId()==R.id.answerNo3_id) {

            if (answerButtonNo3.getText()==mAnswer)
            {
                mInterstitialAd.show();

            }else {

                Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();

            }


        }if (v.getId()==R.id.answerNo4_id) {

            if (answerButtonNo4.getText()==mAnswer)
            {
                mInterstitialAd.show();

            }else {

                Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();

            }


        }


    }
    private void updateQuestion(int num){
        questionTV.setText(questions.getQuestion(num));
        answerButtonNo1.setText(questions.getChoices1(num));
        answerButtonNo2.setText(questions.getChoices2(num));
        answerButtonNo3.setText(questions.getChoices3(num));
        answerButtonNo4.setText(questions.getChoices4(num));

        mAnswer= questions.getCarrectAnswer(num);

    }

    private void gameOver(){

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionWorkActivity.this);

        builder.setMessage("Great Work!")
                .setCancelable(false)
                .setPositiveButton("Go For Click...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(QuestionWorkActivity.this,Click_Activity.class);
                        intent.putExtra("click","question");
                        startActivity(intent);
                        finish();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

 private void gameLoaded(){

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionWorkActivity.this);

        builder.setMessage("Great Work ..!" +
                "\n"+ " Click Ok  For Continue Game ...")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(getApplicationContext(),QuestionWorkActivity.class));
                        finish();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void warningToast(){

        LayoutInflater inflater = getLayoutInflater();

        View toastView = inflater.inflate(R.layout.warning_layout, (ViewGroup) findViewById(R.id.warningToast_id));

        Toast toast = new Toast(QuestionWorkActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(toastView);
        toast.show();


    }
    private void invalidClickMethod() {

        invalidCount++;
        invalidClickControler.AddBalance(invalidCount);
        updateInvalidScore = String.valueOf(invalidClickControler.getBalance());

        myRef.child("Users").child(phoneNo).child(uID).child("InvalidClick").setValue(updateInvalidScore).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    String pushId = myRef.push().getKey();
                    InvalidClickClass invalidClickClass = new InvalidClickClass(phoneNo,updateInvalidScore);
                    myRef.child("InvalidClick").child(pushId).setValue(invalidClickClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                warningToast();
                            } else {
                                Toast.makeText(QuestionWorkActivity.this, "Slow net Connection...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(QuestionWorkActivity.this, "Slow net Connection...", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
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
                answerButtonNo1.setEnabled(true);
                answerButtonNo2.setEnabled(true);
                answerButtonNo3.setEnabled(true);
                answerButtonNo4.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(QuestionWorkActivity.this, "Task ready for you ", Toast.LENGTH_SHORT).show();            }
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
        // startBtn.setText("Start");



    }


}
