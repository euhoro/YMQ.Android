package com.ymarq.eu.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by eu on 1/16/2015.
 */
public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotificationsService.class);//MyWorkerService3.class);
        context.startService(service);
    }
}
