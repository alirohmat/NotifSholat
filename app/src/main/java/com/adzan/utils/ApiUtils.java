package com.adzan.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ApiUtils {
    private static final String BASE_URL = "https://api.myquran.com/v3";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String searchCityByName(String cityName) throws IOException, JSONException {
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

            String searchTerm = cityName.trim().toLowerCase();
            for (int i = 0; i < cities.length(); i++) {
                JSONObject city = cities.getJSONObject(i);
                String cityFullName = city.getString("lokasi").toLowerCase();
                if (cityFullName.contains(searchTerm)) {
                    return city.getString("id");
                }
            }

            return null;
        }
    }

    public static String getPrayerTimes(String cityId) throws IOException, JSONException {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        String period = year + "-" + month;
        String url = BASE_URL + "/sholat/jadwal/" + cityId + "/" + period;

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
}