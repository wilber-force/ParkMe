package com.example.parkme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkme.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends DrawerBaseActivity {

    ActivityHomeBinding activityHomeBinding;

    TextView noOfSlots;
    int slots,emptySlots;
    RadioButton selected;
    TextView details,empty;
    private View view;
    String description,title,mode,avail,slotsavail, allocated;
    RadioGroup radioGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        //get the values from the main activity
        Intent intent= getIntent();
        description=intent.getStringExtra("Description");
         title=intent.getStringExtra("Title");
        slotsavail=intent.getStringExtra("Empty slots");
        allocated = intent.getStringExtra("Allocated");

        //get the id from the xml
        radioGroup = findViewById(R.id.radiogroup);
         empty = findViewById(R.id.availableSlots);
        details = findViewById(R.id.locationdetails);
        noOfSlots = findViewById(R.id.available);

        //set the text on the screen
        details.setText(title +"\n"+description);
        empty.setText(slotsavail);

        //assign action listeners
        findViewById(R.id.cancelBtn).setOnClickListener(v ->exit(view));

        //addition of slots
        findViewById(R.id.plus).setOnClickListener(view ->{
            slots = Integer.parseInt(noOfSlots.getText().toString());
            if (slots > 5){Toast.makeText(getApplicationContext(),"You cannot reserve/park more than five slots. Please reduce the value and try again.", Toast.LENGTH_SHORT).show();slots = 4;}
            else{slots = slots+1;noOfSlots.setText(""+slots);}
        });

        //subtraction of slots
        findViewById(R.id.minus).setOnClickListener(view ->{
            slots = Integer.parseInt(noOfSlots.getText().toString());
            if (slots < 1){Toast.makeText(getApplicationContext(),"You cannot fail to reserve/park a slot. Please increase the value and try again.", Toast.LENGTH_SHORT).show();slots = 1; }
            else{slots = slots-1;noOfSlots.setText(""+slots);}
        });

        //the ok button
        findViewById(R.id.okBtn).setOnClickListener(v ->{
            Toast.makeText(this, " "+ allocated, Toast.LENGTH_SHORT).show();
            emptySlots = Integer.parseInt(empty.getText().toString());
            //if the slots available in the database are more than the input slots
            if (emptySlots >= slots){
                //selecting the mode of parking
                            selected = findViewById(radioGroup.getCheckedRadioButtonId());
                            mode = selected.getText().toString();

                            if(mode.isEmpty()){
                                Toast.makeText(HomeActivity.this, "Please select a parking mode", Toast.LENGTH_SHORT).show();
                            }
                    else {
                        if(mode.equalsIgnoreCase("Park now")){
                    Intent i = new Intent(this, ParkActivity.class);
                                i.putExtra("Numberofslots", ""+slots);
                                i.putExtra("Mode", mode);
                                i.putExtra("Empty slots", slotsavail);
                                i.putExtra("Title", title);
                                i.putExtra("Description", description);
                                i.putExtra("Allocated", allocated);
                                startActivity(i);
                                this.finishAfterTransition();
                        }
                        else if (mode.equalsIgnoreCase("Reserve")){
                            Intent i = new Intent(this, ReserveActivity.class);
                            i.putExtra("Numberofslots", ""+slots);
                            i.putExtra("Mode", mode);
                            i.putExtra("Empty slots", slotsavail);
                            i.putExtra("Title", title);
                            i.putExtra("Description", description);
                            i.putExtra("Allocated", allocated);
                            startActivity(i);
                            this.finishAfterTransition();
                        }
                        else if (mode.equalsIgnoreCase("Deallocate")){
                            Intent i = new Intent(this, DeallocateActivity.class);
                            i.putExtra("Empty slots", slotsavail);
                            startActivity(i);
                            this.finishAfterTransition();
                        }
                        else{
                            Toast.makeText(this,"The mode is not yet implemented",Toast.LENGTH_SHORT).show();
                        }
                    }
            }


            else {
                Toast.makeText(this, "The selected parking cannot accomodate the proposed number of vehicles at the moment.", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void loadEmptyslots() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Parkings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            avail = dataSnapshot.child(title).child("Slots available").getValue(String.class);
            empty.setText(avail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
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

}
