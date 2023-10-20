package com.example.calorieguide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DashboardFragment extends Fragment {
    TextView userInfo;
    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DecimalFormat df = new DecimalFormat("0.0");
    String uIDDB, email;
    Long bmr;
    double weight, height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        auth = FirebaseAuth.getInstance();

        // get all values from mainActivity and update UI
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        uIDDB = mainActivity.uIDDB;
        email = mainActivity.email;
        bmr = mainActivity.bmr;
        weight = mainActivity.latestWeight;
        height = mainActivity.height;

        user = mainActivity.user;
        assert user != null;

        // Set Up BMR
        setupBMRdesc(view);

        // Set Up BMI
        setupBMI(view);
        return view;
    }

    private void setupBMI(View view) {
        double bmi = calculateBMI();
        TextView bmiText = view.findViewById(R.id.dashboard_txtBMI);
        TextView bmiTextCategory = view.findViewById(R.id.dashboard_txtBMICategory);


        String formattedBMI = df.format(bmi);

        bmiText.setText("Your Body Mass Weight (BMI) is: " + formattedBMI);

        String category = getCategory(bmi);
        bmiTextCategory.setText("Category: " + category);

        getToHealthyWeight(view, category, bmi);
    }

    private void getToHealthyWeight(View view, String category, double bmi) {
        TextView txtGetToHealthy = view.findViewById(R.id.dashboard_txtBMICategory_health);
        boolean aboveHealthy = false;
        boolean visible = false;
        if (Objects.equals(category, "Underweight")){
            aboveHealthy = false;
        } else {
            aboveHealthy = true;
        }

        double heightinM = height  / 100;

        if(aboveHealthy){
            double desiredBMI = 25;
            double idealWeight = desiredBMI * (heightinM * heightinM);
            double weightToLose = weight - idealWeight;
            String txtHealthy = "Lose " + df.format(weightToLose) + "kg to reach Normal BMI level";
            txtGetToHealthy.setText(txtHealthy);
            txtGetToHealthy.setVisibility(View.VISIBLE);
        } else if (!aboveHealthy){
            double desiredBMI = 18.5;
            double idealWeight = desiredBMI * (heightinM * heightinM);
            double weightToLose = idealWeight - weight;
            String txtHealthy = "Gain " + df.format(weightToLose) + "kg to reach Normal BMI level";
            txtGetToHealthy.setText(txtHealthy);
            txtGetToHealthy.setVisibility(View.VISIBLE);
        }
    }

    private String getCategory(double bmi) {
        String category = "";
        if(bmi < 18.5){
            category = "Underweight";
        } else if(bmi >= 18.5 && bmi < 25){
            category = "Normal Weight";
        } else if(bmi >= 25 && bmi < 30){
            category = "Overweight";
        } else if(bmi >= 30){
            category = "Obese";
        }
        return category;
    }

    private double calculateBMI() {
        double heightInM = height / 100;
        double HxH = heightInM * heightInM;
        return weight / HxH;
    }

    private void setupBMRdesc(View view) {
        TextView txtBMR = view.findViewById(R.id.dashboard_txtBMR);
        txtBMR.setText("Your approximate BMR: " + bmr);
    }
}