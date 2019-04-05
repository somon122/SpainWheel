package com.example.user.cashearingapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetUpActivity extends AppCompatActivity {


    private EditText nameET,passswordET,confirmPassET,birthDayET;
    private Button infoSubmitButton;
    private TextView datePikerTV;
    private CircleImageView circleImageView;
    Uri accountImageUri = null;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference mStorageRef;
    FirebaseAuth auth;
    FirebaseUser user;
    String uID;

    SharedPreferences myScore;
    int confirmScore=0;
    String phoneNumber;
    AccountInfo accountInfo;
    String name;
    String password;
    String confirmPass;
    String birthDay;



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
        circleImageView = findViewById(R.id.circleImageViewId);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");
        mStorageRef = FirebaseStorage.getInstance().getReference("AccountImage");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        phoneNumber = user.getPhoneNumber();
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        myScore = this.getSharedPreferences("ConfirmSetUp", Context.MODE_PRIVATE);
        confirmScore = myScore.getInt("confirmScore",0);


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

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermission();

            }
        });


        infoSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountSetUp();
            }
        });

    }

    private void isPermission(){
        if (Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(AccountSetUpActivity.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},100);

            }else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountSetUpActivity.this);
            }

        }else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(AccountSetUpActivity.this);
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                accountImageUri = result.getUri();
                circleImageView.setImageURI(accountImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Problem "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (confirmScore==1){
            progressDialog.dismiss();
            Intent intent = new Intent(AccountSetUpActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }else {
            progressDialog.dismiss();

        }
    }

    private void confirmDetails(){

        myScore = getSharedPreferences("ConfirmSetUp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myScore.edit();
        editor.putInt("confirmScore",confirmScore);
        editor.commit();
    }

    private void accountSetUp (){

        name = nameET.getText().toString().trim();
        password = passswordET.getText().toString().trim();
        confirmPass = confirmPassET.getText().toString().trim();
        birthDay = birthDayET.getText().toString().trim();

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
                progressDialog.setMessage("Information is Uploading...");
                progressDialog.show();
                if (accountImageUri != null){

                    StorageReference riversRef = mStorageRef.child(accountImageUri.getLastPathSegment()).child(uID + ".jpg");
                    riversRef.putFile(accountImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                String imageUrl = task.getResult().getDownloadUrl().toString();

                                accountInfo = new AccountInfo(uID,name,confirmPass,birthDay,imageUrl);

                                myRef.child("Users").child(phoneNumber).child(uID).child("AccountInfo").setValue(accountInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            progressDialog.dismiss();
                                            confirmScore++;
                                            confirmDetails();
                                            Toast.makeText(AccountSetUpActivity.this, "Account Setup is Successfully Completed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AccountSetUpActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(AccountSetUpActivity.this, "Please check your Net connection", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });



                            }
                        }
                    });



                }else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sorry missing your Photo....", Toast.LENGTH_SHORT).show();
                }




            }


        }



    }




}
