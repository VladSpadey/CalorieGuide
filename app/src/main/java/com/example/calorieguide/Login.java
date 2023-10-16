package com.example.calorieguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextView transferToRegister;
    private TextInputEditText inputEmail, inputPassword;
    private Button btnLogin;
    String email, password;

    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
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
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        transferToRegister = findViewById(R.id.TransferRegister);
        inputEmail = findViewById(R.id.age_edit_text);
        inputPassword = findViewById(R.id.height_edit_text);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);

        // Transfer to Login View Page Button
        transferToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {

            email = String.valueOf(inputEmail.getText());
            password = String.valueOf(inputPassword.getText());

            if ((TextUtils.isEmpty(email)) && (TextUtils.isEmpty(password))){
                Toast.makeText(Login.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)){
                Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}