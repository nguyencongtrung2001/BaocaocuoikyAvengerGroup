package com.example.baocaocuoiky.Trung;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baocaocuoiky.MainActivity;
import com.example.baocaocuoiky.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText txt_email, txt_pass;
    Button button_login;
    TextView txt_forgetpass , txt_sigin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        txt_email = findViewById(R.id.txt_email);
        txt_pass = findViewById(R.id.txt_pass);

        txt_sigin = findViewById(R.id.textsign);
        txt_sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        txt_forgetpass = findViewById(R.id.textforgetpassword);
        txt_forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        button_login = findViewById(R.id.button_login);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() | !validatePassword()){
                    return;
                } else {
                    checkUser();
                }
            }
        });
    }

    public boolean validateEmail(){
        String val = txt_email.getText().toString();
        if (val.isEmpty()){
            txt_email.setError("Email cannot be empty");
            return false;
        } else {
            txt_email.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        String val = txt_pass.getText().toString();
        if (val.isEmpty()){
            txt_pass.setError("Password cannot be empty");
            return false;
        } else {
            txt_pass.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = txt_email.getText().toString().trim();
        String userPassword = txt_pass.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve all users and check for the matching email
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userExists = false;
                String passwordFromDB = "";

                // Loop through all users and check if email matches
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                    if (emailFromDB != null && emailFromDB.equals(userEmail)) {
                        passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        userExists = true;
                        break;
                    }
                }

                if (userExists) {
                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        // Credentials are correct, proceed to MainActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        // Password is incorrect
                        txt_pass.setError("Invalid Credentials");
                        txt_pass.requestFocus();
                    }
                } else {
                    // User does not exist
                    txt_email.setError("User does not exist");
                    txt_email.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors if any
            }
        });
    }

}
