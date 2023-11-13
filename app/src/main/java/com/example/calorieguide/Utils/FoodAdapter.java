package com.example.calorieguide.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieguide.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private final List<foodModel> foodList;
    private final Context context;

    public FoodAdapter(List<foodModel> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
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

        // Set a click listener for the details button
        holder.btn_Info.setOnClickListener(v -> {
            showDetailsDialog(food);
        });
    }

    private void showDetailsDialog(foodModel food) {
        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Details for " + food.getLabel());

        // Add detailed information to the dialog (customize this based on your needs)
        String details = "Label: " + food.getLabel() + "\n" +
                "Energy: " + food.getEnergyKcal() + " cals\n" +
                "Protein: " + food.getProtein() + " g\n" +
                "Fat: " + food.getFat() + " g\n" +
                "Carbohydrates: " + food.getCarbohydrates() + " g\n" +
                "Fiber: " + food.getFiber() + " g\n";

        builder.setMessage(details);

        // Show the dialog
        builder.show();
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        TextView infoText;
        ImageButton btn_Info;
        //TextView labelsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.labelText);
            //labelsTextView = itemView.findViewById(R.id.labelWeightsTextView);
            infoText = itemView.findViewById(R.id.infoText);
            btn_Info = itemView.findViewById(R.id.btn_addFood);
        }
    }
}