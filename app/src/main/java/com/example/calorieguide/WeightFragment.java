package com.example.calorieguide;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class WeightFragment extends Fragment {
    FirebaseUser user;
    String uID, email;
    Button addData;
    View overlay;
    AnyChartView chartView;
    View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DataEntry> chartData;
    private boolean isChartLoading = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weight, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        user = mainActivity.user;
        uID = mainActivity.uIDDB;
        email = mainActivity.email;
        overlay = view.findViewById(R.id.overlay);

        if (!isChartLoading) {
            // Start loading your chart data
            isChartLoading = true;
            //getDBValues(view);
        }

        // Chart
        setupChart(mainActivity.weightChartValues);


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

                Set set = Set.instantiate();
                set.data(data);
                Mapping chartMapping = set.mapAs("{ x: 'x', value: 'value' }");

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
                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid double
                    Toast.makeText(getContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            });

            alertDialog.setOnDismissListener(v2 -> overlay.setVisibility(View.GONE));
            alertDialog.show();
        });
    }
}