package com.example.calorieguide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.charts.LinearGauge;
import com.anychart.enums.Anchor;
import com.anychart.enums.Layout;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Orientation;
import com.anychart.enums.Position;
import com.anychart.scales.OrdinalColor;

import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
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
    String formattedBMI;
    private boolean isChartLoading = false;
    AnyChartView anyChartView;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel loading if the fragment is destroyed while the chart is loading
        if (isChartLoading) {
            isChartLoading = false;
            anyChartView = null;
        }
    }

    private void setupBMI(View view) {
        double bmi = calculateBMI();
        TextView bmiText = view.findViewById(R.id.dashboard_txtBMI);
        TextView bmiTextCategory = view.findViewById(R.id.dashboard_txtBMICategory);

        formattedBMI = df.format(bmi);
        bmiText.setText("Your Body Mass Index (BMI) is: " + formattedBMI);

        String category = getCategory(bmi);
        bmiTextCategory.setText("Category: " + category);

        getToHealthyWeight(view, category, bmi);
        if (!isChartLoading) {
            // Start loading chart data
            isChartLoading = true;
            setupBMIChart(view);
        }

    }

    private void setupBMIChart(View view) {
        if (isChartLoading){
            anyChartView = view.findViewById(R.id.bmi_chart_view);
            anyChartView.setProgressBar(view.findViewById(R.id.bmi_progress_bar));
            LinearGauge linearGauge = AnyChart.linear();

            double marker = Double.parseDouble(formattedBMI);

            linearGauge.data(new SingleValueDataSet(new Double[] { marker }));

            linearGauge.layout(Layout.HORIZONTAL);

            OrdinalColor scaleBarColorScale = OrdinalColor.instantiate();
            scaleBarColorScale.ranges(new String[]{
                    "{ from: 0, to: 17, color: ['red 0.5'] }",
                    "{ from: 17, to: 18.5, color: ['yellow 0.75'] }",
                    "{ from: 18.5, to: 25, color: ['green 0.75'] }",
                    "{ from: 25, to: 30, color: ['yellow 0.75'] }",
                    "{ from: 30, to: 35, color: ['red 0.75'] }",
                    "{ from: 35, to: 40, color: ['red 0.5'] }"
            });

            linearGauge.scaleBar(0)
                    .width("25%")
                    .colorScale(scaleBarColorScale);

            linearGauge.marker(0)
                    .type(MarkerType.TRIANGLE_DOWN)
                    .color("white")
                    .zIndex(15);

            linearGauge.scale()
                    .minimum(0)
                    .maximum(40);

            linearGauge.axis(0)
                    .minorTicks(false)
                    .width("1%");
            linearGauge.axis(0)
                    .offset("-1.5%")
                    .orientation(Orientation.TOP)
                    .labels("top");

            linearGauge.padding(0, 15, 0, 15);
            linearGauge.background().enabled(true);
            linearGauge.background().fill("#111111");

            anyChartView.setChart(linearGauge);
        }
        }



    private void getToHealthyWeight(View view, String category, double bmi) {
        TextView txtGetToHealthy = view.findViewById(R.id.dashboard_txtBMICategory_health);
        boolean aboveHealthy = false;
        boolean belowHealthy = false;
        boolean healthy = false;

        if (Objects.equals(category, "Underweight")){
            belowHealthy = true;
        } else if(Objects.equals(category, "Overweight") || Objects.equals(category, "Obese")){
            aboveHealthy = true;
        } else {
            healthy = true;
        }

        double heightinM = height  / 100;

        if(healthy){
            txtGetToHealthy.setVisibility(View.GONE);
        } else if(aboveHealthy){
            double desiredBMI = 25;
            double idealWeight = desiredBMI * (heightinM * heightinM);
            double weightToLose = weight - idealWeight;
            String txtHealthy = "Lose " + df.format(weightToLose) + "kg to reach Normal BMI level";
            txtGetToHealthy.setText(txtHealthy);
            txtGetToHealthy.setVisibility(View.VISIBLE);
        } else if (belowHealthy){
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