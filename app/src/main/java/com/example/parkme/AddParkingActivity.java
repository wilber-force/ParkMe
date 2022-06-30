package com.example.parkme;


import android.os.Bundle;

import com.example.parkme.databinding.ActivityAddParkingBinding;

public class AddParkingActivity extends DrawerBaseActivity {

    ActivityAddParkingBinding activityAddParkingBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddParkingBinding = ActivityAddParkingBinding.inflate(getLayoutInflater());
        setContentView(activityAddParkingBinding.getRoot());
    }
}