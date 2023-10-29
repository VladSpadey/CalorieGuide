package com.example.calorieguide.Utils;

import static java.lang.Math.round;

import android.util.Log;

import androidx.annotation.NonNull;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.example.calorieguide.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class dbUtil {
    static FirebaseAuth auth;
    static FirebaseUser user;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addIntToDb(String field, int value) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid(); // Get the UID from Firebase Authentication
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // bmr is updated in Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error updating bmr: " + e.getMessage());
                    }
                });
    }
    public static void addDoubleToDb(String field, double value) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid(); // Get the UID from Firebase Authentication
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // bmr is updated in Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding double to db: " + e.getMessage());
                    }
                });
    }
    public static void addStringToDb(String field, String value) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        String uid = user.getUid(); // Get the UID from Firebase Authentication
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // bmr is updated in Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding double to db: " + e.getMessage());
                    }
                });
    }
    public static void addWeightToDb(double weight){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Map<String, Object> weightData = new HashMap<>();
        String currentDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
        weightData.put("weight", weight);
        weightData.put("date", currentDate);
        CollectionReference weightCollectionRef = db.collection("users").document(user.getUid()).collection("weights");
        DocumentReference weightDocumentRef = weightCollectionRef.document(currentDate);
        weightDocumentRef.set(weightData)
                .addOnSuccessListener(documentReference -> {
                    // Data added successfully
                })
                .addOnFailureListener(e -> {
                    // Handle the error in case of failure
                });
    }

    public static List<DataEntry> getWeightChartValues() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        CollectionReference weightCollectionRef = db.collection("users").document(user.getUid()).collection("weights");
        List<DataEntry> chartData= new ArrayList<>();
        weightCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String date = document.getString("date");
                        double weight = document.getDouble("weight");
                        chartData.add(new ValueDataEntry(date, weight));
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error here, e.g., log the error or display an error message.
                    // You can access the error message with e.getMessage().
                });
        return chartData;
    }
}
