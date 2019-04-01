package com.example.user.cashearingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.cashearingapp.PhoneAuth.PhoneAuthActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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


    private TextView deviceId,timeShowTV;
    TimePicker timePicker;
    ProgressBar progressBar;

    FloatingActionButton rulesButton, wheelButton,quziButton,loveButton;

    Locale locale;

    DrawerLayout drawer;
    NavigationView navigationView;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    CountDownTimer countDownTimer;
    long timeLeft = 30000;
    boolean timeRunning;
    String timeText;


    FloatingActionMenu floatingActionMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initilized();
        timeShowTV.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
           // String completedWork = bundle.getString("completed");
            startStop();

        }




        if (haveNetwork()){

            if (user != null) {

                myRef.child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        if (dataSnapshot.exists()){
                            progressBar.setVisibility(View.GONE);
                            String value = dataSnapshot.getValue(String.class);
                            balanceSetUp.setBalance(Integer.parseInt(value));


                        }else {
                            Toast.makeText(MainActivity.this, " Data is loading...", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value

                    }
                });

                myRef.child(uID).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        if (dataSnapshot.exists()){
                            String value = dataSnapshot.getValue(String.class);
                            clickBalanceControl.setBalance(Integer.parseInt(value));

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


    //---------- OnCreate is End point -----------------


    private void initilized(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceId = findViewById(R.id.deviceId);
        timePicker = new TimePicker(this);



        rulesButton = findViewById(R.id.rules_id);
        loveButton = findViewById(R.id.love_id);
        wheelButton = findViewById(R.id.wheelSpin_id);
        quziButton = findViewById(R.id.quiz_id);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Balance");
        balanceSetUp = new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();

        if (user != null) {
            uID = user.getUid();
        }
        rulesButton.setOnClickListener(this);
        loveButton.setOnClickListener(this);
        quziButton.setOnClickListener(this);
        wheelButton.setOnClickListener(this);

      /*  String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
*/

        locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

        // Read from the database



        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        floatingActionMenu = findViewById(R.id.floatingMenu_id);
        progressBar = findViewById(R.id.progressBar2222_id);
        timeShowTV = findViewById(R.id.timeShoe_id);



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

            if (timeLeft > 299999){
                super.onBackPressed();
            }else {
                Toast.makeText(this, "please Waiting....", Toast.LENGTH_SHORT).show();
            }



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

        int id = item.getItemId();

        if (id == R.id.logOut_id) {

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
        int id = item.getItemId();

        if (id == R.id.love2_id) {
            startActivity(new Intent(MainActivity.this, TaskActivity.class));

        } else if (id == R.id.mcq_id) {
            startActivity(new Intent(MainActivity.this, QuestionWorkActivity.class));

        }else if (id == R.id.dashBoard_id) {
            startActivity(new Intent(MainActivity.this, DashBoadActivity.class));

        } else if (id == R.id.wheelGame_id) {
            startActivity(new Intent(MainActivity.this, WheelActivity.class));

        } else if (id == R.id.convertPoint_id) {

            convertPoint();

        } else if (id == R.id.withdrawActivity_id) {

            if (clickBalanceControl.getBalance()>=500 ){

                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));

            }else {

                Toast.makeText(this, "Sorry..! You don't have enough Balance", Toast.LENGTH_SHORT).show();
            }


        }


        else if (id == R.id.share_id) {

            startActivity(new Intent(MainActivity.this, AccountSetUpActivity.class));


        } else if (id == R.id.aboutMe_id) {



        }else if (id == R.id.logOut_id2) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void convertPoint() {

        if (haveNetwork()){

            if (balanceSetUp.getBalance() >= 5){
                balanceSetUp.Withdraw(5);
                String updateScore = String.valueOf(balanceSetUp.getBalance());
                myRef.child(uID).child("MainBalance").setValue(updateScore);

                clickBalanceControl.AddBalance(500);
                String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                myRef.child(uID).child("ConvertBalance").setValue(updateBalance);

                convertAlert();


            }else {
                Toast.makeText(this, "Sorry..! You don't have enough point", Toast.LENGTH_SHORT).show();
            }


        }else {

            Toast.makeText(this, "Please Check your Net connection", Toast.LENGTH_SHORT).show();

        }



    }


    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.rules_id){

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

    private void convertAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Congratulation..! \n You got Tk500")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }







    private void startStop() {
        if (timeRunning){
            stopTime();
        }else {
            startTime();
        }

    }


    private void startTime() {
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft =millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {

                timeShowTV.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                //drawer.setVisibility(View.VISIBLE);
                floatingActionMenu.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Ready For work", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                finish();

            }
        }.start();
        timeRunning = true;
        //startBtn.setText("Pause");

    }

    private void updateTimer() {

        timeShowTV.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        //drawer.setVisibility(View.GONE);
        floatingActionMenu.setVisibility(View.GONE);

        int minutes = (int) (timeLeft /60000);
        int seconds = (int) (timeLeft % 60000 /1000);
        timeText = ""+minutes;
        timeText += ":";
        if (seconds <10)timeText += "0";
        timeText +=seconds;
        timeShowTV.setText(timeText);


    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        // startBtn.setText("Start");



    }



}
