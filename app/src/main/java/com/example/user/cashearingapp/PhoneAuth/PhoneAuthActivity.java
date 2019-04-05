package com.example.user.cashearingapp.PhoneAuth;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.cashearingapp.BalanceSetUp;
import com.example.user.cashearingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class PhoneAuthActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneNumberET;
    Button sentCodeButton;

    String countryCodeNumber;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);



        countryCodePicker = findViewById(R.id.countryCodePicker_Id);
        phoneNumberET= findViewById(R.id.phoneNumber_Id);
        sentCodeButton= findViewById(R.id.sentCode_Id);

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");



        sentCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (haveNetwork()){
                    phoneAuthentication();
                }else {
                    Toast.makeText(PhoneAuthActivity.this, "Net connection is Error", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    //------ End of onCreate Method ----------



    private void phoneAuthentication(){
        countryCodeNumber = countryCodePicker.getFullNumberWithPlus();
        String phoneNumber = phoneNumberET.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.length()<10){

            phoneNumberET.setError("Please Enter the Correct Phone Number");
            phoneNumberET.requestFocus();
            return;
        }else {
            String number = countryCodeNumber+phoneNumber;
            Intent intent = new Intent(PhoneAuthActivity.this,PhoneAuthConfirmActivity.class);
            intent.putExtra("phoneNumber",number);
            startActivity(intent);

            Toast.makeText(PhoneAuthActivity.this, " Welcome New User", Toast.LENGTH_SHORT).show();


        }


    }

    private boolean haveNetwork ()
    {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo){

            if (info.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (info.isConnected())
                {
                    have_WiFi = true;
                }
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))

            {
                if (info.isConnected())
                {
                    have_Mobile = true;
                }
            }

        }
        return have_WiFi || have_Mobile;

    }



}
