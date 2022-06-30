package com.example.parkme;


import android.os.Bundle;

import com.example.parkme.databinding.ActivityUpgradeUserBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpgradeUserActivity extends DrawerBaseActivity {
    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");

    ActivityUpgradeUserBinding activityUpgradeUserBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUpgradeUserBinding = ActivityUpgradeUserBinding.inflate(getLayoutInflater());
        setContentView(activityUpgradeUserBinding.getRoot());
    }
}