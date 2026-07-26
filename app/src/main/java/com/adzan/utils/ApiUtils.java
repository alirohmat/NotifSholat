package com.adzan.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String PREFS_NAME = "adzan_prefs";
    private static final String CACHED_CITY_DATA_KEY = "cached_city_data";
    private static final String CACHE_EXPIRY_KEY = "city_cache_expiry";
    private static final long CACHE_EXPIRY_HOURS = 24;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String searchCityByName(Context context, String cityName) throws IOException, JSONException {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String cachedCities = prefs.getString(CACHED_CITY_DATA_KEY, null);
        long cacheExpiry = prefs.getLong(CACHE_EXPIRY_KEY, 0);

        if (cachedCities != null && System.currentTimeMillis() < cacheExpiry) {
            String cityId = findCityIdInJson(cachedCities, cityName);
            if (cityId != null) return cityId;
        }

        String url = BASE_URL + "/sholat/kota/semua";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String jsonResponse = response.body().string();
            
            long newExpiry = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(CACHE_EXPIRY_HOURS);
            prefs.edit()
                .putString(CACHED_CITY_DATA_KEY, jsonResponse)
                .putLong(CACHE_EXPIRY_KEY, newExpiry)
                .apply();
            
            return findCityIdInJson(jsonResponse, cityName);
        }
    }

    private static String findCityIdInJson(String jsonResponse, String cityName) throws JSONException {
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

    public static String getPrayerTimes(Context context, String cityId) throws IOException, JSONException {
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