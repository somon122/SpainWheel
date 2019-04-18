package com.example.user.cashearingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoadActivity extends AppCompatActivity {


    private TextView nameTV,balanceTV,phoneTV,withdrawTV,pointTV;
    private CircleImageView circleImageShow;

    private String phoneNumber,name,withdrawall;
    private int balance,point;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    private AdView mAdView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_boad);


        nameTV = findViewById(R.id.nameShow_id);
        balanceTV = findViewById(R.id.balanceShow_id);
        phoneTV = findViewById(R.id.phoneNumberShow_id);
        withdrawTV = findViewById(R.id.tWithdrawShow_id);
        pointTV = findViewById(R.id.pointShow_id);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        circleImageShow = findViewById(R.id.circleImageShowId);

        myRef = database.getReference("UserBalance");

        mAdView = findViewById(R.id.dashBoardBannerAdView_id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        balanceSetUp = new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();

        phoneNumber = user.getPhoneNumber();

        if (user != null) {
            uID = user.getUid();
            loadInfo();
            phoneTV.setText(phoneNumber);

        }else {
            Toast.makeText(this, "Check your Net connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void loadInfo(){


        myRef.child("Users").child(phoneNumber).child(uID).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                   balanceTV.setText(""+value);


                }else {

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
        myRef.child("Users").child(phoneNumber).child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    pointTV.setText(""+value);

                }else {

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
        myRef.child("Users").child(phoneNumber).child(uID).child("AccountInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){

                    AccountInfo accountInfo = dataSnapshot.getValue(AccountInfo.class);
                    nameTV.setText(accountInfo.getUserName());
                    Picasso.get().load(accountInfo.getImageUrl()).placeholder(R.drawable.account).into(circleImageShow);


                }else {

                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



    }


}
