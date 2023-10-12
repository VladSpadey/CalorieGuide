package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private TextView transferToRegister;
    private TextInputEditText inputEmail, inputPassword;
    private Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        transferToRegister = findViewById(R.id.TransferRegister);
        inputEmail = findViewById(R.id.email_edit_text);
        inputPassword = findViewById(R.id.password_edit_text);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);

        // Transfer to Login View Page Button
        transferToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password;
            email = String.valueOf(inputEmail.getText());
            password = String.valueOf(inputPassword.getText());

            if ((TextUtils.isEmpty(email)) && (TextUtils.isEmpty(password))){
                Toast.makeText(Login.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)){
                Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}