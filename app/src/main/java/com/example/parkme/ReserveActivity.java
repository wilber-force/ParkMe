package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.parkme.databinding.ActivityReserveBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ReserveActivity extends DrawerBaseActivity implements View.OnClickListener  {

    ActivityReserveBinding activityReserveBinding;

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private static final String CHANNEL_ID = "Park Me notification";
    private final int NOTIFICATION_ID = 1;
    private View view;
    EditText vehicle,driver,phoneno;
    ScrollView scroll;
    String plateno, idDriver, allocated, phone;
    int slots,empty,used;
    String title,description,mode,reserveDate,reserveTime;
    DatabaseReference db;
    int i = 1;
    static String results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityReserveBinding = ActivityReserveBinding.inflate(getLayoutInflater());
        setContentView(activityReserveBinding.getRoot());

        //instanciating the editTexts
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);
        vehicle = findViewById(R.id.vehiclenoplate);
        driver = findViewById(R.id.driverid);
        scroll = findViewById(R.id.reserveScrollview);
        phoneno = findViewById(R.id.drivernumber);


        //getting values passed from other activities
        Intent intent= getIntent();
        slots = Integer.parseInt(intent.getStringExtra("Numberofslots"));
        mode = intent.getStringExtra("Mode");
        empty = Integer.parseInt(intent.getStringExtra("Empty slots"));
        title = intent.getStringExtra("Title");
        description = intent.getStringExtra("Description");
        allocated = intent.getStringExtra("Allocated");

        //adding the selected options to the scrollview
        TextView txt = new TextView(this);
        String textToSet = "You have selected: \n" + "Title: "+ title + "\nLocation: " + description + "\nMode: " + mode + "\nSlots:  " + slots + "\n";
        txt.setText(textToSet);
        txt.setTextSize(20);
        scroll.addView(txt);

        //adding event listeners
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        findViewById(R.id.reserve_cancel).setOnClickListener(view1 -> {exit(view);});
        findViewById(R.id.confirm).setOnClickListener(view -> {
            //Getting  the values from the edit texts
            plateno = vehicle.getText().toString();
            idDriver = driver.getText().toString();
            reserveDate = txtDate.getText().toString();
            reserveTime = txtTime.getText().toString();
            phone = phoneno.getText().toString();
if(plateno.isEmpty() || idDriver.isEmpty() || reserveTime.isEmpty() || reserveDate.isEmpty() || phone.isEmpty()){
    Toast.makeText(this, "Make sure the above fields are filled", Toast.LENGTH_SHORT).show();
}
else {
    //saving the user selection into the database
    db = FirebaseDatabase.getInstance().getReference();
    db.addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            used = Integer.parseInt(allocated);
            int usedSlots = slots;

            //checking the existance of the id number
            if (snapshot.child("Driver").hasChild(idDriver)) {
                if(snapshot.child("Reserved").hasChild(idDriver)){
                    Toast.makeText(ReserveActivity.this, "The user has already reserved a parking", Toast.LENGTH_SHORT).show();
                }
                else if(snapshot.child("Parked").hasChild(idDriver)){
                    Toast.makeText(ReserveActivity.this, "The user has already parked his/her vehicle", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (i == 1) {
                        // saving the values
                        db.child("Reserved").child(idDriver).child("Vehicle number plate").setValue(plateno);
                        db.child("Reserved").child(idDriver).child("Slots used").setValue(usedSlots);
                        db.child("Reserved").child(idDriver).child("Mode").setValue(mode);
                        db.child("Reserved").child(idDriver).child("Date").setValue(reserveDate);
                        db.child("Reserved").child(idDriver).child("Time").setValue(reserveTime);
                        db.child("Reserved").child(idDriver).child("Parking").setValue(title);

                        empty = empty - slots;
                        slots = slots + used;
                        db.child("Parkings").child(title).child("allocated_slots").setValue(slots);
                        db.child("Parkings").child(title).child("slots").setValue("" + empty);

                        i = 2;
                    }
                    //notify the user that data is saved
                    createNotificationChannel();
                    NotificationCompat.Builder comp = new NotificationCompat.Builder(ReserveActivity.this, CHANNEL_ID);
                    comp.setSmallIcon(R.drawable.parking);
                    comp.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.parking));
                    comp.setContentTitle("Successful");
                    comp.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    results = "Vehicle's number plate: " + plateno + ".\n Driver's ID number: " + idDriver + ".\n" + "Parking details are: \n" + title + " located at " + description + " for a " + mode + " in " + usedSlots + " slots \nOn " + reserveDate + " at " + reserveTime;
                    comp.setStyle(new NotificationCompat.BigTextStyle().bigText("Your parking details are: \n" + results));
                    //show the pop up
                    NotificationManagerCompat notification = NotificationManagerCompat.from(ReserveActivity.this);
                    notification.notify(NOTIFICATION_ID, comp.build());
                    new OutputDialog().show(getSupportFragmentManager(), "OutputDialog");
                    //sendSMS(phone, results);
                }

            } else {
                Toast.makeText(ReserveActivity.this, "The driver is not registered in the system.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(ReserveActivity.this, "Failed to connect" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
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
            Toast.makeText(ReserveActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

}