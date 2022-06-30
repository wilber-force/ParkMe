package com.example.parkme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.parkme.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends DrawerBaseActivity {

    ActivityMainBinding activityDashboardBinding;

    RecyclerView recyclerView;
    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");
    private  ArrayList<Parkings> parking;
   public static String userid;
    static String userfname;
    static String userlname;
    static String usermail;
    static String userphone;
    MyAdapter myAdapter;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        Intent i = getIntent();
        userid = i.getStringExtra("Userid");

        recyclerView = findViewById(R.id.recycleView);
        parking = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        myAdapter = new MyAdapter(parking, this);
        UserDetails();

        recyclerView.setAdapter(myAdapter);
        loadrecyclerViewData();

    }

    private void loadrecyclerViewData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parkings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parking.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Parkings modelCourses1 = dataSnapshot1.getValue(Parkings.class);
                    parking.add(modelCourses1);
                    myAdapter = new MyAdapter(parking, MainActivity.this);
                    recyclerView.setAdapter(myAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void exit(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Exit..!!!");
        alertDialogBuilder.setIcon(R.drawable.question);
        alertDialogBuilder.setMessage("Are you sure you want to exit this page?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"You clicked over cancel",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void UserDetails(){
        db.child("Driver").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userfname = snapshot.child("firstname").getValue(String.class);
                userlname = snapshot.child("surname").getValue(String.class);
                usermail = snapshot.child("email").getValue(String.class);
                userphone = snapshot.child("phone").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}