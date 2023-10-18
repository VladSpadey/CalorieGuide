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
import java.util.Objects;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Nav
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new LogFragment());
        binding.navigation.setOnItemSelectedListener(item ->{
            if (item.getItemId() == R.id.logPage) {
                replaceFragment(new LogFragment());
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

        // If no active user, send them to Login view
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
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
                            Log.e("Firestore", "Error: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                        }
                    }
                });
    }
}
