package com.example.parkme;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;


public class SignUp extends AppCompatActivity {

    EditText fname,sname,idno,password1,password2,phoneno,emailadd;
    String id,pass1,pass2,first,sur,phone,email;
    View view;
    DatabaseReference db;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_signup);
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");

        fname = findViewById(R.id.fname);
        sname = findViewById(R.id.surname);
        idno = findViewById(R.id.idno);
        password1 = findViewById(R.id.mainpass);
        password2 = findViewById(R.id.confirmpass);
        phoneno = findViewById(R.id.phoneno);
        emailadd = findViewById(R.id.email);

        findViewById(R.id.loginView).setOnClickListener(V -> {startActivity(new Intent(SignUp.this,Login.class));finish();});
        findViewById(R.id.exitBtn2).setOnClickListener(V -> exit(view));

        findViewById(R.id.signUpBtn).setOnClickListener(view-> {
            id = idno.getText().toString().trim();
            first = fname.getText().toString().trim();
            sur = sname.getText().toString().trim();
            phone = phoneno.getText().toString().trim();
            email = emailadd.getText().toString().trim();
            pass1 = password1.getText().toString().trim();
            pass2 = password2.getText().toString().trim();
            if (first.isEmpty() || pass1.isEmpty() || sur.isEmpty() || id.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(SignUp.this, "Make sure all fields are filled", Toast.LENGTH_SHORT).show();
            }
            else if(pass1.length() < 8){
                Toast.makeText(this, "Please input a strong pasword", Toast.LENGTH_SHORT).show();
                password1.setError("Error");
            }
            else if(!pass1.equals(pass2)){
                Toast.makeText(SignUp.this, "The passwords did not match.", Toast.LENGTH_SHORT).show();
                password1.setText("");
                password1.setError("Error");
                password2.setText("");
                password2.setError("Error");
            }
            else {

                    db.child("Driver").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(id)) {
                                Toast.makeText(SignUp.this, "The user ID already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                try
                                {
                                    /* MessageDigest instance for MD5. */
                                    MessageDigest m = MessageDigest.getInstance("MD5");
                                    /* Add plain-text password bytes to digest using MD5 update() method. */
                                    m.update(pass1.getBytes());
                                    /* Convert the hash value into bytes */
                                    byte[] bytes = m.digest();
                                    /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
                                    StringBuilder s = new StringBuilder();
                                    for(int i=0; i< bytes.length ;i++)
                                    {
                                        s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                                    }
                                    /* Complete hashed password in hexadecimal format */
                                    pass2= s.toString();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                db.child("Driver").child(id).child("firstname").setValue(first);
                                db.child("Driver").child(id).child("surname").setValue(sur);
                                db.child("Driver").child(id).child("phone").setValue(phone);
                                db.child("Driver").child(id).child("role").setValue("user");
                                db.child("Driver").child(id).child("email").setValue(email);
                                db.child("Driver").child(id).child("password").setValue(pass2);
                                Toast.makeText(SignUp.this, "Details saved successfully.\nYou can now login.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUp.this,Login.class));
                            finishAfterTransition();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SignUp.this, "Connection failed. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(SignUp.this, "Please wait...", Toast.LENGTH_LONG).show();
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