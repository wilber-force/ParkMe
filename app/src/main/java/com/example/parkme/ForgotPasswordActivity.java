package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button reset,cancel;
    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://parkme-cf469-default-rtdb.firebaseio.com");
    String userid,userphone,useremail,newpass;
    EditText idnumber,phoneno,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgot_password);

        reset = findViewById(R.id.resetpassword);
        cancel = findViewById(R.id.cancelforgotpassword);
        idnumber = findViewById(R.id.idnumberconfrim);
        phoneno = findViewById(R.id.phonenumberconfirm);
        email = findViewById(R.id.emailconfirm);

        reset.setOnClickListener(view->{
            userid = idnumber.getText().toString().trim();
            userphone = phoneno.getText().toString().trim();
            useremail = email.getText().toString().trim();

            db.child("Driver").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(userid)){
                        String dbphone = snapshot.child(userid).child("").getValue(String.class);
                        String dbemail = snapshot.child(userid).child("").getValue(String.class);
                        if(dbphone.equalsIgnoreCase(userphone) || dbemail.equalsIgnoreCase(useremail)){
                            //further implementation of reseting the password

                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, "The credentials you entered are not correct", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ForgotPasswordActivity.this,"The user does not exit", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    public void passwordChanged(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Successful..!!!");
        alertDialogBuilder.setIcon(R.drawable.successful);
        alertDialogBuilder.setMessage("Password changed successfully.\nYour new password is "+newpass+" use it to login and change it immediately.");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                startActivity(new Intent(ForgotPasswordActivity.this,Login.class));
                finishAfterTransition();
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