package com.ymarq.eu.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ymarq.eu.CustomExceptionHandler;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.DataNotificationContent;
import com.ymarq.eu.common.NotificationType;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataNotifications;
import com.ymarq.eu.entities.DataNotificationsModel;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.notifications.NotificationsHelper;
import com.ymarq.eu.utilities.UrlHelper;
import com.ymarq.eu.ymarq.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by eu on 1/16/2015.
 */

public class NotificationsService extends Service {
    private final IBinder mBinder = new MyBinder();
    private List<DataMessage> list = new ArrayList<DataMessage>();
    CloudEngine mCloudEngine ;
    PhoneEngine mPhoneEngine ;
    String mUserJson ;
    Date _startDate = null;
    final int _notificationId = 989828;
    private final String mConfigTxtFile = "config.txt";

    //MyWorker _worker;
    ExecutorService _executorService;
    ScheduledExecutorService _executorServiceDelayed;
    ExecutorService _scheduale ;

    NotificationManager _notificationManager;

    Notification _foregroundNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        //_worker = new MyWorker(this);
        _executorService = Executors.newSingleThreadExecutor();
        _executorServiceDelayed = Executors.newSingleThreadScheduledExecutor();
        //_worker.MonitorGpsInBackground();
        mCloudEngine= CloudEngine.getInstance();
        mPhoneEngine = PhoneEngine.getInstance();
        mPhoneEngine.setApplicationContext(this.getApplicationContext());

        _notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    "/sdcard/Pictures/Ymarq", "http://ymarq.azurewebsites.net/home/uploadError", UrlHelper.GetPhoneId4(this)));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUserJson == null) {
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
                mUserJson = intent.getStringExtra(Intent.EXTRA_TEXT);//KEY1
            else
                mUserJson = readUserFromFile();
        }

        ServiceRunnable serviceRunnable = new ServiceRunnable(this,startId,_startDate);
        _executorService.execute(serviceRunnable);
        return Service.START_STICKY;
    }

    private String readUserFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(mConfigTxtFile);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        //_worker.stopGpsMonitoring();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        //return mBinder;
        return null;
    }

    public class MyBinder extends Binder {
        NotificationsService getService() {
            return NotificationsService.this;
        }
    }

    public List<DataMessage> getMesages() {
        return list;
    }


    class ServiceRunnable implements Runnable{
        NotificationsService _theService;
        int _startId;
        Date _startDate;

        ServiceRunnable(NotificationsService _theService,int startId,Date startDate) {
            this._theService = _theService;
            this._startId = startId;
            this._startDate = startDate;
        }

        @Override
        public void run() {
            //Location location = _worker.getLocation();

            //String address = _worker.reverseGeocode(location);

            //_worker.save(location,address,"ResponsiveUX.out");

            Calendar calendar = Calendar.getInstance();


            String key = getApplicationContext().getResources().getString(R.string.pref_refresh_interval_key);
            String def = getApplicationContext().getResources().getString(R.string.pref_refresh_interval_default);

            //SharedPreferences prefs = getApplicationContext().getSharedPreferences(def,
            //        Context.MODE_PRIVATE); // wrong - todo -  check why

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_theService.getApplicationContext());
            Resources resources =  getApplicationContext().getResources();

            String stringMinutes =  prefs.getString(key, def);
            int minutes = Integer.parseInt(stringMinutes);

            String key2 = resources.getString(R.string.pref_server_key);
            String def2 = resources.getString(R.string.pref_server_azure);
            String shouldPopNotification = prefs.getString(key2,def2);

            calendar.add(Calendar.MINUTE , (minutes *(-1)));
            // Get current date of calendar which point to the yesterday now
            Date now = calendar.getTime();
            if (mCloudEngine.getApplicationContext() == null)
                mCloudEngine.setApplicationContext(this._theService.getApplicationContext()); //todo make this more robust

            //DeviceService.startActionGetProductsAsync(this._theService.getApplicationContext(),DataUser.getFromJson(mUserJson).Id);

            DataNotificationsModel notifications = mCloudEngine.GetNotificationsByUserDate(DataUser.getFromJson(mUserJson).Id, now ,false).Result;


            //todo use this line DeviceService.startActionUpdateNotifications(this._theService.getApplicationContext(),DataUser.getFromJson(mUserJson).Id);

            if (notifications == null) {
                //throw new Exception("notifications not good - redo !");
                String e = "Error";
                return; //todo -> fix this - it happens rarely but it happens -> for example when you switch from wifi to 3g and vice versa
            }

            UpdateProvider(notifications);

            int sellerCount = notifications.SellerNotifications.size();
            int buyerCount = notifications.BuyerNotifications.size();
            int newProductsCount = notifications.NewProducts.size();
            //if (sellerCount>0 || buyerCount >0 || newProductsCount>0)
            DataNotificationContent dataNotificationContent = new DataNotificationContent();

            if (sellerCount+ buyerCount + newProductsCount == 0)
                return;

            NotificationsHelper notificationsHelper = NotificationsHelper.getInstance();
            notificationsHelper.setmContext(_theService);

            if (sellerCount+ buyerCount + newProductsCount == 1){

                //todo: do not display if only messages from me were posted
                //todo: check if missed notifications
                //todo: factory and navigate to different Activities



                //if (sellerCount+buyerCount+newProductsCount > 1) {//more than one update
                //    dataNotificationContent.setTitle("Multiple Ymarq updates.Check the summary !!");
                //    dataNotificationContent.setDescription("Seller: " + sellerCount + " Buyer: " + buyerCount + " New Products: " + newProductsCount);
                //    dataNotificationContent.setContent(mUserJson);
                //    dataNotificationContent.setNotificationTypeResult(NotificationType.Summary);
                //}
                //else
                DataNotifications dataNotifications = null;
                DataProduct product = null;

                if (sellerCount > 0) {
                    dataNotifications = notifications.SellerNotifications.get(0);
                    product = notifications.SellerNotifications.get(0).Product;
                    dataNotificationContent.setTitle(getString(R.string.notification_title_potential_buyers) + product.Description );
                    dataNotificationContent.setDescription(dataNotifications.Messages.get(0).Content);
                            dataNotificationContent.setContent(product.getAsJSON());
                    dataNotificationContent.setNotificationTypeResult(NotificationType.SellerMessage);
                }
                else if (buyerCount > 0) {
                    dataNotifications = notifications.BuyerNotifications.get(0);
                    product = notifications.BuyerNotifications.get(0).Product;
                    String PublisherPhoneNumber = notifications.BuyerNotifications.get(0).PublisherPhoneNumber;
                    dataNotificationContent.setTitle(getString(R.string.notification_title_seller_answer)  + product.Description );

                    dataNotificationContent.setDescription(dataNotifications.Messages.get(0).Content);
                    dataNotificationContent.setContent(product.getAsJSON());
                    dataNotificationContent.setNotificationTypeResult(NotificationType.BuyerMessage);
                }
                else {//new Product
                    dataNotifications = notifications.NewProducts.get(0);
                    product = notifications.NewProducts.get(0).Product;
                    dataNotificationContent.setTitle(getString(R.string.notification_title_new_product) + notifications.NewProducts.get(0).Product.Description);
                    dataNotificationContent.setDescription(getString(R.string.notification_title_product) + notifications.NewProducts.get(0).Product.Description);
                    dataNotificationContent.setContent(notifications.NewProducts.get(0).Product.getAsJSON());
                    dataNotificationContent.setNotificationTypeResult(NotificationType.NewProduct);
                }



                //String broadcastAction = "Blabla";
                //sent message receive so it can be shown in
                //Intent broadcastIntent = new Intent(broadcastAction);

                //Bundle extras = new Bundle();
                //extras.putString("send_data_product", product.getAsJSON());
                //broadcastIntent.putExtras(extras);
 //
                //getApplicationContext().sendBroadcast(broadcastIntent);

                //crash
                //if you know the user should be more personalized

                if (dataNotifications.Messages.size()>0)//this is based on the fact that only 1 message will be delivered
                {
                    mPhoneEngine.setApplicationContext(getApplicationContext());
                    String contactName = mPhoneEngine.getUserDataById2(dataNotifications.Messages.get(0).SenderId, false);

                    if (contactName.length() > 1)
                        dataNotificationContent.setTitle("@" + DataUser.getFromJson(contactName).Name +" " + getString(R.string.notification_title_have_answered) + product.Description);
                }



//
                //NotificationsHelper notificationsHelper = NotificationsHelper.getInstance();
                //notificationsHelper.setmContext(_theService);

                //notificationsHelper.NotifyPersonal(dataNotificationContent);
                //startInForeground();//oldstyle
            }
            else if  (sellerCount+ buyerCount + newProductsCount > 1) {

                DataProduct lastProduct = null;
                if (buyerCount>0)
                    lastProduct = notifications.BuyerNotifications.get(0).Product;
                if (newProductsCount>0)
                    lastProduct = notifications.NewProducts.get(0).Product;
                if (sellerCount>0)
                    lastProduct = notifications.SellerNotifications.get(0).Product;

                dataNotificationContent.setTitle(getString(R.string.notification_title_multiple_notifications));
                dataNotificationContent.setDescription(getString(R.string.notification_total_notifications) + (sellerCount + buyerCount + newProductsCount));//+notifications.NewProducts.get(0).Product.Description);
                //dataNotificationContent.setContent(mUserJson);
                dataNotificationContent.setContent(lastProduct.getAsJSON());
                dataNotificationContent.setContentSecondary(notifications.getAsJSON());
                dataNotificationContent.setNotificationTypeResult(NotificationType.Summary);
            }

            //do not pop up notifications if not asked to
            if (!shouldPopNotification.equals(getResources().getString(R.string.pref_refresh_interval_key)))
                notificationsHelper.NotifyPersonal(dataNotificationContent);





            //todo - implement all cases - get notifications from friends only/seletcted contacts / all

            //do not kill
            //DelayedService9Runnable delayedServiceRunnable =  new DelayedServiceRunnable(_theService,_startId);
            //_theService._executorServiceDelayed.schedule(delayedServiceRunnable, 5, TimeUnit.MINUTES);
        }
    }

    private boolean UpdateProvider(DataNotificationsModel notifications)
    {
        boolean res = false;
        for(DataNotifications n :notifications.BuyerNotifications)
        {
            for(DataNotifications dn :notifications.BuyerNotifications) {
                mPhoneEngine.addProductToProvider(dn.Product);
            }//todo ; remove this since it shoul be updated when you serach for producs - here is neededv only new producs
            res = res & mPhoneEngine.updateProductNotification(n.getProduct().Id,n.getMessages().size());
            UpdateMessageInProvider(n.Messages,n.Product.UserId);
        }
        for(DataNotifications n :notifications.SellerNotifications)
        {
            res = res & mPhoneEngine.updateProductNotification(n.getProduct().Id,n.getMessages().size());
        }
        for(DataNotifications n :notifications.NewProducts)
        {
            for(DataNotifications dn :notifications.NewProducts) {
               mPhoneEngine.addProductToProvider(dn.Product);
            }
            res = res & mPhoneEngine.updateProductNotification(n.getProduct().Id,n.getMessages().size());
        }
        return res;
    }

    private boolean UpdateMessageInProvider(List<DataMessage> messages,String sellerUserId )
    {
        for(DataMessage dm :messages)
            mPhoneEngine.addMessageToProvider(dm,sellerUserId);

        return true;
    }

    //public void startInForeground(){
    //    // Set basic notification information
    //    int notificationIcon = R.drawable.ic_launcher;
    //    String notificationTickerText = "LsetRetainInstance(true)aunching pluralsight service";
    //    long notificationTimeStamp = System.currentTimeMillis();
    //    _foregroundNotification = new Notification(notificationIcon,
    //            notificationTickerText, notificationTimeStamp);
//
    //    // Describe what to do if the User clicks the notification in the status bar
    //    String notificationTitleText = "New updates on your ymarq";
    //    String notificationBodyText = "Does non-UI processing";
    //    Intent myActivityIntent = new Intent(this, MainActivity.class);
    //    //myActivityIntent.putExtra(Intent.EXTRA_TEXT,mUserJson);
    //    PendingIntent startMyActivityPendingIntent = PendingIntent.getActivity(this, 0, myActivityIntent, 0);
    //    _foregroundNotification.setLatestEventInfo(this, notificationTitleText, notificationBodyText, startMyActivityPendingIntent);
//
    //    // ID to use w/ Notification Manager for _foregroundNotification
    //    // Set the service to foreground status and provide notification info
    //    this.startForeground(_notificationId, _foregroundNotification);
//
    //}

    class DelayedServiceRunnable implements Runnable{
        NotificationsService _theService;
        int _startId;

        DelayedServiceRunnable(NotificationsService _theService,int startId) {
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





