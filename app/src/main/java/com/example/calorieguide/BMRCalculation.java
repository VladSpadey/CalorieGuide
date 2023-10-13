package com.example.calorieguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

public class BMRCalculation extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton activity;
    Button finish;
    private int selectedIndex = 0;
    double bmr, activityBMR;
    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmrcalculation);
        Intent intent = getIntent();
        bmr = intent.getDoubleExtra("BMR", 0.0); // BMR calculated in the getAdditionalInfo


        addBtnListener();
    }

    private void addBtnListener() {
        finish = findViewById(R.id.btn_finish);
        addListenerOnRadio();

        finish.setOnClickListener(v -> {
            selectedIndex += 1;
            double activityMultiplier = 1;

            // updatedBMR calculation
            switch(selectedIndex){
                case 1:
                    activityMultiplier = 1.2;
                    break;
                case 2:
                    activityMultiplier = 1.375;
                    break;
                case 3:
                    activityMultiplier = 1.55;
                    break;
                case 4:
                    activityMultiplier = 1.725;
                    break;
                case 5:
                    activityMultiplier = 1.9;
                    break;
                default:
                    break;
            }
            activityBMR = bmr * activityMultiplier;
            int newBMR = (int) Math.round(activityBMR);

            updateUserBMR(newBMR);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("activityBMR", activityBMR);
            startActivity(intent);
            finish();

        });
    }

    private void updateUserBMR(int newBMR) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid(); // Get the UID from Firebase Authentication
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.update("bmr", newBMR)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Weight is updated in Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error updating bmr: " + e.getMessage());
                    }
                });
    }

    private void  addListenerOnRadio() {
        // Gets selected Radio Button
        radioGroup = findViewById(R.id.ActivityRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedIndex = group.indexOfChild(findViewById(checkedId)); // Index of selected radio button
            }
        });

    }
}