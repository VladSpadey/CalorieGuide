package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class BMRCalculation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmrcalculation);
        Intent intent = getIntent();
        double bmr = intent.getDoubleExtra("BMR", 0.0); // BMR calculated in the getAdditionalInfo
    }
}