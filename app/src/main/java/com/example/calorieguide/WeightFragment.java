package com.example.calorieguide;

import static java.lang.Math.round;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
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
    View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DataEntry> chartData;
    private boolean isChartLoading = false;
    Set dataSet;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weight, container, false);

        // User Data
        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
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

        if (!isChartLoading) {
            isChartLoading = true;
        }
        // Chart
        chart = mainActivity.weightChartValues;
        if (chart.isEmpty()){
            chartView = view.findViewById(R.id.weight_chart_view);
            chartView.setVisibility(View.GONE);
        } else {
            setupChart(chart);
            chartView.setVisibility(View.VISIBLE);
        }

        // Add Weight
        addData = view.findViewById(R.id.btn_addWeight);
        addDataListener();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel loading if the fragment is destroyed while the chart is loading
        if (isChartLoading) {
            isChartLoading = false;
            // Cancel loading logic (e.g., cancel network requests)
        }
    }

    private void setupChart(List<DataEntry> data) {
        if(isChartLoading){
            chartView = view.findViewById(R.id.weight_chart_view);
            chartView.setProgressBar(view.findViewById(R.id.weight_progress_bar));

            if(chartView != null){
                Cartesian cartesian = AnyChart.line();
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

                Line chart = cartesian.line(chartMapping);
                chart.name("Weight");
                chart.legendItem().enabled(false);
                chart.hovered().markers().enabled(true);
                chart.markers().enabled(true);
                chart.stroke("#f77f00");


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
                    updateBMR();
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
    }

}