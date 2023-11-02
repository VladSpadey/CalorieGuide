package com.example.calorieguide.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieguide.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private final List<foodModel> foodList;

    public FoodAdapter(List<foodModel> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        foodModel food = foodList.get(position);

        holder.labelText.setText(food.getLabel());
        String weightLabel = food.getWeightLabels().get(0);
        String weight = String.valueOf(food.getWeights().get(0));
        String cal = String.valueOf(food.getEnergyKcal());
        holder.infoText.setText(weightLabel + " | " + cal +" cals");
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        TextView infoText;
        //TextView labelsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.labelText);
            //labelsTextView = itemView.findViewById(R.id.labelWeightsTextView);
            infoText = itemView.findViewById(R.id.infoText);
        }
    }
}