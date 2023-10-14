package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    String uID, email;
    Long bmr = 0L;

    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // get all values from db
        getAllValuesFromDB();

        TextView userInfo = findViewById(R.id.user_info);
        userInfo.setText(String.format("id: %s \n email: %s \n bmr: %s", uID, email, bmr));
    }

    private void getAllValuesFromDB() {
        String uid = user.getUid();
        DocumentReference docRef = db.collection("users").document(uid);

        docRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document exists, retrieve values
                            uID = document.getString("uid");
                            bmr = document.getLong("bmr");
                            email = document.getString("email");
                        } else {
                            // Document does not exist for the given UID
                        }
                    } else {
                        // Handle errors
                        Exception e = task.getException();
                        if (e != null) {
                            // Handle the exception
                        }
                    }
                });
    }
}