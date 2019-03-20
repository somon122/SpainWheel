package com.example.user.cashearingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Click_Activity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");


        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            value = bundle.getString("click");
            Toast.makeText(this, "Loaded Value ", Toast.LENGTH_SHORT).show();

        }





    }

    public void CompleteButton(View view) {

        if (value.equals("wheel")){
            myRef.child("ClickBalance").removeValue();
            Toast.makeText(this, "wheel Work Completed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Click_Activity.this,MainActivity.class));
            finish();


        }  else if (value.equals("love")){
            myRef.child("LoveBalance").removeValue();
            Toast.makeText(this, "love Work Completed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Click_Activity.this,MainActivity.class));
            finish();


        }  else if (value.equals("question")){
            myRef.child("QuestionBalance").removeValue();
            Toast.makeText(this, "question Work Completed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Click_Activity.this,MainActivity.class));
            finish();


        }else {
            Toast.makeText(this, " Did not match", Toast.LENGTH_SHORT).show();
        }

    }
}
