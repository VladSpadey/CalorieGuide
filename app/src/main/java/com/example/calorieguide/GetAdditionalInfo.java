package com.example.calorieguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GetAdditionalInfo extends AppCompatActivity {
    Button btn_next;
    RadioGroup sexRadioGroup;
    RadioButton sex;
    EditText age, height, weight;

    @Override
    // This page is going to be open after users first log in, this View asks the user to provide additional info to determine their calorie intake
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_additional_info);


        age = findViewById(R.id.age_edit_text);
        height = findViewById(R.id.height_edit_text);
        weight = findViewById(R.id.weight_edit_text);

        addBtnListener();

    }

    private void addBtnListener() {
        btn_next = findViewById(R.id.bth_getInfoNext);
        btn_next.setOnClickListener(v -> {

            // Strings for input validation
            String weightString = weight.getText().toString();
            String heightString = height.getText().toString();
            String ageString = age.getText().toString();
            String sexString = addListenerOnSexRadio();

            // Validate inputs, then calculate BMR
            if (weightString.isEmpty() || heightString.isEmpty() || ageString.isEmpty() || sexString.isEmpty()){ // Bug: Crushes when no sex selected
                Toast.makeText(this, "Please fill in all of the fields", Toast.LENGTH_SHORT).show();
            } else {
                double BMR = 0; // Caloric Demand

                double weightValue = Double.parseDouble(weightString);
                double heightValue = Double.parseDouble(heightString);
                double ageValue = Double.parseDouble(ageString);

                if (sexString.equals("Female")){
                    double weightMultiply = 9.247 * weightValue;
                    double heightMultiply = 3.098 * heightValue;
                    double ageMultiply = 4.330 * ageValue;
                    BMR = 447.593 + (9.247 * weightValue) + (3.098 * heightValue) - (4.330 * ageValue);
                } else if (sexString.equals("Male")) {
                    BMR = 88.362 + (13.397 * weightValue) + (4.799 * heightValue) - (5.677 * ageValue);
                }


                Intent intent = new Intent(getApplicationContext(), BMRCalculation.class);
                intent.putExtra("BMR", BMR);
                startActivity(intent);
                finish();
            }

        });
    }

    private String  addListenerOnSexRadio() {
        // Gets selected Radio Button
        sexRadioGroup = findViewById(R.id.ActivitySexGroup);
        int selectedRadioID = sexRadioGroup.getCheckedRadioButtonId();
        sex = findViewById(selectedRadioID);
        if  (sex.getText().toString().equals("Male") || sex.getText().toString().equals("Female")){
            return sex.getText().toString();
        } else {
            return "";
        }
    }
}