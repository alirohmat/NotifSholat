package com.adzan.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class ApiUtils {
    private static final String BASE_URL = "https://api.myquran.com/v3";
    private static final OkHttpClient client = new OkHttpClient();

    public static String getPrayerTimes(double latitude, double longitude) throws IOException, JSONException {
        // Find nearest city first
        String cityId = findNearestCity(latitude, longitude);
        if (cityId == null) {
            throw new IOException("No city found for coordinates");
        }

        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Get prayer times for the city
        String url = BASE_URL + "/sholat/jadwal/" + cityId + "/" + year + "/" + month + "/" + day;
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    private static String findNearestCity(double latitude, double longitude) throws IOException, JSONException {
        String url = BASE_URL + "/sholat/kota/semua";
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String jsonResponse = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray cities = jsonObject.getJSONArray("data");
            
            String nearestCityId = null;
            double minDistance = Double.MAX_VALUE;

            for (int i = 0; i < cities.length(); i++) {
                JSONObject city = cities.getJSONObject(i);
                // Note: API doesn't provide lat/lng for cities, so we'll use a simplified approach
                // In a real app, you'd need lat/lng data for each city
                // For now, we'll just return the first city or implement a simple distance calculation
                // if we have coordinate data for cities
                return city.getString("id");
            }

            return nearestCityId;
        }
    }
}