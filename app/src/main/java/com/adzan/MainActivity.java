package com.adzan;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adzan.services.LocationService;
import com.adzan.services.PrayerTimeService;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2;
    private static final String PRAYER_TIME_STATUS_ACTION = "com.adzan.PRAYER_TIME_STATUS";
    
    private TextView prayerTimeText;
    private EditText cityEditText;
    private Button getPrayerTimesButton;
    private String notificationPermissionPendingCity;
    private BroadcastReceiver statusReceiver;
    private boolean receiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prayerTimeText = findViewById(R.id.prayerTimeText);
        cityEditText = findViewById(R.id.cityEditText);
        getPrayerTimesButton = findViewById(R.id.getPrayerTimesButton);

        setupStatusReceiver();
        getPrayerTimesButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString().trim();
            if (cityName.isEmpty()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    enableLocationBasedPrayer();
                }
                return;
            }
            fetchPrayerTimes(cityName);
        });
    }

    private void setupStatusReceiver() {
        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                String message = intent.getStringExtra("message");
                
                if ("loading".equals(status)) {
                    prayerTimeText.setText(getString(R.string.loading_message));
                    prayerTimeText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                } else if ("success".equals(status)) {
                    String city = intent.getStringExtra("city");
                    String times = intent.getStringExtra("prayer_times");
                    prayerTimeText.setText(getString(R.string.prayer_times_title, city, times));
                    prayerTimeText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else if ("error".equals(status)) {
                    prayerTimeText.setText(getString(R.string.error_prefix) + message);
                    prayerTimeText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        };
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(statusReceiver, new IntentFilter(PRAYER_TIME_STATUS_ACTION), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(statusReceiver, new IntentFilter(PRAYER_TIME_STATUS_ACTION));
        }
        receiverRegistered = true;
    }

    private void enableLocationBasedPrayer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        Toast.makeText(this, R.string.location_activating, Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    private void fetchPrayerTimes(String cityName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionPendingCity = cityName;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }

        Intent intent = new Intent(this, PrayerTimeService.class);
        intent.putExtra("city_name", cityName);
        intent.putExtra("status_receiver_action", PRAYER_TIME_STATUS_ACTION);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationBasedPrayer();
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (notificationPermissionPendingCity != null && !notificationPermissionPendingCity.isEmpty()) {
                fetchPrayerTimes(notificationPermissionPendingCity);
                notificationPermissionPendingCity = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiverRegistered && statusReceiver != null) {
            unregisterReceiver(statusReceiver);
            receiverRegistered = false;
        }
    }
}