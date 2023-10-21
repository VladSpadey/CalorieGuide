package com.example.calorieguide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogFragment extends Fragment {
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weight, container, false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }
}