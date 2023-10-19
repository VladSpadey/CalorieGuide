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

import com.anychart.AnyChart;
import com.anychart.AnyChartView;

import java.util.ArrayList;
import java.util.List;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.calorieguide.Utils.dbUtil;
import com.anychart.graphics.vector.Stroke;


public class WeightFragment extends Fragment {
    FirebaseUser user;
    String uID, email;
    Button addData;
    View overlay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        user = mainActivity.user;
        uID = mainActivity.uIDDB;
        email = mainActivity.email;
        overlay = view.findViewById(R.id.overlay);

        // Chart
        setupChart(view);

        // Add Weight
        addData = view.findViewById(R.id.btn_addWeight);
        addDataListener();

        return view;
    }

    private void setupChart(View view) {
        AnyChartView anyChartView = view.findViewById(R.id.weight_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.weight_progress_bar));
        anyChartView.setBackgroundColor("#111111");

        Cartesian cartesian = AnyChart.line();

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.background().fill("#111111");

        cartesian.yAxis(0).title("Weight (kg)");
        cartesian.yAxis(0).labels().padding(-20d, 0d, 0d, -5d);
        cartesian.xAxis(0).labels().padding(5d, 5d, 20d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new ValueDataEntry("Oct 1", 100));
        seriesData.add(new ValueDataEntry("Oct 2", 99.5));
        seriesData.add(new ValueDataEntry("Oct 3", 99.6));
        seriesData.add(new ValueDataEntry("Oct 4", 98));
        seriesData.add(new ValueDataEntry("Oct 5", 97.6));
        seriesData.add(new ValueDataEntry("Oct 6", 98.9));
        seriesData.add(new ValueDataEntry("Oct 9", 100));
        seriesData.add(new ValueDataEntry("Oct 10", 99.9));
        seriesData.add(new ValueDataEntry("Oct 11", 99));
        seriesData.add(new ValueDataEntry("Oct 12", 97.7));
        seriesData.add(new ValueDataEntry("Oct 13", 96));
        seriesData.add(new ValueDataEntry("Oct 17", 95));
        seriesData.add(new ValueDataEntry("Oct 18", 95.5));
        seriesData.add(new ValueDataEntry("Oct 19", 94.4));
        seriesData.add(new ValueDataEntry("Oct 20", 92.3));
        seriesData.add(new ValueDataEntry("Oct 21", 91.9));
        seriesData.add(new ValueDataEntry("Oct 25", 91.8));
        seriesData.add(new ValueDataEntry("Oct 30", 91.1));
        seriesData.add(new ValueDataEntry("Nov 5", 90));
        seriesData.add(new ValueDataEntry("Nov 8", 87.5));
        seriesData.add(new ValueDataEntry("Nov 16", 85));


        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Weight");
        series1.legendItem().enabled(false);
        series1.hovered().markers().enabled(true);
        series1.markers().enabled(true);
        series1.stroke("#f77f00");


        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
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

                double weight = 0 ;
                try {
                    weight = Double.parseDouble(input.getText().toString());
                    dbUtil.addWeightToDb(weight);
                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid double
                    Toast.makeText(getContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "Weight " + weight, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            });

            alertDialog.setOnDismissListener(v2 -> overlay.setVisibility(View.GONE));
            alertDialog.show();
        });
    }

    private void addNewWeightToDB() {

    }
}