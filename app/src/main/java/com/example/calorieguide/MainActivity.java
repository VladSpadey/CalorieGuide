package com.example.calorieguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // WEIGHT PAGE

    FirebaseAuth auth;
    Button btn_logout;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    DashboardFragment dashboardFragment = new DashboardFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        btn_logout = findViewById(R.id.btn_logout);
        user = auth.getCurrentUser();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addUserToDB();

        // If no active user, send them to Login view
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Then App runs
            // Check if the user has completed BMR calculation
            checkUserBMR();
        }

        // Logout button pressed
        btn_logout.setOnClickListener(v ->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkUserBMR() {
        String uid = user.getUid();
        CollectionReference docRef = db.collection("users");
        Task<QuerySnapshot> query = docRef.whereEqualTo("uid", uid).whereGreaterThan("bmr", 0).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                Intent intent = new Intent(getApplicationContext(), GetAdditionalInfo.class);
                                startActivity(intent);
                                finish();
                                Log.d("Firestore", "User doesn't have BMR setup: ", task.getException());
                            } else {
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, dashboardFragment)
                                        .addToBackStack(null) // Optional, for back navigation
                                        .commit();
                            }
                            // User Has BMR, send to Dashboard
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addUserToDB() {
        String uid = user.getUid(); // Get the UID from Firebase Authentication
        DocumentReference userRef = db.collection("users").document(uid);

        ((DocumentReference) userRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        // User does not exist in Firestore, create and save their information
                        String email = user.getEmail();
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("uid", uid);
                        userMap.put("email", email);

                        userRef.set(userMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // UID and Email are saved in Firestore
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the error if saving the UID in Firestore fails
                                    }
                                });
                    }
                } else {
                    // Handle the error if reading the document from Firestore fails
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}