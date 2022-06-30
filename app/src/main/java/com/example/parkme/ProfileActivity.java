package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    String userid,firstname,lastname,email,phone;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    TextView id,fname,lname,mail,phoneno;
    EditText phoneEdit,emailEdit;
    LinearLayout visible,hidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userid = MainActivity.userid;

        id = findViewById(R.id.dbidno);
        fname = findViewById(R.id.dbfname);
        lname = findViewById(R.id.dbsurname);
        mail = findViewById(R.id.dbemail);
        phoneno = findViewById(R.id.dbphoneno);

        phoneEdit = findViewById(R.id.userdbphoneno);
        emailEdit = findViewById(R.id.userdbemail);

        visible = findViewById(R.id.linearLayout);
        hidden = findViewById(R.id.hiddenLinearLayout);
        UserDetails();


        findViewById(R.id.dbeditdetails).setOnClickListener(view -> {
            visible.setVisibility(View.GONE);
            hidden.setVisibility(View.VISIBLE);
            phoneEdit.setText(phone);
            emailEdit.setText(email);
        });

        findViewById(R.id.dbexit).setOnClickListener(v->{finishAndRemoveTask();});

        findViewById(R.id.dbsavedetails).setOnClickListener(v->{
            if(phoneEdit.getText().toString().isEmpty() ||emailEdit.getText().toString().isEmpty()){
                Toast.makeText(this, "The fields cannot be left empty", Toast.LENGTH_SHORT).show();
            }
            else {
                db.child("Driver").child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.child("Driver").child(userid).child("email").setValue(emailEdit.getText().toString());
                        db.child("Driver").child(userid).child("phone").setValue(phoneEdit.getText().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        findViewById(R.id.dbcancel).setOnClickListener(view -> {
            hidden.setVisibility(View.GONE);
            visible.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.dbexit).setOnClickListener(v->{
            finishAndRemoveTask();
        });

    }
    public void UserDetails(){

        db.child("Driver").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firstname = snapshot.child("firstname").getValue(String.class);
                lastname = snapshot.child("surname").getValue(String.class);
                email = snapshot.child("email").getValue(String.class);
                phone = snapshot.child("phone").getValue(String.class);


                id.setText(userid);
                fname.setText(fname.getText()+firstname);
                lname.setText(lname.getText()+lastname);
                mail.setText(mail.getText()+email);
                phoneno.setText(phoneno.getText()+phone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}