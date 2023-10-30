package com.example.calorieguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.calorieguide.Utils.apiRequest;

public class LogFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
    View view;
    private static final int LOADER_ID = 1;
    private String query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log, container, false);
        query = "kitkat bar";

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
            Log.d("API Response", data);
        } else {
            Log.e("API Error", "API request failed");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}