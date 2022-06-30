package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkme.databinding.ActivityDeallocateBinding;
import com.example.parkme.databinding.ActivityReserveBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeallocateActivity extends DrawerBaseActivity {

    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");
    String userid = MainActivity.userid;
    TextView txt;
    ScrollView scrollView;
    String results;
    ActivityDeallocateBinding activityDeallocateBinding;
    Button deallocate,back;
    String title,slots,mode,reserveDate,reserveTime,vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDeallocateBinding = ActivityDeallocateBinding.inflate(getLayoutInflater());
        setContentView(activityDeallocateBinding.getRoot());


        scrollView = findViewById(R.id.deallocatedetails);
        deallocate = findViewById(R.id.deallocatebtn);
        back = findViewById(R.id.backbtn);
        txt = new TextView(this);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Parked").hasChild(userid)){
                    results = "You have selected park";
                    txt.setText(results);
                    scrollView.addView(txt);
                }
                else if(snapshot.child("Reserved").hasChild(userid)){
                    results = "You have selected ";
                    txt.setText(results);
                    scrollView.addView(txt);
                }
                else{
                    results = "You have selected no parking";
                    txt.setText(results);
                    scrollView.addView(txt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeallocateActivity.this,"Failed to connect",Toast.LENGTH_SHORT).show();
            }
        });
        deallocate.setOnClickListener(v->{
            if(db.child("Parked").child(userid)!=null){
                db.child("Parked").child(userid).removeValue();
            }
            else if(db.child("Reserved").child(userid)!=null){

            }
        });

    }
}