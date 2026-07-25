package com.adzan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adzan.services.PrayerTimeService;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView prayerTimeText;
    private EditText cityEditText;
    private Button getPrayerTimesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prayerTimeText = findViewById(R.id.prayerTimeText);
        cityEditText = findViewById(R.id.cityEditText);
        getPrayerTimesButton = findViewById(R.id.enableLocationButton);

        getPrayerTimesButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString().trim();
            if (cityName.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchPrayerTimes(cityName);
        });
    }

    private void fetchPrayerTimes(String cityName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        2);
                return;
            }
        }

        Intent intent = new Intent(this, PrayerTimeService.class);
        intent.putExtra("city_name", cityName);
        startService(intent);
        Toast.makeText(this, "Fetching prayer times for " + cityName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String cityName = cityEditText.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchPrayerTimes(cityName);
            }
        }
    }
}