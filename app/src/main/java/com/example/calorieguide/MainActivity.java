package com.example.calorieguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anychart.chart.common.dataentry.DataEntry;
import com.example.calorieguide.Utils.UserData;
import com.example.calorieguide.Utils.dbManager;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.example.calorieguide.Utils.dbUtil;

public class MainActivity extends AppCompatActivity {
    // WEIGHT PAGE

    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    DashboardFragment dashboardFragment = new DashboardFragment();
    ActivityMainBinding binding;
    String uIDDB, email, sex;
    Long bmr = 0L;
    Double activityLevel, age;
    double latestWeight, height;

    List<DataEntry> weightChartValues;
    Boolean variablesFetched = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get Data

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
            dbManager dataManager = new dbManager();
            Map<String, Object>  data = dataManager.getUserData();
            Log.d("dbManager", "2. MainActivity Data: " + data);
            getValuesFromDB();
            updateWeightValues();
        }
    }

    public void updateWeightValues(){
        weightChartValues = dbUtil.getWeightChartValues();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void getValuesFromDB() {
        // Gets values of the user from DB
        String uid = user.getUid();
        CollectionReference docRef = db.collection("users");
        Task<QuerySnapshot> query = docRef.whereEqualTo("uid", uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            uIDDB = (String) data.get("uid");
                            email = (String) data.get("email");
                            bmr = (Long) data.get("activityBmr");
                            Object latestWeightObject = data.get("latestWeight");
                            if (latestWeightObject != null) {
                                latestWeight = ((Number) latestWeightObject).doubleValue();
                            } else {
                                sendToGetInfo(false);
                            }
                            Object heightObject = data.get("height");
                            if (heightObject != null) {
                                height = ((Number) heightObject).doubleValue();
                            } else {
                                sendToGetInfo(true);
                            }
                            Object activityLevelObject = data.get("activityLevelMultiplier");
                            if (activityLevelObject != null) {
                                activityLevel =  ((Number) activityLevelObject).doubleValue();
                            } else {
                                sendToGetInfo(true);
                            }
                            age = (Double) data.get("age");
                            sex = (String) data.get("sex");
                        }
                    } else {
                        Log.e("Firestore", "Error: " + Objects.requireNonNull(task.getException()).getMessage(), task.getException());
                    }
                });
    }

    private void sendToGetInfo(boolean weightExists) {
        Intent intent = new Intent(getApplicationContext(), GetAdditionalInfo.class);
        intent.putExtra("weightExists", weightExists);
        startActivity(intent);
        finish();
    }
}
