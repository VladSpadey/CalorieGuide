package com.example.calorieguide.Utils;

import static java.lang.Math.round;

import android.util.Log;

import androidx.annotation.NonNull;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    public static void addIntake(foodModel food, double quantity) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Map<String, Object> intakeData = new HashMap<>();

        String currentDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());

        String label = food.getLabel();
        Double kcal = food.getEnergyKcal() * quantity;
        Double fat = food.getFat() * quantity;
        Double fiber = food.getFiber() * quantity;
        Double carbo = food.getCarbohydrates() * quantity;
        Double protein = food.getCarbohydrates() * quantity;

        Map<String, Object> foodItemData = new HashMap<>();
        foodItemData.put("label", label);
        foodItemData.put("quantity", quantity);
        foodItemData.put("kcal", kcal);
        foodItemData.put("fat", fat);
        foodItemData.put("fiber", fiber);
        foodItemData.put("carbo", carbo);
        foodItemData.put("protein", protein);
        foodItemData.put("food", food);

        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("intake").document(currentDate).collection("food");
        DocumentReference documentRef = collectionRef.document(label);
        documentRef.set(foodItemData)
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> {
                });
    }
   /* public static Map<String, Double> getIntake(String todaysDate){
        Map<String, Double> intake = new HashMap<String, Double>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String label;
        Double kcal;


        String currentDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("intake").document(currentDate).collection("food");
        collectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        label = (String) document.get("label");
                        kcal = (Double) document.get("kcal");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });

        Log.d(TAG, "getIntake: " + label + " "  + kcal);
        return intake;
    } */
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
