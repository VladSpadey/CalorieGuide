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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class WeightFragment extends Fragment {
    FirebaseUser user;
    String uID, email;
    Button addData;
    View overlay;
    AnyChartView anyChartView;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    static FirebaseAuth auth;
    List<DataEntry> seriesData;
    private boolean isChartLoading = false;
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

        if (!isChartLoading) {
            // Start loading your chart data
            isChartLoading = true;
            getDBValues(view); // Replace with your chart setup logic
        }

        // Chart
       // getDBValues(view);

        // Add Weight
        addData = view.findViewById(R.id.btn_addWeight);
        addDataListener(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel loading if the fragment is destroyed while the chart is loading
        if (isChartLoading) {
            isChartLoading = false;
            anyChartView = null;
            // Cancel loading logic (e.g., cancel network requests)
        }
    }

    private void getDBValues(View view) {
        CollectionReference weightCollectionRef = db.collection("users").document(user.getUid()).collection("weights");
        weightCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            seriesData = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Extract data from each document
                String date = document.getString("date");  // Replace "date" with the field name in your Firestore document
                double weight = document.getDouble("weight"); // Replace "weight" with the field name in your Firestore document
                // Create a data entry and add it to the series data
                seriesData.add(new ValueDataEntry(date, weight));
            }

            // After fetching all the data, update the chart with the new data
            setupChart(view, seriesData);
        });
    }

    private void setupChart(View view, List<DataEntry> data) {
        if (anyChartView != null)
            anyChartView.invalidate();
        if(isChartLoading){
            anyChartView = view.findViewById(R.id.weight_chart_view);
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
            cartesian.yAxis(0).labels().padding(0d, 0d, 0d, 0d);
            cartesian.xAxis(0).labels().padding(5d, 5d, 20d, 5d);

            Set set = Set.instantiate();
            set.data(data);
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
    }


    private void addDataListener(View mainView) {
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
                getDBValues(mainView);
                alertDialog.dismiss();
            });

            alertDialog.setOnDismissListener(v2 -> overlay.setVisibility(View.GONE));
            alertDialog.show();
        });
    }
}