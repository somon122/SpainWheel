package com.example.user.cashearingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.cashearingapp.PhoneAuth.PhoneAuthActivity;
import com.example.user.cashearingapp.PhoneAuth.PhoneAuthConfirmActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private TextView deviceId,timeShowTV;
    ConstraintLayout waitingLayout;
    TimePicker timePicker;
    ProgressBar progressBar;
    int backSpaceCount = 0;

    FloatingActionButton rulesButton, wheelButton,quziButton,loveButton;

    Locale locale;

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseFirestore mFirestone;


    FirebaseAuth auth;
    FirebaseUser user;
    String uID;
    BalanceSetUp balanceSetUp;
    ClickBalanceControl clickBalanceControl;

    CountDownTimer countDownTimer;
    long timeLeft = 30000;
    boolean timeRunning;
    String timeText;
    String phoneNo;
    FloatingActionMenu floatingActionMenu;

    RecyclerView recyclerMyWOrkView;
    private List<MyWorkClass> myWorkList;
    private MyWorkAdapter adapter;

    ImageView reLoad;
    ConstraintLayout constraintLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reLoad = findViewById(R.id.mainPageReLoadImage_id);
        constraintLayout = findViewById(R.id.mainPageReLoad_id);

        reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });




        if (haveNetwork()){

            constraintLayout.setVisibility(View.GONE);
            initilized();
            mFirestone = FirebaseFirestore.getInstance();
            timeShowTV.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            recyclerMyWOrkView = findViewById(R.id.recyclerMyWOrkView_id);
            myWorkList = new ArrayList<>();
            Bundle bundle = getIntent().getExtras();

            if (bundle != null){
                startStop();

            }
            pointLoad();
            RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
            recyclerMyWOrkView.setLayoutManager(manager);
            adapter = new MyWorkAdapter (getApplicationContext(),myWorkList);
            recyclerMyWOrkView.setAdapter(adapter);
            recyclerMyWOrkView.setHasFixedSize(true);



        }else {

            Toast.makeText(this, "Please Check Your Net Connection ..ok!", Toast.LENGTH_SHORT).show();
        }



    }


    //---------- OnCreate is End point -----------------

    private void pointLoad(){


        if (haveNetwork()){

            if (user != null) {

                myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if (dataSnapshot.exists()){

                            progressBar.setVisibility(View.GONE);
                            String value = dataSnapshot.getValue(String.class);
                            balanceSetUp.setBalance(Integer.parseInt(value));
                        }else {
                            progressBar.setVisibility(View.GONE);

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value

                    }
                });
                myRef.child("Users").child(phoneNo).child(uID).child("ConvertBalance").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        if (dataSnapshot.exists()){
                            progressBar.setVisibility(View.GONE);
                            String value = dataSnapshot.getValue(String.class);
                            clickBalanceControl.setBalance(Integer.parseInt(value));

                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, " Data is loading...", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value

                    }
                });



                //String pushId = myRef.push().getKey();

                myRef.child("MyWork").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        myWorkList.clear();
                        progressBar.setVisibility(View.GONE);

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            MyWorkClass myWorkClass = snapshot.getValue(MyWorkClass.class);
                            myWorkList.add(myWorkClass);

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        progressBar.setVisibility(View.GONE);

                    }
                });

            }

        }else {
            Toast.makeText(this, " Please Check your Net connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void initilized(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deviceId = findViewById(R.id.deviceId);
        timePicker = new TimePicker(this);
        waitingLayout = findViewById(R.id.waiting_id);



        rulesButton = findViewById(R.id.rules_id);
        loveButton = findViewById(R.id.love_id);
        wheelButton = findViewById(R.id.wheelSpin_id);
        quziButton = findViewById(R.id.quiz_id);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");

        balanceSetUp = new BalanceSetUp();
        clickBalanceControl = new ClickBalanceControl();
        waitingLayout.setVisibility(View.GONE);

        if (user != null) {
            uID = user.getUid();
            phoneNo = user.getPhoneNumber();
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
        backSpaceCount++;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (backSpaceCount==2){
                backSpassAlert();
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
            startActivity(new Intent(MainActivity.this, PhoneAuthConfirmActivity.class));


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
                myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(updateScore);

                clickBalanceControl.AddBalance(500);
                String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                myRef.child("Users").child(phoneNo).child(uID).child("ConvertBalance").setValue(updateBalance);

                convertAlert();


            }else {
                Toast.makeText(this, "Sorry..! You don't have enough point"+balanceSetUp.getBalance(), Toast.LENGTH_SHORT).show();
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

        builder.setTitle("Convert Point")
                .setIcon(R.drawable.full_love)
                .setMessage("Congratulation..! \n You got Tk500")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
 private void backSpassAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Are you Sure to exit..!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       finish();

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
                waitingLayout.setVisibility(View.GONE);
                floatingActionMenu.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);

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
        waitingLayout.setVisibility(View.VISIBLE);
        floatingActionMenu.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);


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
