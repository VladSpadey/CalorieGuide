package com.example.calorieguide;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.calorieguide.Utils.dbUtil;


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

    addData = view.findViewById(R.id.btn_addWeight);
    addDataListener();

    return view;
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

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }

            updateWeight.setOnClickListener(v1 -> {

                double weight = 0 ;
                try {
                    weight = Double.parseDouble(input.getText().toString());
                    dbUtil.addDoubleToDb("weight", weight);
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