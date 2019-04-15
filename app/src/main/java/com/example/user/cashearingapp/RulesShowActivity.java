package com.example.user.cashearingapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RulesShowActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RulesAdapter adapter;
    List<RulesClass>rulesList;
    RulesClass rulesClass;
    ProgressDialog progressDialog;

    FirebaseDatabase database;
    DatabaseReference myRef;
    AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_show);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserBalance");
        recyclerView = findViewById(R.id.rulesRecyclerView_id);
        rulesList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(RulesShowActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        progressDialog.show();
        progressDialog.setMessage("Information is loading...");

       /* mAdView = findViewById(R.id.rulesBannerAdView_id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/


        myRef.child("UserRules").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if (dataSnapshot.exists()){

                    progressDialog.dismiss();
                    rulesList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        rulesClass = snapshot.getValue(RulesClass.class);
                        rulesList.add(rulesClass);

                        adapter = new RulesAdapter (getApplicationContext(),rulesList);
                        recyclerView.setAdapter(adapter);

                    }
                    adapter.notifyDataSetChanged();

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(RulesShowActivity.this, "Data is Empty", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });



    }
}
