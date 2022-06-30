package com.example.parkme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {

    ImageView imageView;
    String userid = MainActivity.userid;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
/*
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(view1 -> {
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        });
*/
        findViewById(R.id.okpass).setOnClickListener(v->{
            String oldPass,newPass,dbpass;
            EditText old,pass;
            old = findViewById(R.id.userdbpassword1);
            pass = findViewById(R.id.userdbpassword2);

            oldPass = old.getText().toString();
            newPass = pass.getText().toString();
            if(oldPass.isEmpty() || newPass.isEmpty()){
                Toast.makeText(this, "Ensure all fields are filled", Toast.LENGTH_SHORT).show();
            }
            else {

                if(newPass.length()<8){
                    Toast.makeText(this, "You entered a weak password. It must be more than 8 characters", Toast.LENGTH_SHORT).show();
                    pass.setError("Error");
                }
                else if(newPass.equals(oldPass)){
                    Toast.makeText(this, "The passwords cannot be the same", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.child("Driver").child(userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String user = snapshot.child("password").getValue(String.class);
                            if (user.equals(oldPass)) {
                                db.child("Driver").child(userid).child("password").setValue(newPass);
                                Toast.makeText(ChangePassword.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ChangePassword.this,Login.class));
                                finishAfterTransition();
                            } else {
                                Toast.makeText(ChangePassword.this, "Your old password did not match with the one in the database.", Toast.LENGTH_SHORT).show();
                                old.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChangePassword.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        findViewById(R.id.cancelpass).setOnClickListener(view1 -> finishAndRemoveTask());

    }
}
