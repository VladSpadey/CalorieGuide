package com.example.calorieguide.Utils;
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

import java.text.ParseException;
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
        double kcal = food.getEnergyKcal() * quantity;
        int kcalFormatted = (int) Math.round(kcal);
        Double fat = food.getFat() * quantity;
        int fatFormatted = (int) Math.round(fat);
        Double fiber = food.getFiber() * quantity;
        int fiberFormatted = (int) Math.round(fiber);
        Double carbo = food.getCarbohydrates() * quantity;
        int carboFormatted = (int) Math.round(carbo);
        Double protein = food.getCarbohydrates() * quantity;
        int proteinFormatted = (int) Math.round(protein);

        Map<String, Object> foodItemData = new HashMap<>();
        foodItemData.put("label", label);
        foodItemData.put("quantity", quantity);
        foodItemData.put("kcal", kcalFormatted);
        foodItemData.put("fat", fatFormatted);
        foodItemData.put("fiber", fiberFormatted);
        foodItemData.put("carbo", carboFormatted);
        foodItemData.put("protein", proteinFormatted);
        foodItemData.put("food", food);

        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("intake").document(currentDate).collection("food");
        DocumentReference documentRef = collectionRef.document(label);
        documentRef.set(foodItemData)
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> {
                });
    }
    public interface IntakeCallback {
        void onIntakeReceived(List<Map<String, Object>> intake);
    }
    public static void getIntake(IntakeCallback callback) {
        List<Map<String, Object>> intake = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String currentDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
        CollectionReference collectionRef = db.collection("users").document(user.getUid()).collection("intake").document(currentDate).collection("food");
        collectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> intakeItem = new HashMap<>();
                        String label = (String) document.get("label");
                        Long kcal = (Long) document.get("kcal");
                        Long carbo = (Long) document.get("carbo");
                        Long protein = (Long) document.get("protein");
                        Long fiber = (Long) document.get("fiber");
                        Long fat = (Long) document.get("fat");
                        Double quantity = (Double) document.get("quantity");
                        Map<String, Object> food = (Map<String, Object>) document.get("food");

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
                    callback.onIntakeReceived(intake);
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }
    public static void getWeightChartValues(OnDataFetchListener listener) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        CollectionReference weightCollectionRef = db.collection("users").document(user.getUid()).collection("weights");
        List<DataEntry> chartData = new ArrayList<>();

        weightCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String dateString = document.getString("date");
                        // Convert string to Date
                        Date date = convertStringToDate(dateString);
                        double weight = document.getDouble("weight");
                        chartData.add(new ValueDataEntry(dateString, weight));
                    }
                    listener.onDataFetchSuccess(chartData);
                })
                .addOnFailureListener(e -> {
                    listener.onDataFetchFailure(e.getMessage());
                });
    }
    public interface OnDataFetchListener {
        void onDataFetchSuccess(List<DataEntry> data);
        void onDataFetchFailure(String errorMessage);
    }
    private static Date convertStringToDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            // Handle the parse exception, e.g., log the error
            e.printStackTrace();
            return null;
        }
    }
}
