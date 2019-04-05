package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class VideoShowActivity extends AppCompatActivity {

    Uri accountImageUri = null;
    CircleImageView circleImageView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference mStorageRef;
    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    String phoneNumber;
    ProgressDialog progressDialog;
    private MyWorkClass myWorkClass;
    Bitmap compressedImageFile;
    EditText descriptionET;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);


        circleImageView = findViewById(R.id.imageUpload_id);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");
        mStorageRef = FirebaseStorage.getInstance().getReference("MyWorkImage");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        phoneNumber = user.getPhoneNumber();
        progressDialog = new ProgressDialog(this);
        descriptionET = findViewById(R.id.descriptionSetUp_id);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermission();
            }
        });
        Button button = findViewById(R.id.uploadButton_id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMyWorkImage();
            }
        });

    }

    private void isPermission(){
        if (Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(VideoShowActivity.this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},100);

            }else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(VideoShowActivity.this);
            }

        }else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(VideoShowActivity.this);
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

    private void uploadMyWorkImage(){

        description = descriptionET.getText().toString().trim();

        if (description.isEmpty()){

            descriptionET.setError("Please Enter Work Description.");

        }else {
            if (accountImageUri != null){
                progressDialog.setMessage("Image is Uploading...");
                progressDialog.show();

                File newImageFile = new File(accountImageUri.getPath());
                try {
                    compressedImageFile = new Compressor(this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(100)
                            .compressToBitmap(newImageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] newImageData = baos.toByteArray();


                StorageReference riversRef = mStorageRef.child(accountImageUri.getLastPathSegment()).child(uID + ".jpg");
                riversRef.putBytes(newImageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            String imageUrl = task.getResult().getDownloadUrl().toString();

                            myWorkClass = new MyWorkClass(uID,imageUrl,description);

                            String uploadId = myRef.push().getKey();

                            myRef.child("MyWork").child(uploadId).setValue(myWorkClass).addOnCompleteListener(VideoShowActivity.this,new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        progressDialog.dismiss();
                                        Intent intent = new Intent(VideoShowActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();


                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(VideoShowActivity.this, "Please check your Net connection", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });



                        }
                    }
                });

            }else {
                Toast.makeText(this, "Please load Image...", Toast.LENGTH_SHORT).show();
            }

        }




    }

}
