package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.core.app.*;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkme.databinding.ActivityParkBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParkActivity extends DrawerBaseActivity {

    ActivityParkBinding activityParkBinding;

    int i = 1;
    private static final String CHANNEL_ID = "Park Me notification";
    private final int NOTIFICATION_ID = 1;
    private View view;
    EditText vehicle,driver, phone;
    ScrollView scroll;
    String plateno, idDriver, allocated;
    int slots,empty;
    String title,description,mode;
    DatabaseReference db;
    static String results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityParkBinding = ActivityParkBinding.inflate(getLayoutInflater());
        setContentView(activityParkBinding.getRoot());

        //instanciating the editTexts
        vehicle = findViewById(R.id.noplate);
        driver = findViewById(R.id.driver);
        phone = findViewById(R.id.driverphone);
        scroll = findViewById(R.id.outputScrollview);

        //getting values passed from other activities
        Intent intent= getIntent();
        slots = Integer.parseInt(intent.getStringExtra("Numberofslots"));
        mode = intent.getStringExtra("Mode");
        empty = Integer.parseInt(intent.getStringExtra("Empty slots"));
        title = intent.getStringExtra("Title");
        description = intent.getStringExtra("Description");
        allocated = intent.getStringExtra("Allocated");

        //adding the selected option
        TextView txt = new TextView(this);
        String textToSet = "You have selected: \n" + "Title: "+ title + "\nLocation: " + description + "\nMode: " + mode + "\nSlots:  " + slots + "\n";
        txt.setText(textToSet);
        txt.setTextSize(20);
        scroll.addView(txt);

        findViewById(R.id.cancel).setOnClickListener(v->exit(view));

findViewById(R.id.ok).setOnClickListener(view -> {
    //Getting  the values from the edit texts
    plateno = vehicle.getText().toString();
    idDriver = driver.getText().toString();
    String phoneNumber = phone.getText().toString();
    if(plateno.isEmpty() || idDriver.isEmpty() || phoneNumber.isEmpty()){
        Toast.makeText(this, "Make sure all field are filled", Toast.LENGTH_SHORT).show();
    }
    else {

//saving the user selection into the database
        db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {
            int used;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                used = Integer.parseInt(allocated);
                int usedSlots = slots;

                //checking the existance of the id number
                if (snapshot.child("Driver").hasChild(idDriver)) {
                    if(snapshot.child("Reserved").hasChild(idDriver)){
                        Toast.makeText(ParkActivity.this, "The user has already reserved a parking", Toast.LENGTH_SHORT).show();
                    }
                    else if(snapshot.child("Parked").hasChild(idDriver)){
                        Toast.makeText(ParkActivity.this, "The user has already parked his/her vehicle", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (i == 1) {
                            // saving the values
                            db.child("Parked").child(idDriver).child("Vehicle number plate").setValue(plateno);
                            db.child("Parked").child(idDriver).child("Slots used").setValue(usedSlots);
                            db.child("Parked").child(idDriver).child("Mode").setValue(mode);
                            db.child("Parked").child(idDriver).child("Parking").setValue(title);

                            empty = empty - slots;
                            slots = slots + used;
                            db.child("Parkings").child(title).child("allocated_slots").setValue(slots);
                            db.child("Parkings").child(title).child("slots").setValue("" + empty);

                            i = 2;
                        }
                        //notify the user that data is saved
                        createNotificationChannel();
                        NotificationCompat.Builder comp = new NotificationCompat.Builder(ParkActivity.this, CHANNEL_ID);
                        comp.setSmallIcon(R.drawable.parking);
                        comp.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.parking));
                        comp.setContentTitle("Successful");
                        comp.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        results = " Vehicle's number plate: " + plateno + ".\n Driver's ID number: " + idDriver + ".\n" + "Parking details are: \n" + title + " located at " + description + " for a " + mode + " in " + usedSlots + " slots.";
                        comp.setStyle(new NotificationCompat.BigTextStyle().bigText("Your parking details are: \n" + results));
                        //show the pop up
                        NotificationManagerCompat notification = NotificationManagerCompat.from(ParkActivity.this);
                        notification.notify(NOTIFICATION_ID, comp.build());
                        new OutputDialog().show(getSupportFragmentManager(), "OutputDialog");

                        // sendSMS(phoneNumber,results);
                    }
                } else {
                    Toast.makeText(ParkActivity.this, "The driver is not registered in the system.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ParkActivity.this, "Failed to connect" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
});

    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Park Me";
            String description = "Include all the details";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            noti.createNotificationChannel(channel);
        }
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
    protected void sendSMS(String reciever, String message) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , reciever);
        smsIntent.putExtra("sms_body"  , message);

        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ParkActivity.this, "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

}