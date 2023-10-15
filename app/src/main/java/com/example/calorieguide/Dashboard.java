package com.example.calorieguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    String uID, uIDDB, email;
    Long bmr = 0L;
    TextView userInfo;

    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        uID = user.getUid();
        userInfo = findViewById(R.id.user_info);

        // get all values from db and update UI
        getValuesFromDB();
    }

    private void getValuesFromDB() {
        String uid = user.getUid();
        CollectionReference docRef = db.collection("users");

        Task<QuerySnapshot> query = docRef.whereEqualTo("uid", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Access data from the document
                                Map<String, Object> data = document.getData();
                                uIDDB = (String) data.get("uid");
                                email = (String) data.get("email");
                                bmr = (Long) data.get("bmr");
                                //Toast.makeText(Dashboard.this, data.toString(), Toast.LENGTH_LONG).show();

                                updateUI();
                            }
                        } else {
                            Log.e("Firestore", "Error: " + task.getException().getMessage(), task.getException());
                        }
                    }
                });
    }

    private void updateUI() {
        userInfo.setText(String.format("id: %s \n email: %s \n bmr: %s", uIDDB, email, bmr));
        Toast.makeText(Dashboard.this, "email: " + email, Toast.LENGTH_LONG).show();
    }
}