package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class WheelActivity extends AppCompatActivity {

    Button tapButton;
    ImageView wheelImage;
    TextView resultView,counterShow;

    Random r;
    int degree = 0, degree_old = 0;
    private static final float FACTOR = 15f;
    private InterstitialAd mInterstitialAd;
    ProgressDialog progressDialog;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference myRef;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    int mainBalance = 0 ;
    int counter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");
        balanceSetUp= new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();

        tapButton = findViewById(R.id.tapButtonId);
        wheelImage = findViewById(R.id.wheel_id);
        resultView= findViewById(R.id.resultId);
        counterShow= findViewById(R.id.counterShow_Id);


        r = new Random();
        progressDialog = new ProgressDialog(this);
        progressDialog.show();





        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        BalanceControl();


        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {

                   degree_old = degree % 360;
                   degree = r.nextInt(3600) + 720;

                   RotateAnimation animationRotate = new RotateAnimation(degree_old,degree,RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                   animationRotate.setDuration(3600);
                   animationRotate.setFillAfter(true);
                   animationRotate.setInterpolator(new DecelerateInterpolator());
                   animationRotate.setAnimationListener(new Animation.AnimationListener() {
                       @Override
                       public void onAnimationStart(Animation animation) {

                           tapButton.setEnabled(false);

                       }

                       @Override
                       public void onAnimationEnd(Animation animation) {

                           mInterstitialAd.show();

                       }

                       @Override
                       public void onAnimationRepeat(Animation animation) {

                       }
                   });

                   wheelImage.startAnimation(animationRotate);


               }

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

                Toast.makeText(WheelActivity.this, " You are doing Mistake ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the interstitial ad is closed.

                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                //mainBalance++;
                //String balance = String.valueOf(mainBalance);

                if (clickBalanceControl.getBalance() >= 10)

                {
                    Intent intent = new Intent(WheelActivity.this,Click_Activity.class);
                    intent.putExtra("click","wheel");
                    startActivity(intent);
                    finish();

                }else {
                    courrentNumber(360 - (degree%360));

                    balanceSetUp.AddBalance(mainBalance);
                    String updateBalance = String.valueOf(balanceSetUp.getBalance());
                    myRef.child("MainBalance").setValue(updateBalance);


                    counter++;
                    clickBalanceControl.AddBalance(counter);
                    String updateClickBalance = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child("ClickBalance").setValue(updateClickBalance);

                    tapButton.setEnabled(false);
                    //resultView.setText(courrentNumber(360 - (degree%360)));
                    gameOver();


                }

            }
        });


    }

    private void BalanceControl() {


        myRef.child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(value));
                    resultView.setText("Balance : "+balanceSetUp.getBalance());

                }/*else {
                    Toast.makeText(WheelActivity.this, " Data is Empty", Toast.LENGTH_SHORT).show();
                }*/



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        myRef.child("ClickBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));
                    counterShow.setText("Show: "+clickBalanceControl.getBalance());

                }/*else {
                    Toast.makeText(WheelActivity.this, " Data is Empty", Toast.LENGTH_SHORT).show();
                }*/



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });








    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(WheelActivity.this,MainActivity.class));
        finish();

    }

    private void adIsLoaded() {

        if (mInterstitialAd.isLoaded()){

            tapButton.setEnabled(true);
            progressDialog.dismiss();
        }else {

            Toast.makeText(this, "Please Check your Net Connections", Toast.LENGTH_SHORT).show();
        }

    }

    private String courrentNumber (int degrees){
        String text = "";

              if (degrees >= (FACTOR *1) && degrees < (FACTOR * 3)){

                  //text =  "02 green";
                  mainBalance = mainBalance+2;
                  //resultView.setText("MainBalance : "+mainBalance);


            } if (degrees >= (FACTOR *3) && degrees < (FACTOR * 5)){

            //text =  "03 green";
            mainBalance = mainBalance+3;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *5) && degrees < (FACTOR * 7)){


            //text =  "04 green";
            mainBalance = mainBalance+4;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *7) && degrees < (FACTOR * 9)){

            //text =  "05 green";
            mainBalance = mainBalance+5;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *9) && degrees < (FACTOR * 11)){

            //text =  "06 green";
            mainBalance = mainBalance+6;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *11) && degrees < (FACTOR * 13)){

            //text =  "07 green";
            mainBalance = mainBalance+7;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *13) && degrees < (FACTOR * 15)){

            //text =  "08 green";
            mainBalance = mainBalance+8;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *15) && degrees < (FACTOR * 17)){

            //text =  "09 green";
            mainBalance = mainBalance+9;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *17) && degrees < (FACTOR * 19)){

            //text =  "10 green";
            mainBalance = mainBalance+10;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *19) && degrees < (FACTOR * 21)){


            //text =  "11 green";
            mainBalance = mainBalance+11;
            //resultView.setText("MainBalance : "+mainBalance);

            } if (degrees >= (FACTOR *21) && degrees < (FACTOR * 23)){


            //text =  "12 green";
            mainBalance = mainBalance+12;
            //resultView.setText("MainBalance : "+mainBalance);

            }

        if ((degrees >= (FACTOR * 23 ) && degrees < 360) || (degrees >= 0 && degrees < (FACTOR * 1)))
        {

            //text = "1";
            mainBalance = mainBalance+1;
            //resultView.setText("MainBalance : "+mainBalance);
        }

        return text;

    }


    private void gameOver(){

        AlertDialog.Builder builder = new AlertDialog.Builder(WheelActivity.this);

        builder.setMessage(" Great Work ...!" +
                "\n"+" Click Ok For Continue Game ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(getApplicationContext(),WheelActivity.class));
                        finish();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}
