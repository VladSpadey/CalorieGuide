package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button btn_logout;
    TextView info;
    FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        btn_logout = findViewById(R.id.btn_logout);
        info = findViewById(R.id.user_info);
        user = auth.getCurrentUser();



        // If no active user, send them to Login view
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            info.setText(user.getUid());
        }

        // Logout button pressed
        btn_logout.setOnClickListener(v ->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }
}