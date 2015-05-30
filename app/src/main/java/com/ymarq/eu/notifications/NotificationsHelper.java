package com.ymarq.eu.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.ymarq.eu.activities.MainTabbedActivity;
import com.ymarq.eu.news.NewsActivityNew;
import com.ymarq.eu.common.DataNotificationContent;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.messagestree.MessageTreeActivity;
import com.ymarq.eu.ymarq.R;

import java.util.ArrayList;

/**
 * Created by eu on 1/21/2015.
 */
public class NotificationsHelper {
    private final static int NOTIFY_ID = 1;
    private int mNotifyCount = 1;
    private final String YMARQ="YMarq333";
    private static NotificationsHelper ourInstance = new NotificationsHelper();
    //Notification _foregroundNotification;

    public static NotificationsHelper getInstance() {
        return ourInstance;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    Context mContext;
    private NotificationsHelper() {
    }

    public void NotifySimple(String text) {
        String title = YMARQ;

        Intent intent = new Intent(mContext, SimpleTextActivity.class);
        intent.setAction("Notify");
        intent.putExtra(SimpleTextActivity.TITLE_EXTRA, title);
        intent.putExtra(SimpleTextActivity.BODY_TEXT_EXTRA, text);
        NotificationCompat.Builder builder = initBasicBuilder(title, text, intent);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }


    public void NotifyPersonal(DataNotificationContent notificationContent) {
        //String title2 = Description;//"You got a new message !";

        // Create the Intent to display the text in an Activity
        //Intent intent = new Intent(mContext, SimpleTextActivity.class);
        //intent.setAction("NotifyPersonal");
        //intent.putExtra(SimpleTextActivity.TITLE_EXTRA, title);
        //intent.putExtra(SimpleTextActivity.BODY_TEXT_EXTRA, text);

        String notitficationType = notificationContent.NotificationTypeResult.toString();
        Intent intent = null;
        switch (notitficationType)
        {
            case "Summary":
                //intent = new Intent(mContext, MainTabbedActivity.class);
                //intent.putExtra("FirstTab", 2);
                //intent = new Intent(mContext, MainTabbedActivity.class);//seller is missing - envelope added
                intent = new Intent(mContext, MainTabbedActivity.class);
                break;
            case "SellerMessage":
            case "BuyerMessage":
            case "NewProduct":
                DataProduct dp = DataProduct.getFromJson(notificationContent.Content)    ;
                intent = new Intent(mContext, MessageTreeActivity.class);
                intent.setData(ProductsContract.ProductEntry.buildProductsUserWithProductId2(
                        dp.getUserId(), dp.Id));
                break;
            default:
                intent = new Intent(mContext, MainTabbedActivity.class);
                break;

        }

        intent.setAction("NotifyPersonal"+System.currentTimeMillis());
        intent.putExtra(Intent.EXTRA_TEXT, notificationContent.Content);//productJson);

        if (notificationContent.getContentSecondary()!=null)
            intent.putExtra(Intent.EXTRA_TEMPLATE, notificationContent.ContentSecondary);//productJson);

        intent.putExtra(Intent.EXTRA_TITLE, "title");//getClass().toString());


        // Create Builder with basic notification info
        NotificationCompat.Builder builder = initBasicBuilder(notificationContent.Title, notificationContent.Description, intent);

        //b.setAutoCancel(true)
        //        .setDefaults(Notification.DEFAULT_ALL)
        //        .setWhen(System.currentTimeMillis())
        //        .setSmallIcon(R.drawable.ic_launcher)
        //        .setTicker("Hearty365")
        //        .setContentTitle("Default notification")
        //        .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
        //        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
        //        .setContentIntent(contentIntent)
        //        .setContentInfo("Info");

        builder.setTicker("Ymarq ticker - You got updates !");
        builder.setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND);


        // Make things personal
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));


        // Construct the Notification
        Notification notification = builder.build();

        // Display the Notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    public void NotifyMulti() {
        String title = "Notify";
        String text = "You have multiple entries";

        String detailText1 = "Never mind .. just being a cat";
        String detailText2 = "Just making sure you're paying attention";
        ++mNotifyCount;

        ArrayList<String> textValues = new ArrayList<String>();
        textValues.add(detailText1);
        textValues.add(detailText2);

        // Create the Intent to display the info in an Activity
        Intent intent = new Intent(mContext, SimpleListActivity.class);
        intent.setAction("NotifyMultiXXXX");
        intent.putExtra(SimpleListActivity.TITLE_EXTRA, title);
        intent.putExtra(SimpleListActivity.TEXT_VALUES_EXTRA, textValues);

        // Create Builder with basic notification info
        NotificationCompat.Builder builder = initBasicBuilder(title, text, intent);

        // Make multi
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_multi_logo))
                .setNumber(mNotifyCount)
                .setTicker("You received another value");


        // Construct the Notification
        Notification notification = builder.build();

        // Display the Notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    public void NotifyBigText() {
        String title = "Meow";
        String text = "Never mind .. just being a cat";
        String bigTitle = "This is the big title";
        String bigSummary = "This is the big summary";
        String notificationText = mContext.getString(R.string.big_text_for_notification);

        // Create the Intent to display the text in an Activity
        Intent intent = new Intent(mContext, SimpleTextActivity.class);
        intent.setAction("NotifyBigText");
        intent.putExtra(SimpleTextActivity.TITLE_EXTRA, bigTitle);
        intent.putExtra(SimpleTextActivity.BODY_TEXT_EXTRA, notificationText);

        // Create Builder with basic notification info
        NotificationCompat.Builder builder = initBasicBuilder(title, text, intent);

        //builder.setTicker("Meaw");does not work
        // Add the Big Text Style
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(bigTitle)
                .setSummaryText(bigSummary)
                .bigText(notificationText);
        builder.setStyle(bigTextStyle);

        // Construct the Notification
        Notification notification = builder.build();

        // Display the Notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    public void NotifyBigPicture()  {
        String title = "Meow";
        String text = "Never mind .. just being a cat";
        String bigTitle = "Growing Up";
        String bigSummary = "This is me in my box now";

        // Create the Intent to display the picture in an Activity
        Intent intent = new Intent(mContext, SimplePictureActivity.class);
        intent.setAction("NotifyBigPicture");
        intent.putExtra(SimplePictureActivity.TITLE_EXTRA, bigTitle);
        intent.putExtra(SimplePictureActivity.IMAGE_RESOURCE_ID_EXTRA, R.drawable.ymarq_dream);

        // Create Builder with basic notification info
        NotificationCompat.Builder builder = initBasicBuilder(title, text, intent);

        // Add the Big Picture Style
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(bigTitle)
                .setSummaryText(bigSummary)
                .bigPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ymarq_dream));

        builder.setStyle(bigPictureStyle);

        // Construct the Notification
        Notification notification = builder.build();

        // Display the Notification
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }


    private NotificationCompat.Builder initBasicBuilder(String title, String text, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(text);

        if (intent != null) {

            //FELIX
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            //eugen
            //PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);


            //udacity
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.

            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            //stackBuilder.addNextIntent(intent);
            //PendingIntent pendingIntent =
            //            stackBuilder.getPendingIntent(
            //                    0,
            //                    PendingIntent.FLAG_UPDATE_CURRENT
            //            );
//

            builder.setContentIntent(pendingIntent);
        }

        //euegen remove notification onclick
        builder.setAutoCancel(true);
        return builder;
    }

    private void RemoveNotify() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_ID);
    }
}
