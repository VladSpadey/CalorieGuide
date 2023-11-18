package com.example.calorieguide.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieguide.MainActivity;
import com.example.calorieguide.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private final List<foodModel> foodList;
    private final Context context;
    private final LayoutInflater inflater;
    private final View overlay;
    private final MainActivity mainActivity;

    public FoodAdapter(List<foodModel> foodList, Context context, LayoutInflater inflater, View overlay, MainActivity mainActivity) {
        this.foodList = foodList;
        this.context = context;
        this.inflater = inflater;
        this.overlay = overlay;
        this.mainActivity = mainActivity;
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
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.dailog_food_detail, null);


        TextView name = view.findViewById(R.id.txt_food_name);
        TextView per = view.findViewById(R.id.txt_food_per);
        TextView cal = view.findViewById(R.id.txt_food_details_1);
        TextView details = view.findViewById(R.id.txt_food_details_2);
        EditText edit_quantity = view.findViewById(R.id.edit_quantity);
        Button btn_add = view.findViewById(R.id.btn_food_add);
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        name.setText(food.getLabel());
        per.setText(String.format("Per: %s", food.getWeightLabels().get(0)));
        cal.setText(String.format("Energy: %s cals", food.getEnergyKcal()));
        details.setText(String.format("Protein: %s g\nFat: %s g\nCarbohydrates: %s g\nFiber: %s g\n", food.getProtein(), food.getFat(), food.getCarbohydrates(), food.getFiber()));

        btn_add.setOnClickListener(v->{
            String quantityString = edit_quantity.getText().toString();
            if (!quantityString.isEmpty()) {
                double q = Double.parseDouble(quantityString);
                dbUtil.addIntake(food, q);
                Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT).show();
                mainActivity.updateIntake();
                alertDialog.dismiss();
            } else {
                Toast.makeText(context, "Quantity needs to have a value.", Toast.LENGTH_SHORT).show();
            }
        });


        overlay.setVisibility(View.VISIBLE);
        alertDialog.setOnDismissListener(v2 -> overlay.setVisibility(View.GONE));
        alertDialog.show();
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