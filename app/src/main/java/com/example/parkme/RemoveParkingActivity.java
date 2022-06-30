package com.example.parkme;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkme.databinding.ActivityRemoveParkingBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RemoveParkingActivity extends DrawerBaseActivity {
    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");

    ActivityRemoveParkingBinding activityRemoveParkingBinding;
    Button search,changedetails,deleteparkings,exitparkings,exitsearch;
    EditText parkingtitle;
    ScrollView scrollView;
    LinearLayout scrollayout,searchlayout;
    String title,description,slots;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRemoveParkingBinding = ActivityRemoveParkingBinding.inflate(getLayoutInflater());
        setContentView(activityRemoveParkingBinding.getRoot());

        search = findViewById(R.id.searchparking);
        changedetails = findViewById(R.id.changeparkingdetails);
        deleteparkings = findViewById(R.id.deleteparking);
        exitparkings = findViewById(R.id.parkingexit);
        exitsearch = findViewById(R.id.exitparking);

        parkingtitle = findViewById(R.id.searcheEditid);

        scrollView = findViewById(R.id.scrollviewparking);

        scrollayout = findViewById(R.id.scrolllinearlayout);
        searchlayout = findViewById(R.id.searchbtns);
        txt = new TextView(this);

        search.setOnClickListener(view->{
            title = parkingtitle.getText().toString().trim();
            db.child("Parking").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(title)){
                        searchlayout.setVisibility(View.GONE);
                        scrollayout.setVisibility(View.VISIBLE);
                        description = snapshot.child(title).child("description").getValue(String.class);
                        slots = snapshot.child(title).child("total slots").getValue(String.class);
                        txt.setText("Title: "+title+"\nDescription: "+description+"\nTotal slots: "+slots);
                        scrollView.addView(txt);
                    }
                    else{
                        Toast.makeText(RemoveParkingActivity.this, "The specified parking does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RemoveParkingActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            });
        });
        changedetails.setOnClickListener(view->{

        });
        deleteparkings.setOnClickListener(view->{

        });
        exitsearch.setOnClickListener(view->{
            finishAndRemoveTask();
        });
        exitparkings.setOnClickListener(view->{
            finishAndRemoveTask();
        });
    }
}