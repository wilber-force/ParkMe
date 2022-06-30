package com.example.parkme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

public class Login extends AppCompatActivity {

    EditText userIdTxt,passTxt;
    String user;
    String pass;
    static String role;
    private View view;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
    //getSupportActionBar().hide();

        db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");



        userIdTxt = findViewById(R.id.userId);
        passTxt = findViewById(R.id.password);

        findViewById(R.id.signupview).setOnClickListener(V ->{startActivity(new Intent(Login.this, SignUp.class));finish();});
        findViewById(R.id.exitBtn).setOnClickListener(V -> exit(view));

        findViewById(R.id.loginBtn).setOnClickListener(view -> {
                user = userIdTxt.getText().toString().trim();
                pass = passTxt.getText().toString().trim();
            if(user.equalsIgnoreCase("") | pass.equals("")){
                Toast.makeText(getApplicationContext(), "Make sure all fields are filled", Toast.LENGTH_SHORT).show();
            }
            else{

                db.child("Driver").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                              if(dataSnapshot.hasChild(user)) {
                                  String userPass = dataSnapshot.child(user).child("password").getValue(String.class);
                                 role = dataSnapshot.child(user).child("role").getValue(String.class);

                                  if (userPass != null) {
                                      try
                                      {
                                          /* MessageDigest instance for MD5. */
                                          MessageDigest m = MessageDigest.getInstance("MD5");
                                          /* Add plain-text password bytes to digest using MD5 update() method. */
                                          m.update(pass.getBytes());
                                          /* Convert the hash value into bytes */
                                          byte[] bytes = m.digest();
                                          /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
                                          StringBuilder s = new StringBuilder();
                                          for(int i=0; i< bytes.length ;i++)
                                          {
                                              s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                                          }
                                          /* Complete hashed password in hexadecimal format */
                                          pass= s.toString();
                                      }
                                      catch (Exception e)
                                      {
                                          e.printStackTrace();
                                      }
                                       if (pass.equals(userPass)) {
                                          Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                          Intent intent = new Intent(Login.this, MainActivity.class);
                                          intent.putExtra("Userid", user);
                                          startActivity(intent);
                                          finishAfterTransition();
                                      } else {
                                          Toast.makeText(Login.this, "Wrong password please confirm and try again.", Toast.LENGTH_SHORT).show();
                                          passTxt.setText("");
                                          pass = "";
                                      }
                                  }
                              }
                              else{
                                  Toast.makeText(Login.this, "A user with that ID does not exist. Please check and try again or create an account", Toast.LENGTH_SHORT).show();
                                  userIdTxt.setText("");
                              }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    Toast.makeText(Login.this, "Failed to connect. Please try again", Toast.LENGTH_LONG).show();
                                }
                            });
                Toast.makeText(Login.this, "Please wait...", Toast.LENGTH_LONG).show();
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