package com.adzan.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements LocationListener {
    private static final String TAG = "LocationService";
    private LocationManager locationManager;
    private Location currentLocation;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.e(TAG, "LocationManager is null. Location service cannot start.");
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        if (locationManager == null) {
            Log.e(TAG, "LocationManager is null. Cannot start location updates.");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3600000, 1000, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3600000, 1000, this);
        } else {
            Log.w(TAG, "Location permission not granted. Cannot start location updates.");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Log.e(TAG, "Location is null in onLocationChanged callback");
            return;
        }

        currentLocation = location;
        Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());

        String cityName = getCityName(location.getLatitude(), location.getLongitude());
        if (cityName != null) {
        Intent intent = new Intent(this, PrayerTimeService.class);
        intent.putExtra("city_name", cityName);
        startService(intent);
        stopSelf();

        } else {
            Log.w(TAG, "City name not found for the current location.");
        }
    }

    private String getCityName(double latitude, double longitude) {
        if (latitude == 0 || longitude == 0) {
            Log.w(TAG, "Invalid coordinates: " + latitude + ", " + longitude);
            return null;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                if (city == null) {
                    city = addresses.get(0).getSubAdminArea();
                }
                return city;
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
        }
        return null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
