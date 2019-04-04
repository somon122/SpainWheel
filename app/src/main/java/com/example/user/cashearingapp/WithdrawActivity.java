package com.example.user.cashearingapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.cashearingapp.PhoneAuth.PhoneAuthActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WithdrawActivity extends AppCompatActivity {


    private ConstraintLayout constraintLayout1,constraintLayout2,constraintLayout3,constraintLayout4;
    private Spinner spinner;
    private String spinnerValue;

    private EditText paypalAddressET,paypalAmountET,bKashNumberET,bKashAmountET,
            rocketNumberET,rocketAmountET,mobileReNumberET,mobileReAmount;

  private Button submitButton;
    WithdrawSubmit submit;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    String currentDateTimeString;

    String paypleAddress;
    String bKashNumber;
    String rocketNumber;
    String rechargeNumber;

    int payPalAmount;
    int bKashAmount;
    int rocketAmount;
    int rechargeAmount;

    String phoneNo;

    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        balanceSetUp = new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();
        if (user != null){
            uID = user.getUid();

        }
        constraintLayout1 = findViewById(R.id.paypalConstraintLayout_id);
        constraintLayout2 = findViewById(R.id.bKashConstraintLayout_id);
        constraintLayout3 = findViewById(R.id.rocketConstraintLayout_id);
        constraintLayout4 = findViewById(R.id.rechargeConstraintLayout_id);
        spinner = findViewById(R.id.spinner_id);
        submitButton = findViewById(R.id.withdrawSubmit_id);
        paypalAddressET=findViewById(R.id.PaypalAddress_id);
        paypalAmountET=findViewById(R.id.paypalAmount_id);

        bKashNumberET=findViewById(R.id.bKashNumber_id);
        bKashAmountET=findViewById(R.id.bKashAmount_id);

        rocketNumberET=findViewById(R.id.rocketNumber_id);
        rocketAmountET=findViewById(R.id.rocketAmount_id);

        mobileReNumberET=findViewById(R.id.rechargeNumber_id);
        mobileReAmount=findViewById(R.id.rechargeAmount_id);

        constraintLayout1.setVisibility(View.GONE);
        constraintLayout2.setVisibility(View.GONE);
        constraintLayout3.setVisibility(View.GONE);
        constraintLayout4.setVisibility(View.GONE);

        phoneNo = user.getPhoneNumber();

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        progressDialog = new ProgressDialog(this);
        progressDialog.show();



        myRef.child(phoneNo).child(uID).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){
                    progressDialog.dismiss();
                    String value = dataSnapshot.getValue(String.class);
                    clickBalanceControl.setBalance(Integer.parseInt(value));

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, " Data is loading...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();

            }
        });

        List<String> paymentSystem = new ArrayList<String>();
        paymentSystem.add("Select Any One");
        paymentSystem.add("PayPal");
        paymentSystem.add("BKash");
        paymentSystem.add("Rocket");
        paymentSystem.add("Mobile Recharge");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentSystem);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectItem2();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submittion();

            }
        });





    }


    private void submittion(){


        if (spinnerValue.equals("PayPal")){


            paypleAddress = paypalAddressET.getText().toString().trim();
            String paypalAmount = paypalAmountET.getText().toString().trim();

            if (paypleAddress.isEmpty() ){

                paypalAddressET.setError("Please Enter PayPal Address");


            }else  if (paypalAmount.isEmpty()){
                paypalAmountET.setError("Please Enter PayPal Address");


            } else {

                progressDialog.show();
                payPalAmount = Integer.parseInt(paypalAmount);
                if (clickBalanceControl.getBalance() >=payPalAmount){

                    clickBalanceControl.Withdraw(payPalAmount);
                    String updateBalance = String.valueOf(clickBalanceControl.getBalance());

                    myRef.child(phoneNo).child(uID).child("ConvertBalance").setValue(updateBalance).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                submit = new WithdrawSubmit(paypleAddress,payPalAmount);
                                myRef.child(phoneNo).child(uID).child("Withdraw").child("PayPal").child(currentDateTimeString).setValue(submit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is successfully completed ", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sorry..! You don't have enough Balance", Toast.LENGTH_SHORT).show();
                }


            }





        }else if (spinnerValue.equals("BKash")){


            bKashNumber = bKashNumberET.getText().toString().trim();
            String amount = bKashAmountET.getText().toString().trim();

            if (bKashNumber.isEmpty() ){

                bKashNumberET.setError("Please Enter PayPal Address");


            }else  if (amount.isEmpty()){
                bKashAmountET.setError("Please Enter PayPal Address");


            } else {

                progressDialog.show();
                bKashAmount = Integer.parseInt(amount);
                if (clickBalanceControl.getBalance() >=bKashAmount){
                    clickBalanceControl.Withdraw(bKashAmount);
                    String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child(phoneNo).child(uID).child("ConvertBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                submit = new WithdrawSubmit(bKashNumber,bKashAmount);
                                myRef.child(phoneNo).child(uID).child("Withdraw").child("BKash").child(currentDateTimeString).setValue(submit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is successfully Completed ", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);


                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });


                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                }else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sorry..! You don't have enough Balance", Toast.LENGTH_SHORT).show();
                }


            }



        }

        else if (spinnerValue.equals("Rocket")){

            rocketNumber = rocketNumberET.getText().toString().trim();
            String amount = rocketAmountET.getText().toString().trim();

            if (rocketNumber.isEmpty() ){

                rocketNumberET.setError("Please Enter PayPal Address");


            }else  if (amount.isEmpty()){
                rocketAmountET.setError("Please Enter PayPal Address");


            } else {

                progressDialog.show();
                rocketAmount = Integer.parseInt(amount);
                if (clickBalanceControl.getBalance() >=rocketAmount){
                    clickBalanceControl.Withdraw(rocketAmount);
                    String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child(phoneNo).child(uID).child("ConvertBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                submit = new WithdrawSubmit(rocketNumber,rocketAmount);
                                myRef.child(phoneNo).child(uID).child("Withdraw").child("Rocket").child(currentDateTimeString).setValue(submit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is successfully Completed ", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });


                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sorry..! You don't have enough Balance", Toast.LENGTH_SHORT).show();
                }


            }



        }

        else if (spinnerValue.equals("Mobile Recharge")){



            rechargeNumber = mobileReNumberET.getText().toString().trim();
            String amount = mobileReAmount.getText().toString().trim();

            if (rechargeNumber.isEmpty() ){

                mobileReNumberET.setError("Please Enter PayPal Address");


            }else  if (amount.isEmpty()){
                mobileReAmount.setError("Please Enter PayPal Address");


            } else {

                progressDialog.show();
                rechargeAmount = Integer.parseInt(amount);
                if (clickBalanceControl.getBalance() >=rechargeAmount){
                    clickBalanceControl.Withdraw(rechargeAmount);
                    String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                    myRef.child(phoneNo).child(uID).child("ConvertBalance").setValue(updateBalance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                submit = new WithdrawSubmit(rechargeNumber,rechargeAmount);
                                myRef.child(phoneNo).child(uID).child("Withdraw").child("MobileRecharge").child(currentDateTimeString).setValue(submit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is successfully Completed ", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(WithdrawActivity.this, "Withdraw is Field", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });



                }else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sorry..! You don't have enough Balance", Toast.LENGTH_SHORT).show();
                }


            }



        }else {

            Toast.makeText(this, "Could not match", Toast.LENGTH_SHORT).show();

        }





    }


    private void selectItem2(){

        spinnerValue = spinner.getSelectedItem().toString();


        if (spinnerValue.equals("PayPal")){

            constraintLayout1.setVisibility(View.VISIBLE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.GONE);



        }else if (spinnerValue.equals("BKash")){
            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.VISIBLE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.GONE);

        }

        else if (spinnerValue.equals("Rocket")){
            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.VISIBLE);
            constraintLayout4.setVisibility(View.GONE);


        }

        else if (spinnerValue.equals("Mobile Recharge")){

            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.VISIBLE);


        }else {
            constraintLayout1.setVisibility(View.GONE);
            constraintLayout2.setVisibility(View.GONE);
            constraintLayout3.setVisibility(View.GONE);
            constraintLayout4.setVisibility(View.GONE);
        }



}


}
