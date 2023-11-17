package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.anychart.chart.common.dataentry.DataEntry;
import com.example.calorieguide.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    Map<String, Object> userData;
    Double activityLevel, age;
    double latestWeight, height;
    List<DataEntry> weightChartValues;
    List<Map<String, Object>> intakeReceived;





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
            updateValuesFromDB();
            updateWeightValues();
            updateIntakeValues();
        }

    }

    private void updateIntakeValues() {
        dbUtil.getIntake(new dbUtil.IntakeCallback() {
            @Override
            public void onIntakeReceived(List<Map<String, Object>> intake) {
                intakeReceived = intake;
            }
        });
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

    public void updateValuesFromDB() {
        // Gets values of the user from DB
        String uid = user.getUid();
        CollectionReference docRef = db.collection("users");
        Task<QuerySnapshot> query = docRef.whereEqualTo("uid", uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userData = document.getData();
                            uIDDB = (String) userData.get("uid");
                            email = (String) userData.get("email");
                            bmr = (Long) userData.get("activityBmr");

                            Object latestWeightObject = userData.get("latestWeight");
                            if (latestWeightObject != null) {
                                latestWeight = ((Number) latestWeightObject).doubleValue();
                            } else {
                                sendToGetInfo(false);
                            }
                            Object heightObject = userData.get("height");
                            if (heightObject != null) {
                                height = ((Number) heightObject).doubleValue();
                            } else {
                                sendToGetInfo(true);
                            }
                            Object activityLevelObject = userData.get("activityLevelMultiplier");
                            if (activityLevelObject != null) {
                                activityLevel =  ((Number) activityLevelObject).doubleValue();
                            } else {
                                sendToGetInfo(true);
                            }
                            age = (Double) userData.get("age");
                            sex = (String) userData.get("sex");
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
