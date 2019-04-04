package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
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

public class WheelActivity extends AppCompatActivity implements View.OnClickListener,RewardedVideoAdListener {

    Button tapButton;
    ImageView wheelImage;
    TextView resultView,counterShow;

    Random r;
    int degree = 0, degree_old = 0;
    private static final float FACTOR = 15f;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    private ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    FirebaseUser user;

    String uId;
    String phoneNo;

    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    int mainBalance = 0 ;
    int counter = 0;
    int showCount = 0;


    int warningCount;
    int warningScore;
    SharedPreferences myScore3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        uId = user.getUid();

        balanceSetUp= new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();

        tapButton = findViewById(R.id.tapButtonId);
        wheelImage = findViewById(R.id.wheel_id);
        resultView= findViewById(R.id.resultId);
        counterShow= findViewById(R.id.counterShow_Id);
        tapButton.setVisibility(View.GONE);

        phoneNo = user.getPhoneNumber();


        r = new Random();
       progressBar=findViewById(R.id.wheelProgressBar_id);


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();



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

                           tapButton.setVisibility(View.GONE);

                       }

                       @Override
                       public void onAnimationEnd(Animation animation) {

                           courrentNumber(360 - (degree%360));

                       }

                       @Override
                       public void onAnimationRepeat(Animation animation) {

                       }
                   });

                   wheelImage.startAnimation(animationRotate);


               }

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
                // Code to be executed when the user has left the app.

               warningMethod();
            }

            @Override
            public void onAdClosed() {

                // Code to be executed when when the interstitial ad is closed.


                if (clickBalanceControl.getBalance() >=5)

                {
                    Intent intent = new Intent(WheelActivity.this,Click_Activity.class);
                    intent.putExtra("click","wheel");
                    startActivity(intent);
                    finish();

                }else {
                    //courrentNumber(360 - (degree%360));

                    balanceSetUp.AddBalance(mainBalance);
                    String updateBalance = String.valueOf(balanceSetUp.getBalance());
                    myRef.child(phoneNo).child(uId).child("MainBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(WheelActivity.this, "point is added", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                        }
                    });


                    counter++;
                    //showCount++;
                    clickBalanceControl.AddBalance(counter);
                    String updateClickBalance = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child(phoneNo).child(uId).child("ClickBalance").setValue(updateClickBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(WheelActivity.this, "Well Done..", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                        }
                    });

                    tapButton.setVisibility(View.GONE);
                    //resultView.setText(courrentNumber(360 - (degree%360)));
                    gameOver(mainBalance);


                }

            }
        });


    }

    private void BalanceControl() {


        myRef.child(phoneNo).child(uId).child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    balanceSetUp.setBalance(Integer.parseInt(value));
                    resultView.setText("Score : "+balanceSetUp.getBalance());

                }/*else {
                    Toast.makeText(WheelActivity.this, " Data is Empty", Toast.LENGTH_SHORT).show();
                }*/



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        myRef.child(phoneNo).child(uId).child("ClickBalance").addValueEventListener(new ValueEventListener() {
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

        if (mInterstitialAd.isLoaded()&& mRewardedVideoAd.isLoaded()){

            tapButton.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check your Net Connections", Toast.LENGTH_SHORT).show();
        }

    }

    private String courrentNumber (int degrees){
        String text = "";

              if (degrees >= (FACTOR *1) && degrees < (FACTOR * 3)){

                  mainBalance = mainBalance+2;
                  mInterstitialAd.show();

            } if (degrees >= (FACTOR *3) && degrees < (FACTOR * 5)){

            mainBalance = mainBalance+3;
            mInterstitialAd.show();

            } if (degrees >= (FACTOR *5) && degrees < (FACTOR * 7)){

            mainBalance = mainBalance+4;
            mInterstitialAd.show();

            } if (degrees >= (FACTOR *7) && degrees < (FACTOR * 9)){

            mainBalance = mainBalance+5;
            mInterstitialAd.show();

            } if (degrees >= (FACTOR *9) && degrees < (FACTOR * 11)){

            mainBalance = mainBalance+6;
            mRewardedVideoAd.show();


            } if (degrees >= (FACTOR *11) && degrees < (FACTOR * 13)){

            mainBalance = mainBalance+7;
            mInterstitialAd.show();


            } if (degrees >= (FACTOR *13) && degrees < (FACTOR * 15)){

            mainBalance = mainBalance+8;
            mRewardedVideoAd.show();


            } if (degrees >= (FACTOR *15) && degrees < (FACTOR * 17)){


            mainBalance = mainBalance+9;
            mRewardedVideoAd.show();

            } if (degrees >= (FACTOR *17) && degrees < (FACTOR * 19)){

            mainBalance = mainBalance+10;
            mInterstitialAd.show();


            } if (degrees >= (FACTOR *19) && degrees < (FACTOR * 21)){

            mainBalance = mainBalance+11;
            mRewardedVideoAd.show();

            } if (degrees >= (FACTOR *21) && degrees < (FACTOR * 23)){

            mainBalance = mainBalance+12;
            mRewardedVideoAd.show();

            }

        if ((degrees >= (FACTOR * 23 ) && degrees < 360) || (degrees >= 0 && degrees < (FACTOR * 1)))
        {

            mainBalance = mainBalance+1;
            mInterstitialAd.show();
        }

        return text;

    }


    private void gameOver(int score){

        AlertDialog.Builder builder = new AlertDialog.Builder(WheelActivity.this);

        builder.setMessage("Congratulation..!"+"\n\n"+"You Got : "+score+" point"+
                "\n\n"+" Click Ok For Continue Game ..." +
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


    private void loadRewardedVideoAd() {

        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {

        adIsLoaded();

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        if (clickBalanceControl.getBalance() >=5)

        {
            Intent intent = new Intent(WheelActivity.this,Click_Activity.class);
            intent.putExtra("click","wheel");
            startActivity(intent);
            finish();

        }else {
            //courrentNumber(360 - (degree%360));

            balanceSetUp.AddBalance(mainBalance);
            String updateBalance = String.valueOf(balanceSetUp.getBalance());
            myRef.child(phoneNo).child(uId).child("MainBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(WheelActivity.this, "point is added", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                }
            });


            counter++;
            clickBalanceControl.AddBalance(counter);
            String updateClickBalance = String.valueOf(clickBalanceControl.getBalance());
            myRef.child(phoneNo).child(uId).child("ClickBalance").setValue(updateClickBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(WheelActivity.this, "Well Done..", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WheelActivity.this, "try Again", Toast.LENGTH_SHORT).show();

                }
            });

            tapButton.setVisibility(View.GONE);
            gameOver(mainBalance);

        }

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

        Toast.makeText(this, "You are mistake...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    private void warningMethod() {


        if (warningCount>=3){

            mainBalance = mainBalance-10;
            balanceSetUp.Withdraw(mainBalance);
            String updateBalance = String.valueOf(balanceSetUp.getBalance());
            myRef.child(phoneNo).child(uId).child("MainBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(WheelActivity.this, "10 point is Minus...!\n Don't Mistake Again ok.", Toast.LENGTH_SHORT).show();
                    }else {


                    }
                }
            });

        }else {

            warningToast();
            warningScore++;
            myScore3 = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myScore3.edit();
            editor.putInt("warningScore",warningScore);
            editor.commit();

        }

    }

    private void warningToast(){

        LayoutInflater inflater = getLayoutInflater();

        View toastView = inflater.inflate(R.layout.warning_layout, (ViewGroup) findViewById(R.id.warningToast_id));

        Toast toast = new Toast(WheelActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(toastView);
        toast.show();


    }



}
