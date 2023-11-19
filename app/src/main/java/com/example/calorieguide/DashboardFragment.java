package com.example.calorieguide;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.LinearGauge;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.Layout;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Orientation;
import com.anychart.enums.SelectionMode;
import com.anychart.scales.OrdinalColor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DashboardFragment extends Fragment {
    TextView userInfo;
    FirebaseAuth auth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    DecimalFormat df = new DecimalFormat("0.0");
    // UserData
    MainActivity mainActivity;
    String uID, email, sex;
    Long bmr =0L;
    double bmi = 0;

    double weight, height, activityLevel,age;
    String formattedBMI;
    private boolean isBMIChartLoading = false;
    private boolean isPieChartLoading = false;
    private boolean isDetailsPieChartLoading = false;
    AnyChartView bmi_anyChartView;
    AnyChartView intake_anyChartView;
    AnyChartView food_details_anyChartView;
    Long totalKcal = 0L;
    Pie pie;
    Pie detailsPie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        auth = FirebaseAuth.getInstance();

        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        ProgressBar loadingBar = view.findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.VISIBLE);
        if (mainActivity.userData == null || mainActivity.userData.isEmpty()) {
            // User data is not available yet, schedule a delayed callback
            new Handler().postDelayed(() -> {
                user = mainActivity.user;
                uID = (String) mainActivity.userData.get("uid");
                email = (String) mainActivity.userData.get("email");
                bmr = (Long) mainActivity.userData.get("activityBmr");
                age = (double) mainActivity.userData.get("age");
                weight = (double) mainActivity.userData.get("latestWeight");
                height = (double) mainActivity.userData.get("height");
                activityLevel = mainActivity.activityLevel;
                sex = mainActivity.sex;

                user = mainActivity.user;
                assert user != null;

                // Set Up BMI
                setupBMI(view);
                // Set Up Intake Display
                if (!isPieChartLoading)
                    isPieChartLoading = true;
                setupIntakeChart(view);

                if (!isDetailsPieChartLoading)
                    isDetailsPieChartLoading = true;
                setupFoodDetailsChart(view);

                loadingBar.setVisibility(View.GONE);
            }, 2000);
        } else {
            user = mainActivity.user;
            uID = (String) mainActivity.userData.get("uid");
            email = (String) mainActivity.userData.get("email");
            bmr = (Long) mainActivity.userData.get("activityBmr");
            age = (double) mainActivity.userData.get("age");
            weight = (double) mainActivity.userData.get("latestWeight");
            height = (double) mainActivity.userData.get("height");
            activityLevel = mainActivity.activityLevel;
            sex = mainActivity.sex;

            user = mainActivity.user;
            assert user != null;
            // Set Up BMI
            setupBMI(view);
            // Set Up Intake Display
            if (!isPieChartLoading)
                isPieChartLoading = true;
            setupIntakeChart(view);

            if (!isDetailsPieChartLoading)
                isDetailsPieChartLoading = true;
            setupFoodDetailsChart(view);

            loadingBar.setVisibility(View.GONE);
        }
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isBMIChartLoading) {
            isBMIChartLoading = false;
            bmi_anyChartView = null;
            intake_anyChartView = null;
        }
        if (isPieChartLoading) {
            isPieChartLoading = false;
            intake_anyChartView = null;
            pie = null;
        }
        if (isDetailsPieChartLoading) {
            isDetailsPieChartLoading = false;
            food_details_anyChartView = null;
            detailsPie = null;
        }
    }
    // Food Intake Functions
    public void setupIntakeChart(View view) {
        boolean overTheGoal = false;
        if(isPieChartLoading) {
            if (intake_anyChartView != null)
                intake_anyChartView.invalidate();
            if (isPieChartLoading) {
                intake_anyChartView = view.findViewById(R.id.intake_pie_view);
                intake_anyChartView.setProgressBar(view.findViewById(R.id.intake_progress_bar));
                APIlib.getInstance().setActiveAnyChartView(intake_anyChartView);
                List<DataEntry> data = new ArrayList<>();
                List<Map<String, Object>> intake = mainActivity.intakeReceived;
                    for (Map<String, Object> item : intake) {
                        Long kcal = (Long) item.get("kcal");
                        totalKcal += kcal;
                    }
                TextView intake_desc = view.findViewById(R.id.txt_eaten);
                long available = bmr - totalKcal;
                if(available < 0){
                    overTheGoal = true;
                    available *= -1;
                }
                    if(intake_anyChartView != null){
                        pie = AnyChart.pie();
                        pie.interactivity().selectionMode(SelectionMode.NONE);
                        pie.startAngle(0);
                        if(overTheGoal){
                            pie.palette(new String[]{"#fb8500 0.8", "#780000"});
                            data.add(new ValueDataEntry("Consumed", totalKcal));
                            data.add(new ValueDataEntry("Over", available));
                            intake_desc.setText(Html.fromHtml(
                                    "<font color='#adb5bd'>Consumed: </font><b>" + totalKcal + "</b> cal<br>" +
                                            "<font color='#adb5bd'>Over by: </font><b>" + available + "</b> cal<br>" +
                                            "<font color='#adb5bd'>Goal: </font><b>" + bmr + "</b> cal"
                            ));
                        } else {
                            pie.palette(new String[]{"#fb8500 0.85", "#495057 0.85"});
                            data.add(new ValueDataEntry("Consumed", totalKcal));
                            data.add(new ValueDataEntry("Left", available));
                            intake_desc.setText(Html.fromHtml(
                                    "<font color='#adb5bd'>Consumed: </font><b>" + totalKcal + "</b> cal<br>" +
                                            "<font color='#adb5bd'>Left: </font><b>" + available + "</b> cal<br>" +
                                            "<font color='#adb5bd''>Goal: </font><b>" + bmr + "</b> cal"
                            ));
                        }
                        pie.stroke("#111111");
                        pie.normal().outline().enabled(false);
                        pie.data(data);
                        pie.labels().enabled(false);

                        pie.background().enabled(true);
                        pie.background().fill("#111111");
                        pie.padding(10, 0, 25, 0);
                        pie.tooltip().enabled(false);

                        if (intake_anyChartView != null && isPieChartLoading) {
                            intake_anyChartView.setChart(pie);
                        }
                    }
            }
        }
    }
    public void setupFoodDetailsChart(View view){
        if(isDetailsPieChartLoading) {
            if (food_details_anyChartView != null)
                food_details_anyChartView.invalidate();
            if (isDetailsPieChartLoading) {
                food_details_anyChartView = view.findViewById(R.id.details_pie_view);
                food_details_anyChartView.setProgressBar(view.findViewById(R.id.details_progress_bar));
                APIlib.getInstance().setActiveAnyChartView(food_details_anyChartView);

                Long totalProtein = 0L;
                Long totalCarbs = 0L;
                Long totalFats = 0L;
                Long totalFiber = 0L;
                List<DataEntry> data = new ArrayList<>();
                List<Map<String, Object>> intake = mainActivity.intakeReceived;
                for (Map<String, Object> item : intake) {
                    Long protein = (Long) item.get("protein");
                    Long carbs = (Long) item.get("carbo");
                    Long fat = (Long) item.get("fat");
                    Long fiber = (Long) item.get("fiber");
                    totalProtein += protein;
                    totalCarbs += carbs;
                    totalFats += fat;
                    totalFiber += fiber;
                }
                //TextView intake_desc = view.findViewById(R.id.txt_eaten);
                if(food_details_anyChartView != null){
                    detailsPie = AnyChart.pie();
                    detailsPie.interactivity().selectionMode(SelectionMode.NONE);
                    detailsPie.startAngle(0);
                    detailsPie.palette(new String[]{"#fb8500", "#023047", "#ffb703", "#219ebc"});
                    data.add(new ValueDataEntry("Protein", totalProtein));
                    data.add(new ValueDataEntry("Carbs", totalCarbs));
                    data.add(new ValueDataEntry("Fats", totalFats));
                    data.add(new ValueDataEntry("Fiber", totalFiber));
                    detailsPie.stroke("#111111");
                    detailsPie.normal().outline().enabled(false);
                    detailsPie.data(data);
                    detailsPie.labels().enabled(false);
                    detailsPie.background().enabled(true);
                    detailsPie.background().fill("#111111");
                    detailsPie.padding(10, 0, 20, 0);
                    detailsPie.tooltip().enabled(false);
                    detailsPie.legend()
                            .itemsLayout("vertical")
                            .positionMode("outside")
                            .padding(10, 40, 13, 0)
                            .align("middle")
                            .align(Align.BOTTOM)
                            .itemsSpacing(5);
                    detailsPie.legend().useHtml(true);
                    String itemsFormat = "function() {" +
                            "var percent = this.getStat('percentValue');" +
                            "percent = percent.toFixed(0);" +
                            "return '<span style=\"color: #adb5bd; font-weight: bold;\">' + this.x + '</span>' + ': <span style=\"color: white; font-weight: bold; font-size: 14;\">' + percent + '%</span>'; }";

                    detailsPie.legend().itemsFormat(itemsFormat);

                    if (food_details_anyChartView != null && isDetailsPieChartLoading) {
                        food_details_anyChartView.setChart(detailsPie);
                    }
                }
            }
        }
    }

    // BMI Functions
    private void setupBMI(View view) {
        double bmi = calculateBMI();
        TextView bmiText = view.findViewById(R.id.dashboard_txtBMI);
        TextView bmiTextCategory = view.findViewById(R.id.dashboard_txtBMICategory);

        formattedBMI = df.format(bmi);
        bmiText.setText("Your Body Mass Index (BMI) is: " + formattedBMI);

        String category = getCategory(bmi);
        bmiTextCategory.setText("Category: " + category);

        getToHealthyWeight(view, category, bmi);
        if (!isBMIChartLoading) {
            // Start loading chart data
            isBMIChartLoading = true;
            setupBMIChart(view);
        }

    }
    private void setupBMIChart(View view) {
        if (isBMIChartLoading){
            bmi_anyChartView = view.findViewById(R.id.bmi_chart_view);
            bmi_anyChartView.setProgressBar(view.findViewById(R.id.bmi_progress_bar));
            APIlib.getInstance().setActiveAnyChartView(bmi_anyChartView);
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
                    .zIndex(1)
                    .width("15")
                    .offset(-3);


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

            bmi_anyChartView.setChart(linearGauge);
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
        bmi = weight / HxH;
        return bmi;
    }
}