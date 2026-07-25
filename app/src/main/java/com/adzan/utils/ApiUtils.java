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

            String searchTerm = cityName.toLowerCase();
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
        int day = calendar.get(Calendar.DAY_OF_MONTH);

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
}