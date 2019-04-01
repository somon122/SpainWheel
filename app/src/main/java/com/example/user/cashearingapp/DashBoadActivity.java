package com.example.user.cashearingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashBoadActivity extends AppCompatActivity {


    private TextView nameTV,balanceTV,phoneTV,withdrawTV,pointTV;

    private String phoneNumber,name,withdrawall;
    private int balance,point;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;




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

        myRef = database.getReference("Balance");

        balanceSetUp = new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();

        if (user != null) {
            uID = user.getUid();
            loadInfo();
            phoneTV.setText(user.getPhoneNumber());

        }else {
            Toast.makeText(this, "Check your Net connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void loadInfo(){


        myRef.child(uID).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                   balanceTV.setText(""+value);


                }else {
                    Toast.makeText(DashBoadActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
        myRef.child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    pointTV.setText(""+value);

                }else {
                    Toast.makeText(DashBoadActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
        myRef.child(uID).child("AccountInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){

                    AccountInfo accountInfo = dataSnapshot.getValue(AccountInfo.class);
                    nameTV.setText(accountInfo.getUserName());


                }else {
                    Toast.makeText(DashBoadActivity.this, " Data is empty", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



    }


}
