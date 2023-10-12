package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GetAdditionalInfo extends AppCompatActivity {

    @Override
    // This page is going to be open after users first log in, this View asks the user to provide additional info to determine their calorie intake
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_additional_info);
    }
}