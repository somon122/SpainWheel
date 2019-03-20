package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class QuestionWorkActivity extends AppCompatActivity implements View.OnClickListener {


    private Button answerButtonNo1,answerButtonNo2,answerButtonNo3,answerButtonNo4;
    private TextView scoreTV,questionTV,showScoreTV;

    private Questions questions = new Questions();
     private String mAnswer;
     private int mScore = 0;
     private int showScore = 0;
     private int mQuestionsLenght = questions.mQuestions.length ;
     Random r;
    private InterstitialAd mInterstitialAd;
    private ProgressDialog progressDialog;

    FirebaseDatabase database;
    DatabaseReference myRef;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    int mainBalance = 0 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_work);


        initialized();


        myRef.child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(value));
                    scoreTV.setText("MainBalance : "+balanceSetUp.getBalance());

                }else {
                    Toast.makeText(QuestionWorkActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

 myRef.child("QuestionBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));
                    showScoreTV.setText("Show : "+clickBalanceControl.getBalance());

                }else {
                    Toast.makeText(QuestionWorkActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the interstitial ad is closed.
                //mInterstitialAd.loadAd(new AdRequest.Builder().build());

                if (clickBalanceControl.getBalance() >=15){

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
                    myRef.child("MainBalance").setValue(updateScore);

                    showScore++;
                    clickBalanceControl.AddBalance(mainBalance);
                    String updateShowScore = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child("QuestionBalance").setValue(updateShowScore);

                    //score.setText("Score: "+mScore);
                    updateQuestion(r.nextInt(mQuestionsLenght));
                    answerButtonNo1.setEnabled(false);
                    answerButtonNo2.setEnabled(false);
                    answerButtonNo3.setEnabled(false);
                    answerButtonNo4.setEnabled(false);
                    gameLoaded();
                }

            }
        });

    }
    @Override
    public void onBackPressed() {

        startActivity(new Intent(QuestionWorkActivity.this,MainActivity.class));
        finish();

    }

    private void adIsLoaded() {

        if (mInterstitialAd.isLoaded()){
            answerButtonNo1.setEnabled(true);
            answerButtonNo2.setEnabled(true);
            answerButtonNo3.setEnabled(true);
            answerButtonNo4.setEnabled(true);
            progressDialog.dismiss();
            Toast.makeText(this, "Ad is loaded successfully", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Check your Net Connections", Toast.LENGTH_SHORT).show();
        }

    }

    private void initialized() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");
        balanceSetUp= new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();


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

        answerButtonNo1.setOnClickListener(this);
        answerButtonNo2.setOnClickListener(this);
        answerButtonNo3.setOnClickListener(this);
        answerButtonNo4.setOnClickListener(this);

       // score.setText("Score: "+mScore);
        updateQuestion(r.nextInt(mQuestionsLenght));
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        answerButtonNo1.setEnabled(false);
        answerButtonNo2.setEnabled(false);
        answerButtonNo3.setEnabled(false);
        answerButtonNo4.setEnabled(false);


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

          /*  if (mScore >11){

                questionTV.setVisibility(View.GONE);
                answerButtonNo1.setVisibility(View.GONE);
                answerButtonNo2.setVisibility(View.GONE);
                answerButtonNo3.setVisibility(View.GONE);
                answerButtonNo4.setVisibility(View.GONE);
                gameOver();
            }else {

                if (answerButtonNo2.getText()==mAnswer){
                    mScore++;
                    score.setText("Score: "+mScore);
                    updateQuestion(r.nextInt(mQuestionsLenght));
                }else {

                    //gameOver();
                    Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();


                }

            }*/

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



}
