package com.example.calorieguide.Utils;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class apiRequest extends AsyncTaskLoader<String> {
    private String query;
    public apiRequest(Context context, String query) {
        super(context);
        this.query = query;
    }
    @Nullable
    @Override
    public String loadInBackground() {
        try {
            String apiKey = "7a44747f0483cf75f0de2c698cfa0a07";
            String apiID = "eb5024ca";
            String edamamUrl = "https://api.edamam.com/search";

            String apiUrl = edamamUrl + "?q=" + query + "&app_id=" + apiID + "&app_key=" + apiKey;
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            inputStream.close();

            // Return the API response as a string
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

