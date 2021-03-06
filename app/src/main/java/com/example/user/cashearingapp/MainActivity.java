package com.example.user.cashearingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import com.google.android.gms.appinvite.AppInviteInvitation;
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
import com.google.firebase.firestore.FieldValue;
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
    private ConstraintLayout waitingLayout;
    private TimePicker timePicker;
    private ProgressBar progressBar;
    private int backSpaceCount = 0;

    private final int REQUEST_INVITE = 420;

    private FloatingActionButton rulesButton, wheelButton,quziButton,loveButton;

    private Locale locale;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore mFirestone;


    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uID;
    private BalanceSetUp balanceSetUp;
    private ClickBalanceControl clickBalanceControl;

    private CountDownTimer countDownTimer;
    private long timeLeft = 30000;
    boolean timeRunning;
    private String timeText;
    private String phoneNo;
    private FloatingActionMenu floatingActionMenu;

    private RecyclerView recyclerMyWOrkView;
    private List<MyWorkClass> myWorkList;
    private MyWorkAdapter adapter;

    private ImageView reLoad;
    private ConstraintLayout constraintLayout;

    private SharedPreferences sharedPreferences;
    private int workControl = 0;



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


            sharedPreferences = this.getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
            workControl = sharedPreferences.getInt("workControl",0);



            if (bundle != null){
                workControl++;
                sharedPreferences = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("workControl", workControl);
                editor.commit();
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

                        if (dataSnapshot.exists()){
                            myWorkList.clear();
                            progressBar.setVisibility(View.GONE);

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                MyWorkClass myWorkClass = snapshot.getValue(MyWorkClass.class);
                                myWorkList.add(myWorkClass);

                            }
                            adapter.notifyDataSetChanged();

                        }else {
                            progressBar.setVisibility(View.GONE);

                        }


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
       /* TextView textView = findViewById(R.id.serverTime_id);
        textView.setText((CharSequence) FieldValue.serverTimestamp());
*/


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

            if (backSpaceCount==1){
                super.onBackPressed();

            }else {
                backSpaceCount++;
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
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
        if (id == R.id.rules420_id){
            startActivity(new Intent(MainActivity.this, RulesShowActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.love2_id) {

            if (workControl ==1){
                startActivity(new Intent(MainActivity.this, TaskActivity.class));
            }else if (workControl == 0){
                Toast.makeText(this, "Please Complete Wheel Spin Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else if (workControl==2){
                Toast.makeText(this, "Please Complete Quiz Question Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.mcq_id) {

            if (workControl ==1){
                Toast.makeText(this, "Please Complete  Love Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();

            }else if (workControl == 0){
                Toast.makeText(this, "Please Complete Wheel Spin Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else if (workControl==2){
                startActivity(new Intent(MainActivity.this, QuestionWorkActivity.class));



            }else {
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }


        }else if (id == R.id.dashBoard_id) {
            startActivity(new Intent(MainActivity.this, DashBoadActivity.class));

        } else if (id == R.id.wheelGame_id) {


            if (workControl ==1){

                Toast.makeText(this, "Please Complete Love Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();

            }else if (workControl == 0){
                startActivity(new Intent(MainActivity.this, WheelActivity.class));


            }else if (workControl==2){
                Toast.makeText(this, "Please Complete Quiz Question Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.convertPoint_id) {

            convertPoint();

        } else if (id == R.id.withdrawActivity_id) {

            if (clickBalanceControl.getBalance()>=300 ){

                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));

            }else {

                Toast.makeText(this, "Sorry..! You don't have enough Balance\n\n Minimum Withdraw TK 300", Toast.LENGTH_LONG).show();
            }


        }


        else if (id == R.id.share_id) {


            onInviteClicked();



        } else if (id == R.id.rulesShow22_id) {

            startActivity(new Intent(MainActivity.this, RulesShowActivity.class));


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

    private void onInviteClicked() {
        @SuppressLint("ResourceType") Intent intent = new AppInviteInvitation.IntentBuilder("Cash Earning App")
                .setMessage("Download this app for best Income")
                .setDeepLink(Uri.parse("https://drive.google.com/open?id=1VoBbilyVl3QdO6lQCn9At4sNENakMlcg"))
               /* .setCustomImage(Uri.parse(getString(R.drawable.cashearninglogo)))*/
                .setCallToActionText("Hello")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void convertPoint() {

        if (haveNetwork()){

            if (balanceSetUp.getBalance() >= 30000){
                balanceSetUp.Withdraw(30000);
                String updateScore = String.valueOf(balanceSetUp.getBalance());
                myRef.child("Users").child(phoneNo).child(uID).child("MainBalance").setValue(updateScore);

                clickBalanceControl.AddBalance(300);
                String updateBalance = String.valueOf(clickBalanceControl.getBalance());
                myRef.child("Users").child(phoneNo).child(uID).child("ConvertBalance").setValue(updateBalance);

                convertAlert();


            }else {
                Toast.makeText(this, "Sorry..! You don't have enough point\n\nMinimum 30000 points needed", Toast.LENGTH_LONG).show();
            }


        }else {

            Toast.makeText(this, "Please Check your Net connection", Toast.LENGTH_SHORT).show();

        }



    }


    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.rules_id){

            startActivity(new Intent(MainActivity.this,RulesShowActivity.class));

        }
        if (v.getId()==R.id.wheelSpin_id){


            if (workControl ==1){

                Toast.makeText(this, "Please Complete Love Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();

            }else if (workControl == 0){
                startActivity(new Intent(MainActivity.this, WheelActivity.class));


            }else if (workControl==2){
                Toast.makeText(this, "Please Complete Quiz Question Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }


        }
        if (v.getId()==R.id.quiz_id){

            if (workControl ==1){
                Toast.makeText(this, "Please Complete  Love Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();

            }else if (workControl == 0){
                Toast.makeText(this, "Please Complete Wheel Spin Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else if (workControl==2){
                startActivity(new Intent(MainActivity.this, QuestionWorkActivity.class));



            }else {
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }


        }
        if (v.getId()==R.id.love_id){

            if (workControl ==1){
                startActivity(new Intent(MainActivity.this, TaskActivity.class));
            }else if (workControl == 0){
                Toast.makeText(this, "Please Complete Wheel Spin Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else if (workControl==2){
                Toast.makeText(this, "Please Complete Quiz Question Game \n\n Then Try Again.. ok !", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }



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
                .setMessage("Congratulation..! \n You got Tk300")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
 private void rulesAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("আপনি কি সব নিয়মকানুন জানেন?\n(Are you know all Trams and Conditions?)")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,MainActivity.class));

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this,RulesShowActivity.class));

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
                if (workControl == 3){
                    workControl = workControl-3;
                    sharedPreferences = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("workControl", workControl);
                    editor.commit();
                }
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
