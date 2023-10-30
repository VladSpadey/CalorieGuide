package com.example.calorieguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log, container, false);
        query = "Salmon";
        gson = new Gson();

        if(!query.isEmpty())
                LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this).forceLoad();
        return view;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new apiRequest(getContext(), query);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                List<foodModel> foodList = new ArrayList<>();
                JSONArray hintsArray = jsonObject.getJSONArray("hints");

                for (int i = 0; i < hintsArray.length(); i++) {
                    JSONObject hintObject = hintsArray.getJSONObject(i);
                    JSONObject foodObject = hintObject.getJSONObject("food");

                    String label = foodObject.getString("label");

                    JSONArray measuresArray = hintObject.getJSONArray("measures");
                    if (measuresArray.length() > 0) {
                        List<String> weightLabels = new ArrayList<>();
                        List<Integer> weights = new ArrayList<>();

                        for (int j = 0; j < measuresArray.length(); j++) {
                            JSONObject measureObject = measuresArray.getJSONObject(j);
                            String weightLabel = measureObject.getString("label");
                            int weight = (int) measureObject.getDouble("weight");

                            // Exclude specific weight labels
                            if (!weightLabel.equals("Gram") && !weightLabel.equals("Ounce") && !weightLabel.equals("Pound") && !weightLabel.equals("Kilogram")
                                    && !weightLabel.equals("Cubic inch") && !weightLabel.isEmpty()) {
                                weightLabels.add(weightLabel);
                                weights.add(weight);
                            }
                        }

                        if (!weightLabels.isEmpty() && !weights.isEmpty()) {
                            foodModel item = new foodModel(label, weightLabels, weights);
                            foodList.add(item);
                        }
                    }
                }

                for (foodModel item : foodList) {
                    Log.d("GSON", "Label: " + item.getLabel() + ", Weights: " + item.getWeights() + ", Weight Labels: " + item.getWeightLabels());
                }
                RecyclerView recyclerView = view.findViewById(R.id.food_list_view);
                FoodAdapter adapter = new FoodAdapter(foodList);
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
    }
}