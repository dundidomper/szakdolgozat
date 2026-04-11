package com.example.app1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            Log.d("REMINDER", "No user logged in. Skipping notification.");
            return;
        }

        String uid = user.getUid();
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String lastWorkout = prefs.getString("lastWorkoutDate_" + uid, null);
        String today = getTodayDateString();

        Log.d("REMINDER", "Receiver triggered!");

        if (lastWorkout == null || !lastWorkout.equals(today)) {
            NotificationHandler.showNotification(
                    context,
                    "Edzésemlékeztető",
                    "Ne felejtsd el megcsinálni a mai edzésed!"
            );
        } else {
            NotificationHandler.showNotification(
                    context,
                    "Szép munka!",
                    "Megcsináltad a mai edzésed. Talán ideje naplózni a gondolataid."
            );
        }
    }

    private String getTodayDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
