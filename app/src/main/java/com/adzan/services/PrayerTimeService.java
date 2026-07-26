package com.adzan.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.adzan.AdzanApp;
import com.adzan.R;
import com.adzan.utils.ApiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class PrayerTimeService extends Service {
    private static final String TAG = "PrayerTimeService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cityName = intent.getStringExtra("city_name");
        String statusAction = intent.getStringExtra("status_receiver_action");
        
        sendBroadcastStatus(statusAction, "loading", null, null, null);

        new Thread(() -> {
            try {
                if (cityName == null || cityName.trim().isEmpty()) {
                    sendBroadcastStatus(statusAction, "error", "Nama kota kosong", null, null);
                    return;
                }
                String cityId = ApiUtils.searchCityByName(cityName);
                if (cityId == null) {
                    Log.e(TAG, "City not found: " + cityName);
                    sendBroadcastStatus(statusAction, "error", "Kota tidak ditemukan: " + cityName, null, null);
                    return;
                }
                String prayerTimesJson = ApiUtils.getPrayerTimes(cityId);
                JSONObject prayerTimes = new JSONObject(prayerTimesJson);
                
                JSONObject jadwal = prayerTimes.getJSONObject("data").getJSONObject("jadwal");
                StringBuilder prayerTimesText = new StringBuilder();
                String[] prayerNames = {"subuh", "dzuhur", "ashar", "maghrib", "isya"};
                for (String prayerName : prayerNames) {
                    prayerTimesText.append(prayerName.toUpperCase())
                                   .append(": ")
                                   .append(jadwal.getString(prayerName))
                                   .append("\n");
                }
                
                schedulePrayerTimeNotifications(prayerTimes);
                sendBroadcastStatus(statusAction, "success", null, cityName, prayerTimesText.toString());
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching prayer times: " + e.getMessage());
                sendBroadcastStatus(statusAction, "error", e.getMessage(), null, null);
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void sendBroadcastStatus(String action, String status, String message, String city, String prayerTimes) {
        if (action == null) return;
        
        Intent intent = new Intent(action);
        intent.putExtra("status", status);
        intent.putExtra("message", message);
        intent.putExtra("city", city);
        intent.putExtra("prayer_times", prayerTimes);
        sendBroadcast(intent);
    }

    private void schedulePrayerTimeNotifications(JSONObject prayerTimes) throws JSONException {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String[] prayerNames = {"subuh", "dzuhur", "ashar", "maghrib", "isya"};

        for (String prayerName : prayerNames) {
            String prayerTime = prayerTimes.getJSONObject("data").getJSONObject("jadwal").getString(prayerName);
            String[] timeParts = prayerTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            Intent intent = new Intent(this, PrayerTimeReceiver.class);
            intent.putExtra("prayer_name", prayerName);
            int requestCode = (prayerName + ":" + calendar.get(Calendar.YEAR) + ":" + calendar.get(Calendar.DAY_OF_YEAR)).hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class PrayerTimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prayerName = intent.getStringExtra("prayer_name");
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AdzanApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("It's time for " + prayerName)
                    .setContentText("Time to pray.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w("PrayerTimeReceiver", "POST_NOTIFICATIONS permission not granted");
                return;
            }
            notificationManager.notify(1, builder.build());
        }
    }
}