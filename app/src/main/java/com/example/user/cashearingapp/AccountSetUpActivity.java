package com.example.user.cashearingapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AccountSetUpActivity extends AppCompatActivity {


    private EditText nameET,passswordET,confirmPassET,birthDayET;
    private Button infoSubmitButton;
    private TextView datePikerTV;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    String phoneNumber;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_set_up);

        nameET = findViewById(R.id.name_id);
        passswordET = findViewById(R.id.password_id);
        confirmPassET = findViewById(R.id.confirmPassword_id);
        birthDayET = findViewById(R.id.birthDay_id);
        infoSubmitButton = findViewById(R.id.informationSubmit_id);
        datePikerTV = findViewById(R.id.datePiker_id);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        phoneNumber = user.getPhoneNumber();
        progressDialog = new ProgressDialog(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date1 = dayOfMonth + "/" +month+ "/" +year;
                birthDayET.setText(date1);
            }
        };

        datePikerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(AccountSetUpActivity.this,android.R.style.
                        Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });


        infoSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountSetUp();
            }
        });

    }

    private void accountSetUp (){

        String name = nameET.getText().toString().trim();
        String password = passswordET.getText().toString().trim();
        String confirmPass = confirmPassET.getText().toString().trim();
        String birthDay = birthDayET.getText().toString().trim();

        if (name.isEmpty()){

            nameET.setError("Please Enter Your Full name");

        }else if (password.isEmpty()){
            passswordET.setError("Please Enter Password");

        }else if (confirmPass.isEmpty()){
            confirmPassET.setError("Please enter Re-password");

        }else if (birthDay.isEmpty()){
            birthDayET.setError("Please enter Your Real Birth Date");

        }else {


            if (!password.equals(confirmPass)){

                Toast.makeText(this, "Confirm password could not match", Toast.LENGTH_LONG).show();

            }else {
                progressDialog.show();
                AccountInfo accountInfo = new AccountInfo(uID,name,confirmPass,birthDay);
                myRef.child(uID).child("AccountInfo").setValue(accountInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            progressDialog.dismiss();
                            Toast.makeText(AccountSetUpActivity.this, "Account Setup is Successfully Completed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AccountSetUpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(AccountSetUpActivity.this, "Please check your Net connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });



            }


        }



    }
}
