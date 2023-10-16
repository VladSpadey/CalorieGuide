package com.example.calorieguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.calorieguide.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // WEIGHT PAGE

    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    DashboardFragment dashboardFragment = new DashboardFragment();
    ActivityMainBinding binding;

    String uIDDB, email;
    Long bmr = 0L;
    Boolean variablesNotFetched = true;
    Boolean BMRchecked = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nav
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new DashboardFragment());
        binding.navigation.setOnItemSelectedListener(item ->{
            if (item.getItemId() == R.id.logPage) {
                //replaceFragment(new LearnFragment());
            } else if (item.getItemId() == R.id.dashboardPage) {
                replaceFragment(new DashboardFragment());
            } else if (item.getItemId() == R.id.weightPage) {
                replaceFragment(new WeightFragment());
            } else if (item.getItemId() == R.id.settingsPage){
                replaceFragment(new SettingsFragment());
            }
            return true;
        });

        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addUserToDB();

        // If no active user, send them to Login view
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            if(BMRchecked)
                checkUserBMR();
            if(variablesNotFetched)
                getValuesFromDB();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void checkUserBMR() {
        BMRchecked = true;
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
                                /*fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, dashboardFragment)
                                        .addToBackStack(null) // Optional, for back navigation
                                        .commit(); */
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

    private void getValuesFromDB() {
        // Gets values of the user from DB
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
                                variablesNotFetched = false;
                            }
                        } else {
                            Log.e("Firestore", "Error: " + task.getException().getMessage(), task.getException());
                        }
                    }
                });
    }
}
