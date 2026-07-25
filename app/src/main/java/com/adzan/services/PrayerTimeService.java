package com.adzan.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
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
import java.util.HashMap;
import java.util.Map;

public class PrayerTimeService extends Service {
    private static final String TAG = "PrayerTimeService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        new Thread(() -> {
            try {
                String prayerTimesJson = ApiUtils.getPrayerTimes(latitude, longitude);
                JSONObject prayerTimes = new JSONObject(prayerTimesJson);
                schedulePrayerTimeNotifications(prayerTimes);
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching prayer times: " + e.getMessage());
            }
        }).start();

        return START_NOT_STICKY;
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, prayerName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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