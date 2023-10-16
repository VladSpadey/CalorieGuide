package com.example.calorieguide;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class WeightFragment extends Fragment {
    FirebaseUser user;
    String uID, email;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_settings, container, false);
    MainActivity mainActivity = (MainActivity) getActivity();
    assert mainActivity != null;
    user = mainActivity.user;
    uID = mainActivity.uIDDB;
    email = mainActivity.email;




        return inflater.inflate(R.layout.fragment_weight, container, false);
    }
}