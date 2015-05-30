package com.ymarq.eu.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ymarq.eu.activities.MainTabbedActivity;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.DataNotificationContent;
import com.ymarq.eu.common.NotificationType;
import com.ymarq.eu.entities.DataApiNotification;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataNotifications;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataSubscription;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.notifications.NotificationsHelper;
import com.ymarq.eu.sync.YmarqSyncAdapter;
import com.ymarq.eu.ymarq.R;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    private static String TAG = "GCMSERVICE";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                String message = intent.getStringExtra("message");
                if (message != null && message.length()>0) {
                    Intent service = new Intent(this, NotificationsService.class);//MyWorkerService3.class);
                    this.startService(service);
                }
                //else ( somethimes google just sends ping on the first use ( login )


                String broadcastAction = "Blabla";
                //sent message receive so it can be shown in
                Intent broadcastIntent = new Intent(broadcastAction);

                //Bundle extras = new Bundle();
                //extras.putString("send_data_product", product.getAsJSON());
                //broadcastIntent.putExtras(extras);

                getApplicationContext().sendBroadcast(broadcastIntent);


                //if (HandleNotification(intent)) return;
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private boolean HandleNotification(Intent intent) {
        NotificationsHelper notificationsHelper = NotificationsHelper.getInstance();
        notificationsHelper.setmContext(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Resources resources =  getApplicationContext().getResources();

        String key2 = resources.getString(R.string.pref_notification_get_key);
        String def2 = resources.getString(R.string.pref_update_mode_default);
        String shouldPopNotification = prefs.getString(key2,def2);


        String message = intent.getStringExtra("message");
        DataApiNotification dataApiNotification = DataApiNotification.getFromJson(message);

        DataNotificationContent dataNotificationContent = new DataNotificationContent();

        if (dataApiNotification == null)
            return true;


        //product
        if (dataApiNotification.MessageType==0) {
            DataProduct dp = DataProduct.getFromJson(dataApiNotification.Data);

            PhoneEngine.getInstance().addProductToProvider(dp);
            PhoneEngine.getInstance().updateProductNotification(dp.Id, 1);

            String contactName = PhoneEngine.getInstance().getContactNameByUserId(dp.UserId);

            dataNotificationContent.setTitle(getString(R.string.notification_title_new_product) +" "+ dp.Description);
            dataNotificationContent.setDescription("@" +" " + contactName);
            dataNotificationContent.setContent(dp.getAsJSON());
            dataNotificationContent.setNotificationTypeResult(NotificationType.NewProduct);

        }

        //message
        else if (dataApiNotification.MessageType==1) {

            DataMessage dataMessage = DataMessage.getFromJson(dataApiNotification.Data);

           // PhoneEngine.getInstance().addProductToProvider(dp);
            PhoneEngine.getInstance().updateProductNotification(dataMessage.ProductId,1);

            DataProduct dataProduct = PhoneEngine.getInstance().getProductsDataByProductId(dataMessage.ProductId);
            String meSerialized =  PhoneEngine.getInstance().getUserDataById2("", true);
            DataUser meUser = DataUser.getFromJson(meSerialized);

            String senderUserSerialized = PhoneEngine.getInstance().getUserDataById2(dataMessage.SenderId, true);
            DataUser senderUser = DataUser.getFromJson(senderUserSerialized);

            boolean isForMyProducts = dataProduct.UserId.equals(meUser.Id);

            if (isForMyProducts) {
                dataNotificationContent.setTitle(getString(R.string.notification_title_potential_buyers) + dataProduct.Description );
                dataNotificationContent.setDescription(dataMessage.Content);
                dataNotificationContent.setContent(dataProduct.getAsJSON());
                dataNotificationContent.setNotificationTypeResult(NotificationType.SellerMessage);
            }
            else {
               dataNotificationContent.setTitle(getString(R.string.notification_title_seller_answer)  + dataProduct.Description );

               dataNotificationContent.setDescription(dataMessage.Content);
               dataNotificationContent.setContent(dataProduct.getAsJSON());
               dataNotificationContent.setNotificationTypeResult(NotificationType.BuyerMessage);
            }
        }
        //subscription
        else if (dataApiNotification.MessageType== 2) {
            // DataSubscription dp = DataSubscription.getFromJson(dataApiNotification.Data);
            //insert data subscription to the table and shouw it in the news
        }
        else
        {
            //throw not implemented
        }

        if (shouldPopNotification.equals(getResources().getString(R.string.pref_update_mode_default)))
            notificationsHelper.NotifyPersonal(dataNotificationContent);

        //if type = 0
        //deserialize product + insert to database + notification1 if subscription or notification 2 if friend
        //if type = 1
        //  deserialize message + insert database + notification
        //check how many not read
        // if one
        //  notification one
        // if multi

        //notifiaction multi


        //// This loop represents the service doing some work.
        //for (int i=0; i<5; i++) {
        //    Log.i(TAG, "Working... " + (i + 1)
        //            + "/5 @ " + SystemClock.elapsedRealtime());
        //    try {
        //        Thread.sleep(5000);
        //    } catch (InterruptedException e) {
        //    }
        //}
        //Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
        //// Post notification of received message.
        //sendNotification("Received: " + extras.toString());
        //Log.i(TAG, "Received: " + extras.toString());

        //Intent service = new Intent(this, NotificationsService.class);


        //YmarqSyncAdapter.initializeSyncAdapter(this);

        //YmarqSyncAdapter.syncImmediately(this);
        return false;
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                //new Intent(this, DemoActivity.class), 0);
                new Intent(this, MainTabbedActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.ic_stat_gcm)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}