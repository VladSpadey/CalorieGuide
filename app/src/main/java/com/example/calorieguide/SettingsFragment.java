package com.example.calorieguide;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    Button btn_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find the button within the fragment's layout
        btn_logout = view.findViewById(R.id.btn_logout);

        // Set a click listener for the button
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the logout action
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), Login.class); // Use requireContext() to get the context
                startActivity(intent);
                requireActivity().finish(); // Finish the hosting activity
            }
        });

        return view;
    }
}