package com.ymarq.eu.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.business.StorageEngine;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataFriendContact;
import com.ymarq.eu.entities.DataGroupFriends;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataNotifications;
import com.ymarq.eu.entities.DataNotificationsModel;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataSubscription;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.utilities.YMQConst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DeviceService extends IntentService {
    private static String LOG_TAG = "DeviceService";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    //private static final String ACTION_UPDATE_NOTIFICATIONS = "com.ymarq.eu.services.action.ACTION_UPDATE_NOTIFICATIONS";

    private static final String ACTION_GET_SUBSCRIPTIONS_ASYNC = "com.ymarq.eu.services.action.ACTION_GET_SUBSCRIPTIONS_ASYNC";

    private static final String ACTION_GET_PRODUCTS_ASYNC = "com.ymarq.eu.services.action.ACTION_GET_PRODUCTS_ASYNC";
    private static final String ACTION_OBJ_ADD_PRODUCT = "com.ymarq.eu.services.action.ACTION_OBJ_ADD_PRODUCT";
    private static final String ACTION_DELETE_PRODUCT = "com.ymarq.eu.services.action.ACTION_DELETE_PRODUCT";
    private static final String ACTION_LEAVE_PRODUCT = "com.ymarq.eu.services.action.ACTION_LEAVE_PRODUCT";
    //todo maybe add update


    private static final String ACTION_HANDLE_CONTACTS_FIRST = "com.ymarq.eu.services.action.ACTION_HANDLE_CONTACTS_FIRST";
    private static final String ACTION_HANDLE_CONTACTS_UPDATE = "com.ymarq.eu.services.action.ACTION_HANDLE_CONTACTS_UPDATE";

    // TODO: Rename parameters
    private static final String EXTRA_USER_ID2 = "com.ymarq.eu.services.extra.EXTRA_USER_ID2";
    private static final String EXTRA_USER_IS_ME = "com.ymarq.eu.services.extra.EXTRA_USER_IS_ME";

    //private static final String EXTRA_REAL_USER_ID2 = "com.ymarq.eu.services.extra.EXTRA_REAL_USER_ID2";
    private static final String EXTRA_PRODUCT_ID2 = "com.ymarq.eu.services.extra.EXTRA_PRODUCT_ID2";
    private static final String EXTRA_IS_PRODUCT_MINE = "com.ymarq.eu.services.extra.EXTRA_IS_PRODUCT_MINE";

    private static final String EXTRA_OBJ_PRODUCT = "com.ymarq.eu.entities.DataProduct";
    private static final String EXTRA_COUNTRY_CODE = "com.ymarq.eu.services.extra.EXTRA_COUNTRY_CODE";

    public DeviceService() {
        super("DeviceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();


            if (ACTION_GET_SUBSCRIPTIONS_ASYNC.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_USER_ID2);
                handleActionSubscriptionToDeviceAsync(param1);
            }
            else if (ACTION_GET_PRODUCTS_ASYNC.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_USER_ID2);
                final boolean param2 = intent.getBooleanExtra(EXTRA_IS_PRODUCT_MINE, true);
                handleActionProductsToDeviceAsync(param1, param2);
            } else if (ACTION_OBJ_ADD_PRODUCT.equals(action)) {
                DataProduct dp = (DataProduct) intent.getSerializableExtra(EXTRA_OBJ_PRODUCT);
                handleActionAddProductAsync(dp);
            }
            //else if (ACTION_UPDATE_NOTIFICATIONS.equals(action)) {
            //    final String param1 = intent.getStringExtra(EXTRA_USER_ID2);
            //    handleActionUpdateNotifications(param1);
            //}
            else if (ACTION_DELETE_PRODUCT.equals(action)) {
                final String param2 = intent.getStringExtra(EXTRA_PRODUCT_ID2);
                final boolean param3 = intent.getBooleanExtra(EXTRA_IS_PRODUCT_MINE, false);
                handleActionDeleteProduct(param2,param3);
            }
            else if (ACTION_LEAVE_PRODUCT.equals(action)) {
                final String param2 = intent.getStringExtra(EXTRA_PRODUCT_ID2);
                final String param3 = intent.getStringExtra(EXTRA_USER_ID2);
                handleActionLeaveProduct(param2, param3);
            }
            else if (ACTION_HANDLE_CONTACTS_FIRST.equals(action)) {
                final String userId = intent.getStringExtra(EXTRA_USER_ID2);
                final int contryCode = intent.getIntExtra(EXTRA_COUNTRY_CODE, 0);
                handleActionHandleContacts(contryCode, userId);
            } else if (ACTION_HANDLE_CONTACTS_UPDATE.equals(action)) {
                final String userId = intent.getStringExtra(EXTRA_USER_ID2);
                handleActionUpdateContacts(userId);
            }
        }
    }


    public DeviceService(String name) {
        super("YmarqDeviceService");
    }

    public static void startActionUpdateNotifications(Context context, String userId2) {
        Intent intent = new Intent(context, DeviceService.class);
        intent.setAction(ACTION_GET_PRODUCTS_ASYNC);
        intent.putExtra(EXTRA_USER_ID2, userId2);
        context.startService(intent);
    }

    //private void handleActionUpdateNotifications(String userId2) {
    //    DataNotificationsModel notifications = CloudEngine.getInstance().GetNotificationsByUserDate(userId2, null, false).Result;
//
    //    boolean res = false;
    //    for(DataNotifications n :notifications.BuyerNotifications)
    //    {
    //        for(DataNotifications dn :notifications.BuyerNotifications) {
    //            PhoneEngine.getInstance().addProductToProvider(dn.Product);
    //        }//todo ; remove this since it shoul be updated when you serach for producs - here is neededv only new producs
    //        res = res & PhoneEngine.getInstance().updateProductNotification(n.getProduct().Id, n.getMessages().size());
    //    }
    //    for(DataNotifications n :notifications.SellerNotifications)
    //    {
    //        res = res & PhoneEngine.getInstance().updateProductNotification(n.getProduct().Id, n.getMessages().size());
    //    }
    //    for(DataNotifications n :notifications.NewProducts)
    //    {
    //        for(DataNotifications dn :notifications.NewProducts) {
    //            PhoneEngine.getInstance().addProductToProvider(dn.Product);
    //        }
    //        res = res & PhoneEngine.getInstance().updateProductNotification(n.getProduct().Id, n.getMessages().size());
    //    }
    //    //return res;
    //}

    public static void startActionSubscriptionToDeviceAsync  (Context context,String userId2)
    {
        Intent intent = new Intent(context, DeviceService.class);
        intent.putExtra(EXTRA_USER_ID2, userId2);
        intent.setAction(ACTION_GET_SUBSCRIPTIONS_ASYNC);
        context.startService(intent);
    }

    public static void startActionUpdateContacts  (Context context,String userId2)
    {
        Intent intent = new Intent(context, DeviceService.class);
        intent.putExtra(EXTRA_USER_ID2, userId2);
        intent.setAction(ACTION_HANDLE_CONTACTS_UPDATE);
        context.startService(intent);
    }

    private void handleActionUpdateContacts(String userId2) {
        List<DataFriendContact> phoneList = PhoneEngine.getInstance().readContactFromProvider();

        DataGroupFriends df = new DataGroupFriends();
        df.UserId = userId2;
        df.Members = phoneList;

        DataApiResult<List<DataFriendContact>> result =  CloudEngine.getInstance().GetFriendsStatus(df, false);
        if (result != null && result.Result!=null && result.Result.size()>0)
            PhoneEngine.getInstance().updateUsersProvider(userId2, result.Result);
    }

    public static void startActionGetProductsAsync(Context context, String userId2,boolean areMine ) {
        Intent intent = new Intent(context, DeviceService.class);
        intent.setAction(ACTION_GET_PRODUCTS_ASYNC);
        intent.putExtra(EXTRA_USER_ID2, userId2);
        intent.putExtra(EXTRA_IS_PRODUCT_MINE, areMine);
        context.startService(intent);
    }


    private void handleActionSubscriptionToDeviceAsync(String userId) {
        {
            //sync Operations
            DataApiResult<List<DataSubscription>> result = PhoneEngine.getInstance().getSubscriptionsDataById2(userId);
            DataApiResult<List<DataSubscription>> resultCloud = CloudEngine.getInstance().GetSubscriptions(userId, false);

            if (result.Error == null && result.Result != null && resultCloud!= null &&  resultCloud.Result!= null) {
                if (result.Result.size() != resultCloud.Result.size()) //resync
                {
                    PhoneEngine.getInstance().deleteSusbcriptionByUserId(userId);
                    PhoneEngine.getInstance().insertSubscriptionDataInProvider(resultCloud.Result, userId);
                    //result =  PhoneEngine.getInstance().getProductsDataById2(userId);//again from provider
                }
            }
        }
    }

    /**
     * Handle action add products
     * parameters.
     */
    private void handleActionProductsToDeviceAsync(String userId, boolean areProductsMine) {
            if (areProductsMine) {
                //sync Operations
                PhoneEngine.getInstance().setApplicationContext(this.getApplicationContext());
                DataApiResult<List<DataProduct>> result = PhoneEngine.getInstance().getProductsDataById2(userId);
                DataApiResult<List<DataProduct>> resultCloud = CloudEngine.getInstance().GetProducts(userId, false);

                if (result.Error == null && result.Result != null && resultCloud != null && resultCloud.Result != null) {
                    if (result.Result.size() != resultCloud.Result.size()) //resync
                    {
                        PhoneEngine.getInstance().deleteProductsByUserId(userId);
                        PhoneEngine.getInstance().insertProductsDataInProvider(resultCloud.Result);//, userId);
                        //result =  PhoneEngine.getInstance().getProductsDataById2(userId);//again from provider
                        InsertMessages(resultCloud);


                    }
                }
                //return result;
            }
        else{
            DataApiResult<List<DataProduct>> resultCloud = CloudEngine.getInstance().GetTopProducts(userId, false);

            if (resultCloud != null && resultCloud.Result != null) {
                //PhoneEngine.getInstance().deleteProductsByUserId(userId);
                PhoneEngine.getInstance().insertProductsDataInProvider(resultCloud.Result);//, null);//not my products
                InsertMessages(resultCloud);
                //result =  PhoneEngine.getInstance().getProductsDataById2(userId);//again from provider

                /////add the messages
                //for (DataProduct dp : resultCloud.Result) {
                //    DataApiResult<List<DataMessage>> res = CloudEngine.getInstance().GetMessages(UUID.fromString(dp.Id), false);
                //    for (DataMessage dm : res.Result) {
                //        PhoneEngine.getInstance().addMessageToProvider(dm, dp.UserId);
                //    }
                //}
            }

        }
    }

    private void InsertMessages(DataApiResult<List<DataProduct>> resultCloud) {
        ///add the messages
        for (DataProduct dp : resultCloud.Result) {
            DataApiResult<List<DataMessage>> res = CloudEngine.getInstance().GetMessages(UUID.fromString(dp.Id), false);
            for (DataMessage dm : res.Result) {
                PhoneEngine.getInstance().addMessageToProvider(dm, dp.UserId);
            }
        }
    }

    public static void startActionAddProductAsync(Context context, String productId) {
        Intent intent = new Intent(context, DeviceService.class);
        intent.setAction(ACTION_OBJ_ADD_PRODUCT);
        DataProduct dp = DataProduct.getFromJson(productId);
        intent.putExtra(EXTRA_OBJ_PRODUCT, dp);
        context.startService(intent);
    }

    private void handleActionAddProductAsync(DataProduct dp) {
        // TODO: Handle action Baz
        //DataProduct dp = DataProduct.getFromJson(productId2);
        dp.ImageContent = StorageEngine.getInstance().getEncoded(dp.Image);
        dp.Image = null;
        DataApiResult<DataProduct> res = CloudEngine.getInstance().PublishProduct(dp, false);
        if (res != null && res.Error == null && res.Result != null) {
            //res.Result.GiveAway = dp.GiveAway;
            PhoneEngine.getInstance().addProductToProvider(res.Result);

            DataMessage dm = new DataMessage(res.Result.Description, dp.UserId, res.Result.Id);
            CloudEngine.getInstance().SendMessage(dm, true);
        }
    }

    // TODO: Customize helper method
    public static void startActionDeleteProduct(Context context, String productId2,boolean isMine) {
        Intent intent = new Intent(context, DeviceService.class);
        intent.setAction(ACTION_DELETE_PRODUCT);
        intent.putExtra(EXTRA_PRODUCT_ID2, productId2);
        intent.putExtra(EXTRA_IS_PRODUCT_MINE,isMine);
        context.startService(intent);
    }

    // TODO: Customize helper method
    public static void startActionLeaveProduct(Context context, String productIdSerialized,String userId) {
        Intent intent = new Intent(context, DeviceService.class);
        intent.setAction(ACTION_LEAVE_PRODUCT);
        intent.putExtra(EXTRA_PRODUCT_ID2, productIdSerialized);
        intent.putExtra(EXTRA_USER_ID2,userId);
        context.startService(intent);
    }

    /**
     * Handle delete product in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDeleteProduct(String productSerialized,boolean isProductMine) {
        DataProduct obj = DataProduct.getFromJson(productSerialized);
        DataApiResult<Boolean> res = null;
        //if (isProductMine) {
            res = CloudEngine.getInstance().DeleteProduct(obj, false);
        //}
        //else {
        //    res = CloudEngine.getInstance().LeaveProduct(obj, false);
        //}

        if (res.Error == null && res.Result == true)
            PhoneEngine.getInstance().deleteProduct(obj.Id);
    }

    private void handleActionLeaveProduct(String productSerialized,String userId) {
        DataProduct obj = DataProduct.getFromJson(productSerialized);
        DataApiResult<Boolean> res = null;
        DataProduct dp = DataProduct.getFromJson(productSerialized);

        res = CloudEngine.getInstance().LeaveProduct(dp, userId,false);

        if (res.Error == null && res.Result == true)
            PhoneEngine.getInstance().deleteProduct(obj.Id);
    }

    public static void startActionHandleContactsFirst(Context context, int countryCode, String userId) {
        Intent intent = new Intent(context, DeviceService.class);
        intent.setAction(ACTION_HANDLE_CONTACTS_FIRST);

        intent.putExtra(EXTRA_COUNTRY_CODE, countryCode);
        intent.putExtra(EXTRA_USER_ID2, userId);
        context.startService(intent);
    }

    private void handleActionHandleContacts(int countryCode, String userId) {
        List<DataFriendContact> contacts = PhoneEngine.getInstance().readContactFromDevice();

        //by default all contacts are selected
        for (DataFriendContact cont : contacts)
            cont.setIsSelected(true);

        //todo - remove this to one call
        PhoneEngine.getInstance().writeContactsToProvider(contacts, null, countryCode);//integer country code
        handleFriendsUpdate(userId);
    }

    private void handleFriendsUpdate(String userId) {
        List<DataFriendContact> contacts;
        contacts = PhoneEngine.getInstance().readContactFromProvider(); //formated numbers

        DataGroupFriends df = new DataGroupFriends();
        df.Members = contacts;
        df.UserId = userId;


        //todo remove this
        CloudEngine.getInstance().setApplicationContext(this.getApplicationContext());
        CloudEngine.getInstance().UpdateFriends(df, true);
    }
}