package com.example.user.cashearingapp;

import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.os.ConfigurationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.cashearingapp.PhoneAuth.PhoneAuthActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private TextView deviceId,textView2;
    TimePicker timePicker;

    FloatingActionButton videoButton, wheelButton,quziButton,loveButton;

    Locale locale;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initilized();

        if (haveNetwork()){

            if (auth != null) {

           /* String number = user.getPhoneNumber();

            textView2 = findViewById(R.id.textView2);
            textView2.setText(number);*/

                myRef.child("MainBalance").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        if (dataSnapshot.exists()){
                            String value = dataSnapshot.getValue(String.class);
                            BalanceSetUp balanceSetUp = new BalanceSetUp();
                            balanceSetUp.setBalance(Integer.parseInt(value));

                            deviceId.setText("Main Balance : "+ balanceSetUp.getBalance());


                        }else {
                            Toast.makeText(MainActivity.this, " Data is loading...", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value

                    }
                });

            }

        }else {
            Toast.makeText(this, " Please Check your Net connection", Toast.LENGTH_SHORT).show();
        }



    }


    private void initilized(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceId = findViewById(R.id.deviceId);
        timePicker = new TimePicker(this);

        videoButton = findViewById(R.id.video_id);
        loveButton = findViewById(R.id.love_id);
        wheelButton = findViewById(R.id.wheelSpin_id);
        quziButton = findViewById(R.id.quiz_id);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");

        videoButton.setOnClickListener(this);
        loveButton.setOnClickListener(this);
        quziButton.setOnClickListener(this);
        wheelButton.setOnClickListener(this);

        String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

        // Read from the database

        //deviceId.setText("Main Balance is :" +mainBlance);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            startActivity(new Intent(MainActivity.this, TaskActivity.class));


        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this, QuestionWorkActivity.class));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, WheelActivity.class));

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, VideoShowActivity.class));

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.video_id){

            startActivity(new Intent(MainActivity.this,VideoShowActivity.class));

        }
        if (v.getId()==R.id.wheelSpin_id){
            startActivity(new Intent(MainActivity.this,WheelActivity.class));

        }
        if (v.getId()==R.id.quiz_id){
            startActivity(new Intent(MainActivity.this,QuestionWorkActivity.class));

        }
        if (v.getId()==R.id.love_id){
            startActivity(new Intent(MainActivity.this,TaskActivity.class));

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
