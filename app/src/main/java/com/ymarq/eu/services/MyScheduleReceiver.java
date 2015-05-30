package com.ymarq.eu.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.ymarq.eu.ymarq.R;

import java.util.Calendar;

/**
 * Created by eu on 1/16/2015.
 */
public class MyScheduleReceiver extends BroadcastReceiver {

    // restart service every 30 seconds
    //private static final long REPEAT_TIME = 1000 * 30;//30seconds
    //private static final long REPEAT_TIME = 500 * 60;//5 minutes
    private static final long REPEAT_TIME2 = 5000 * 120;//5 minutes

    @Override
    public void onReceive(Context context, Intent intent) {

        String key = context.getString(R.string.pref_refresh_interval_key);
        String def = context.getResources().getString(R.string.pref_refresh_interval_default);

        //SharedPreferences prefs = context.getSharedPreferences("myPrefs",
        //        Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Resources resources =  context.getResources();

        String stringMinutes =  prefs.getString(key, def);
        int minutes = Integer.parseInt(stringMinutes);

        //long updateIntervalMIliseconds = 60 * 1000 * minutes;
        long updateIntervalMIliseconds = 60 * 1000 * minutes;

        AlarmManager service = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intentStartService = new Intent(context, MyStartServiceReceiver.class);
        PendingIntent pending2 = PendingIntent.getBroadcast(context, 0, intentStartService,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        // start 30 seconds after boot completed
        cal.add(Calendar.SECOND, 30);
        // fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption

        //service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
          //      cal.getTimeInMillis(),updateIntervalMIliseconds, pending);

        service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
              cal.getTimeInMillis(),updateIntervalMIliseconds, pending2);

        // service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
        // REPEAT_TIME, pending);

    }
}
