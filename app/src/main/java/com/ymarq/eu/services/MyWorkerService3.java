package com.ymarq.eu.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.entities.DataMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by eu on 1/16/2015.
 */

public class MyWorkerService3 extends Service {
    private final IBinder mBinder = new MyBinder();
    private List<DataMessage> list = new ArrayList<DataMessage>();
    CloudEngine mCloudEngine = CloudEngine.getInstance();
    String mProductId = "7ce32384-703c-4ef0-8b90-cd6a44195230";

    MyWorker _worker;
    ExecutorService _executorService;
    ScheduledExecutorService _executorServiceDelayed;
    ExecutorService _scheduale ;

    @Override
    public void onCreate() {
        super.onCreate();
        _worker = new MyWorker(this);
        _executorService = Executors.newSingleThreadExecutor();
        _executorServiceDelayed = Executors.newSingleThreadScheduledExecutor();
        _worker.MonitorGpsInBackground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceRunnable serviceRunnable = new ServiceRunnable(this,startId);
        _executorService.execute(serviceRunnable);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        _worker.stopGpsMonitoring();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        //return mBinder;
        return null;
    }

    public class MyBinder extends Binder {
        MyWorkerService3 getService() {
            return MyWorkerService3.this;
        }
    }

    class ServiceRunnable implements Runnable{
        MyWorkerService3 _theService;
        int _startId;

        ServiceRunnable(MyWorkerService3 _theService,int startId) {
            this._theService = _theService;
            this._startId = startId;
        }

        @Override
        public void run() {
            Location location = _worker.getLocation();

            String address = _worker.reverseGeocode(location);

            _worker.save(location,address,"ResponsiveUX.out");

            DelayedServiceRunnable delayedServiceRunnable =  new DelayedServiceRunnable(_theService,_startId);
            _theService._executorServiceDelayed.schedule(delayedServiceRunnable, 5, TimeUnit.MINUTES);
        }
    }

    class DelayedServiceRunnable implements Runnable{
        MyWorkerService3 _theService;
        int _startId;

        DelayedServiceRunnable(MyWorkerService3 _theService,int startId) {
            this._theService = _theService;
            this._startId = startId;
        }

        @Override
        public void run() {
            //eugen it will stop only if there is no work to do and nobody called the service for long time
            _theService.stopSelfResult(_startId);;
        }
    }
}




