package com.example.calorieguide.Utils;

import static java.lang.Math.round;

import android.nfc.Tag;
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
   public static List<Map<String, Object>> getIntake(){
        List<Map<String, Object>> intake = new ArrayList<Map<String, Object>>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String currentDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("intake").document(currentDate).collection("food");
        collectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> intakeItem = new HashMap<>();
                        String label = (String) document.get("label");
                        Double kcal = (Double) document.get("kcal");
                        Double carbo = (Double) document.get("carbo");
                        Double protein = (Double) document.get("protein");
                        Double fiber = (Double) document.get("fiber");
                        Double fat = (Double) document.get("fat");
                        Double quantity = (Double) document.get("quantity");
                        Map<String, Object> food = (Map<String, Object>) document.get("food");
                        //foodModel food = (foodModel)  document.get("food");
                        intakeItem.put("label", label);
                        intakeItem.put("kcal", kcal);
                        intakeItem.put("carbo", carbo);
                        intakeItem.put("protein", protein);
                        intakeItem.put("fiber", fiber);
                        intakeItem.put("fat", fat);
                        intakeItem.put("quantity", quantity);
                        intakeItem.put("food", food);
                        intake.add(intakeItem);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
       return intake;
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
