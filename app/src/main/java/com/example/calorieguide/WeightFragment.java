package com.example.calorieguide;

import static java.lang.Math.round;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import com.google.firebase.auth.FirebaseUser;
import com.example.calorieguide.Utils.dbUtil;
import com.google.firebase.firestore.FirebaseFirestore;


public class WeightFragment extends Fragment {
    FirebaseUser user;

    MainActivity mainActivity;
    String uID, email, sex;
    Long bmr =0L;
    double weight, height, activityLevel,age;
    Button addData;
    View overlay;
    AnyChartView chartView;
    List <DataEntry> chart;
    Cartesian cartesian;
    Set dataSet;
    View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DataEntry> chartData;
    private boolean isChartLoading = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weight, container, false);

        ProgressBar loadingBar = view.findViewById(R.id.loadingBar_weight);
        loadingBar.setVisibility(View.VISIBLE);

        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        if (mainActivity.userData == null || mainActivity.userData.isEmpty()) {
            // User data is not available yet, schedule a delayed callback
            new Handler().postDelayed(() -> {
                runWeightFragment();
                addDataListener();
                if (chart == null || chart.isEmpty()){
                    chartView = view.findViewById(R.id.weight_chart_view);
                    chartView.setVisibility(View.GONE);
                } else {
                    setupChart(chart);
                    chartView.setVisibility(View.VISIBLE);
                }
                loadingBar.setVisibility(View.GONE);
            }, 2000);
        } else {
            runWeightFragment();
            addDataListener();
            if (chart == null || chart.isEmpty()){
                chartView = view.findViewById(R.id.weight_chart_view);
                chartView.setVisibility(View.GONE);
            } else {
                setupChart(chart);
                chartView.setVisibility(View.VISIBLE);
            }
            loadingBar.setVisibility(View.GONE);
        }
        return view;
    }

    private void runWeightFragment() {
        Log.d("Weight Fragment", "data: " + mainActivity.userData);
        // User Data
        user = mainActivity.user;
        uID = (String) mainActivity.userData.get("uid");
        email = (String) mainActivity.userData.get("email");
        bmr = (Long) mainActivity.userData.get("activityBmr");
        age = (double) mainActivity.userData.get("age");
        weight = (double) mainActivity.userData.get("latestWeight");
        height = (double) mainActivity.userData.get("height");
        activityLevel = mainActivity.activityLevel;
        sex = mainActivity.sex;
        overlay = view.findViewById(R.id.overlay);
        updateBMR();
        if (!isChartLoading) {
            isChartLoading = true;
        }
        // Chart
        chart = mainActivity.weightChartValues;

        // Add Weight
        addData = view.findViewById(R.id.btn_addWeight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel loading if the fragment is destroyed while the chart is loading
        if (isChartLoading) {
            isChartLoading = false;
        }
    }

    private void setupChart(List<DataEntry> data) {
        if(isChartLoading){
            chartView = view.findViewById(R.id.weight_chart_view);
            chartView.setProgressBar(view.findViewById(R.id.weight_progress_bar));

            if(chartView != null){
                cartesian = AnyChart.line();
                cartesian.padding(10d, 20d, 5d, 20d);
                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.background().fill("#111111");

                cartesian.yAxis(0).title("Weight (kg)");
                cartesian.yAxis(0).labels().padding(0d, 0d, 0d, 0d);
                cartesian.xAxis(0).labels().padding(5d, 5d, 20d, 5d);

                Set dataSet = Set.instantiate();
                dataSet.data(data);
                Mapping chartMapping = dataSet.mapAs("{ x: 'x', value: 'value' }");

                Line chartLine = cartesian.line(chartMapping);
                chartLine.name("Weight");
                chartLine.legendItem().enabled(false);
                chartLine.hovered().markers().enabled(true);
                chartLine.markers().enabled(true);
                chartLine.stroke("#f77f00");
                cartesian.autoRedraw(true);
                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(13d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);
                chartView.setChart(cartesian);
            }
        }
    }

    private void addDataListener() {
        addData.setOnClickListener(v -> {
            // Show Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
            View view = getLayoutInflater().inflate(R.layout.dialog_add_weight, null);

            Button updateWeight = view.findViewById(R.id.btn_dialog_AddButton);

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            overlay.setVisibility(View.VISIBLE);
            EditText input = view.findViewById(R.id.input_weight);
            input.requestFocus();


            updateWeight.setOnClickListener(v1 -> {
                double weight;
                try {
                    weight = Double.parseDouble(input.getText().toString());
                    dbUtil.addWeightToDb(weight);
                    dbUtil.addDoubleToDb("latestWeight", weight);
                    mainActivity.userData.put("latestWeight", weight);

                    Map<String, Object> weightData = new HashMap<>();
                    String currentDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
                    weightData.put("weight", weight);
                    weightData.put("date", currentDate);
                    chart.add(new ValueDataEntry(currentDate, weight));
                    updateBMR();
                    isChartLoading = false;
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            });

            alertDialog.setOnDismissListener(v2 -> overlay.setVisibility(View.GONE));
            alertDialog.show();
        });
    }

    private void updateBMR(){
        long updatedBMR = 0;
        double BMR = 0;
        if (sex.equals("Female")){
            BMR = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        } else if (sex.equals("Male")) {
            BMR = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        }
        updatedBMR = round(BMR * activityLevel);
        mainActivity.userData.put("activityBmr", (Long) updatedBMR);
        dbUtil.addIntToDb("activityBmr", (int) updatedBMR);
        setupBMRdesc(updatedBMR);
    }
    // BMR Functions
    private void setupBMRdesc(long updatedBMR) {
        TextView txtBMR = view.findViewById(R.id.dashboard_txtBMR);
        txtBMR.setText("Your approximate BMR: " + updatedBMR);
    }
}