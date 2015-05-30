package com.ymarq.eu.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class YmarqSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static YmarqSyncAdapter sYmarqSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("YmarqSyncService", "onCreate - YmarqSyncService");
        synchronized (sSyncAdapterLock) {
            if (sYmarqSyncAdapter == null) {
                sYmarqSyncAdapter = new YmarqSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sYmarqSyncAdapter.getSyncAdapterBinder();
    }
}