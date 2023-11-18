package com.example.calorieguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.calorieguide.Utils.FoodAdapter;
import com.example.calorieguide.Utils.apiRequest;
import com.google.gson.Gson;
import com.example.calorieguide.Utils.foodModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
    View view;
    private static final int LOADER_ID = 1;
    private String query;
    Gson gson;
    EditText foodInput;
    List<foodModel> foodList;
    View overlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log, container, false);
        query = "";
        gson = new Gson();

        overlay = view.findViewById(R.id.overlay);

        inputListener();

        if(!query.isEmpty())
                LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this).forceLoad();
        return view;
    }

    private void inputListener() {
        foodInput = view.findViewById(R.id.food_input);
        Handler handler = new Handler();
        final Runnable[] runnable = {null};
        foodInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable[0]);
                runnable[0] = new Runnable() {
                    @Override
                    public void run() {
                        String text = s.toString();
                        query = text;
                        if(!query.isEmpty()){
                            LoaderManager.getInstance(LogFragment.this).restartLoader(LOADER_ID, null, LogFragment.this).forceLoad();
                        }

                    }
                };
                handler.postDelayed(runnable[0], 500);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        showLoadingBar();
        return new apiRequest(getContext(), query);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                foodList = new ArrayList<>();
                JSONArray hintsArray = jsonObject.getJSONArray("hints");

                for (int i = 0; i < hintsArray.length(); i++) {
                    JSONObject hintObject = hintsArray.getJSONObject(i);
                    JSONObject foodObject = hintObject.getJSONObject("food");

                    String label = foodObject.getString("label");



                    // Extract nutrients
                    JSONObject nutrientsObject = foodObject.getJSONObject("nutrients");
                    double energyKcal = 0, protein = 0, fat = 0, carbohydrates = 0, fiber = 0;
                    if (nutrientsObject.has("ENERC_KCAL")){
                        energyKcal = nutrientsObject.getDouble("ENERC_KCAL");
                    }
                    if (nutrientsObject.has("PROCNT")){
                        protein = nutrientsObject.getDouble("PROCNT");
                    }
                    if (nutrientsObject.has("FAT")){
                        fat = nutrientsObject.getDouble("FAT");
                    }
                    if (nutrientsObject.has("CHOCDF")){
                        carbohydrates = nutrientsObject.getDouble("CHOCDF");
                    }
                    if (nutrientsObject.has("FIBTG")){
                        fiber = nutrientsObject.getDouble("FIBTG");
                    }


                    JSONArray measuresArray = hintObject.getJSONArray("measures");

                    if (measuresArray.length() > 0) {
                        List<String> weightLabels = new ArrayList<>();
                        List<Integer> weights = new ArrayList<>();
                        for (int j = 0; j < measuresArray.length(); j++) {
                            JSONObject measureObject = measuresArray.getJSONObject(j);
                            if(measureObject.has("label")) {
                                String weightLabel = measureObject.getString("label");
                                int weight = (int) measureObject.getDouble("weight");

                                // Exclude specific weight labels
                                if (!weightLabel.equals("Gram") && !weightLabel.equals("Ounce") && !weightLabel.equals("Pound") && !weightLabel.equals("Kilogram")
                                        && !weightLabel.equals("Cubic inch") && !weightLabel.isEmpty()) {
                                    weightLabels.add(weightLabel);
                                    weights.add(weight);
                                }
                            }
                        }

                        if (!weightLabels.isEmpty()) {
                            foodModel item = new foodModel(label, weightLabels, weights, energyKcal, protein, fat, carbohydrates, fiber);
                            foodList.add(item);
                        }
                    }
                }
                hideLoadingBar();
                RecyclerView recyclerView = view.findViewById(R.id.food_list_view);
                MainActivity mainActivity = (MainActivity) getActivity();
                assert mainActivity != null;
                FoodAdapter adapter = new FoodAdapter(foodList, requireContext(), getLayoutInflater(), overlay, mainActivity);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } catch (JSONException e) {
                Log.e("GSON Error", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            Log.e("API Error", "API request failed");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        hideLoadingBar();
    }

    private void showLoadingBar() {
        View loadingContainer = view.findViewById(R.id.loadingBar);
        loadingContainer.setVisibility(View.VISIBLE);
    }

    private void hideLoadingBar() {
        View loadingContainer = view.findViewById(R.id.loadingBar);
        loadingContainer.setVisibility(View.GONE);
    }
}