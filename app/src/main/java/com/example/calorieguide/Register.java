package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private TextView transferToLogin;
    private TextInputEditText inputEmail, inputPassword;
    private Button btnRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    String email, password;
    FirebaseUser user;
    FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onStart() {
        super.onStart();
        // Opens MainActivity if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        transferToLogin = findViewById(R.id.alreadyHaveAnAccount);
        inputEmail = findViewById(R.id.age_edit_text);
        inputPassword = findViewById(R.id.height_edit_text);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        // Transfer to Login View Page Button
        transferToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Register Button Clicked -> Creates User
        btnRegister.setOnClickListener(v -> {
            email = String.valueOf(inputEmail.getText());
            password = String.valueOf(inputPassword.getText());

            if ((TextUtils.isEmpty(email)) && (TextUtils.isEmpty(password))){
                Toast.makeText(Register.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)){
                Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                register();
            }
            // Creates User with Email and Password, throws to get additional info

        });
    }

    private void register() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        addUserToDB();
                        Toast.makeText(Register.this, "Your account has been created.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), GetAdditionalInfo.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void addUserToDB() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid(); // Get the UID from Firebase Authentication
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    // User does not exist in Firestore, create and save their information
                    String email = user.getEmail();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);
                    userMap.put("email", email);

                    userRef.set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                // UID and Email are saved in Firestore
                            })
                            .addOnFailureListener(e -> {
                                // Handle the error if saving the UID in Firestore fails
                            });
                }
            } else {
                // Handle the error if reading the document from Firestore fails
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }
}