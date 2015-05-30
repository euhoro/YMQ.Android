package com.ymarq.eu.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ymarq.eu.common.DataUrlContent;
import com.ymarq.eu.common.IAuthenticationService;
import com.ymarq.eu.common.IConfigurationService;
import com.ymarq.eu.common.IImageService;
import com.ymarq.eu.common.IMessagingService;
import com.ymarq.eu.common.IOnContactsReceived;
import com.ymarq.eu.common.IOnGcmKeyReceived;
import com.ymarq.eu.common.IOnImageReceived;
import com.ymarq.eu.common.IOnMessagesReceived;
import com.ymarq.eu.common.IOnNotificationsReceived;
import com.ymarq.eu.common.IOnProductsReceived;
import com.ymarq.eu.common.IOnSubscriptionsReceived;
import com.ymarq.eu.common.IOnUserReceived;
import com.ymarq.eu.common.IProductService;
import com.ymarq.eu.common.ResultArrayTypes;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataExeption2;
import com.ymarq.eu.entities.DataFriendContact;
import com.ymarq.eu.entities.DataGroupFriends;
import com.ymarq.eu.entities.DataGroupId;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataNotifications;
import com.ymarq.eu.entities.DataNotificationsModel;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataProductId;
import com.ymarq.eu.entities.DataSearchId;
import com.ymarq.eu.entities.DataSubscription;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.entities.DataUserId;
import com.ymarq.eu.entities.DataUserIdDate;
import com.ymarq.eu.utilities.UrlHelper;
import com.ymarq.eu.utilities.YMQConst;
import com.ymarq.eu.ymarq.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

//import com.ymarq.eu.entities.DataFriendContact;
//import com.ymarq.eu.entities.DataGroupFriends;

/**
 * Created by eu on 12/23/2014.
 */
public class CloudEngine implements IAuthenticationService, IProductService, IMessagingService, IImageService
        //, IGcmService
{
    public final String ERROR_CLOUD_ENGINE_CLIENT =  "Android Error: ";

    ObjectMapper mObjectMapper = new ObjectMapper();

    public boolean isBusy() {
        return IsBusy;
    }

    public void setBusy(boolean isBusy) {
        this.IsBusy = isBusy;
    }

    public boolean IsBusy;

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        //it should be set only one time
        if (this.applicationContext == null)
            this.applicationContext = applicationContext;
    }

    public Context applicationContext;

    private IConfigurationService mConfigurationManager;

    public String getBaseUrl() {
        return BaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        BaseUrl = baseUrl;
    }

    private String BaseUrl = "http://ymarq.azurewebsites.net";//"http://10.0.0.3:9090";//
    // Implement single tone

    private static CloudEngine Instance ;

    //GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();

    private IOnSubscriptionsReceived subscriptionsReceivedListener;
    private IOnProductsReceived productsReceivedListener;
    private IOnMessagesReceived messagesReceivedListener;
    private IOnNotificationsReceived notificationsReceivedListener;
    private IOnContactsReceived contactsReceivedListener;
    private IOnUserReceived userReceivedListener;
    private IOnImageReceived imageReceivedListener;
    private IOnGcmKeyReceived gcmKeyReceivedListener;

    public IOnGcmKeyReceived getGcmKeyReceivedListener() {
        return gcmKeyReceivedListener;
    }

    public void setGcmKeyReceivedListener(IOnGcmKeyReceived gcmKeyReceivedListener) {
        this.gcmKeyReceivedListener = gcmKeyReceivedListener;
    }

    public IOnContactsReceived getContactsReceivedListener() {
        return contactsReceivedListener;
    }

    public void setContactsReceivedListener(IOnContactsReceived contactsReceivedListener) {
        this.contactsReceivedListener = contactsReceivedListener;
    }

    public IOnUserReceived getUserReceivedListener() {
        return this.userReceivedListener;
    }

    public void setUserReceivedListener(IOnUserReceived userReceivedListener) {
        this.userReceivedListener = userReceivedListener;
    }

    public IOnNotificationsReceived getNotificationsReceived() {
        return notificationsReceivedListener;
    }

    public void setNotificationsReceived(IOnNotificationsReceived notificationsReceivedListener) {
        this.notificationsReceivedListener = notificationsReceivedListener;
    }

    public IOnImageReceived getImageReceivedListener() {
        return imageReceivedListener;
    }

    public void setImageReceivedListener(IOnImageReceived imageReceivedListener) {
        this.imageReceivedListener = imageReceivedListener;
    }

    public  IOnSubscriptionsReceived getSubscriptionsReceivedListener() {
        return subscriptionsReceivedListener;
    }

    public void setSubscriptionsReceivedListener(IOnSubscriptionsReceived listener) {
        this.subscriptionsReceivedListener = listener;
    }

    public  IOnProductsReceived getProductsReceivedListener() {
        return productsReceivedListener;
    }

    public void setProductsReceivedListener(IOnProductsReceived listener) {
        this.productsReceivedListener = listener;
    }

    public  IOnMessagesReceived getMessagesReceivedListener() {
        return messagesReceivedListener;
    }

    public void setMessagesReceivedListener(IOnMessagesReceived listener) {
        this.messagesReceivedListener = listener;
    }

    public static CloudEngine getInstance()
    {
        if (Instance == null)
        {
            Instance = new CloudEngine();
        }
        return Instance;
    }

    private CloudEngine(){}

    /// <summary>
    /// Tries to login User. If User is not exist returns null
    /// </summary>
    public DataApiResult<DataUser> Login(String userId,boolean async) {
        DataApiResult<DataUser> result1 = null;
        String result="";
        try {
            DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_LOGIN, new DataUserId(userId), ResultArrayTypes.None);
            if (!async){
            result = getDataSync(dataUrlContent);

                result1 =  mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<DataUser>>() {
                        });
            }
            else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch (Exception ex)
        {
            result1 = new DataApiResult<DataUser>(null, getErrorFromServerString(result,ex));
        }
        return result1;
    }

    public DataApiResult<DataUser> LoginLogon(DataUser dataUser,boolean async2)
    {
        DataApiResult<DataUser> resultUser = null;
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_LOGIN, new DataUserId(dataUser.Id), ResultArrayTypes.OneUser);
        GenericPostResultTask genericPostTask = new GenericPostResultTask();
        String result ="";

        try {
            //if (async != false)
            {

                dataUrlContent.ExpectedArrayResult =  ResultArrayTypes.None;

                result = genericPostTask.execute(dataUrlContent).get();
                resultUser = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<DataUser>>() {
                        });
                //
                if (resultUser.Result == null || resultUser.Result.RegistrationId == null || !resultUser.Result.RegistrationId.equals(dataUser.RegistrationId)) {
                    dataUrlContent.ExpectedArrayResult = ResultArrayTypes.OneUser;
                    resultUser = Logon(dataUser, async2);
                }
                else if (resultUser.Result!=null &&
                        (!resultUser.Result.Email.equals(dataUser.Email)||
                        !resultUser.Result.Name.equals( dataUser.Name) ||
                        !resultUser.Result.Phone.equals(dataUser.Phone) ) )
                {//not the same data
                    if (userReceivedListener != null)
                        userReceivedListener.fireOnOneUserReceived(resultUser);

                }
                else {
                    //this is a workaround so the login progress bar will stop
                    if (userReceivedListener != null)
                        userReceivedListener.fireOnOneUserReceived(resultUser);
                }
            }
           //else
           //{
           //    result = genericPostTask.execute(dataUrlContent).get();
           //    resultUser = mObjectMapper.readValue(result,
           //            new TypeReference<DataApiResult<DataUser>>() {
           //            });
           //    //GetGcmRegistrationKeyTask getGcmRegistrationKeyTask = new GetGcmRegistrationKeyTask();
           //    //DataApiResult<String> resKey = getGcmRegistrationKeyTask.execute("").get();
           //    //dataUser.RegistrationId = resKey.Result;

           //    //this condition is duplicated
           //    if (resultUser.Result == null || resultUser.Result.RegistrationId == null || !resultUser.Result.RegistrationId.equals(dataUser.RegistrationId))
           //        resultUser = Logon(dataUser, async);
           //}
        }
        catch (Exception ex){
            resultUser = new DataApiResult<DataUser>(null, getErrorFromServerString(result,ex));
            if (userReceivedListener != null)
                userReceivedListener.fireOnOneUserReceived(null);
        }

        return resultUser;
    }

    /// <summary>
    /// User Logon
    /// </summary>
    public DataApiResult<DataUser> Logon(DataUser dataUser,boolean async)
    {
        DataApiResult<DataUser> dataUserResult = null;
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_LOGON,dataUser,ResultArrayTypes.OneUser);
        String result = "";
        try{
            if (!async) {
                result = getDataSync(dataUrlContent);
                dataUserResult =  mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<DataUser>>() {
                        });
            }
            else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch (Exception ex)
        {
            dataUserResult = new DataApiResult<DataUser>(null, getErrorFromServerString(result,ex));
        }
        return dataUserResult;
    }

    /// <summary>
    /// Publish new sell message
    /// </summary>
    public DataApiResult<Boolean> SendMessage(DataMessage message,boolean async)
    {
        DataApiResult<Boolean> result1 = null;
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_SEND_MESSAGE,message,ResultArrayTypes.None);
        String result = "";
        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                //todo parse the DataApiResult<VOID>
                result1 = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<Boolean>>() { } );
            } else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch (Exception ex)
        {
            result1 = new DataApiResult<Boolean>(false,getErrorFromServerString(result,ex));
        }
        return result1;
    }

    /// <summary>
    /// Returns all new messages
    /// </summary>
    public DataApiResult<List<DataMessage>> GetMessages(UUID productId,boolean async)
    {
        DataApiResult<List<DataMessage>> messagesResult = null;
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_MESSAGES, new DataProductId(productId.toString()), ResultArrayTypes.Messages);
        String result = "";
        try {
            if(!async) {
                result = getDataSync(dataUrlContent);
                messagesResult =
                        mObjectMapper.readValue(result,
                                new TypeReference<DataApiResult<List<DataMessage>>>() {
                                });
            }
            else{
                GenericPostResultTask genericPostResultTask = new GenericPostResultTask();
                genericPostResultTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex){
            messagesResult = new DataApiResult<List<DataMessage>>(null, getErrorFromServerString(result,ex));
        }
        return messagesResult;
    }

    public DataApiResult<List<DataMessage>> GetMessagesByDate(UUID productId,Date from,boolean async)
    {
        //String result = null;
        //try {
        //    DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_MESSAGES_BY_DATE,new DataProductId(productId.toString(),from),ResultArrayTypes.Messages);
        //    if (!async) {
        //         result = getDataSync(dataUrlContent);
        //        //return DataMessage.getMessagesFromJson(result);
        //        //todo this is no longer implemented - please implement if necesary
        //    }
        //}
        //catch(Exception ex){
        //    displayErrorSync2(ex.toString());
        //}
        return null;
    }

    public DataApiResult<DataNotificationsModel> GetNotificationsByUserDate(String userId,Date from,boolean async)
        {
            DataApiResult<DataNotificationsModel> notificationsResult = null;
            DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_NOTIFICATIONS_BY_DATE,new DataUserIdDate(userId,from),ResultArrayTypes.Notifications);
            String result = "";
        try {
            if(!async) {
                result = getDataSync(dataUrlContent);
                notificationsResult =
                        mObjectMapper.readValue(result,
                                new TypeReference<DataApiResult<DataNotificationsModel>>() {
                                });
            }
            else
            {
                GenericPostResultTask genericPostResultTask = new GenericPostResultTask();
                genericPostResultTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex){
            notificationsResult = new  DataApiResult<DataNotificationsModel>(null, getErrorFromServerString(result,ex));
        }
        return notificationsResult;
    }

    public DataApiResult<List<DataFriendContact>> GetFriendsStatus(DataGroupFriends groupFriendsInput,boolean async) {
        DataApiResult<List<DataFriendContact>> resultFriends = null;
        String result = "";
        String listString = "";
        DataGroupId groupFriends3 = new DataGroupId();
        groupFriends3.userid = groupFriendsInput.UserId;

        for (DataFriendContact s : groupFriendsInput.Members) {
            listString += s.PhoneNumber + ",";
        }
        if (listString.endsWith(","))
            listString = listString.substring(0, listString.length() - 1);

        groupFriends3.phonenumbers = listString;

        //

        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_FRIENDS_STATUS, groupFriends3, ResultArrayTypes.Contacts);
        //dataUrlContent.setUrl("http://ymarq.azurewebsites.net/api/users/GetFriendsStatus");

        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                resultFriends = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<List<DataFriendContact>>>() {
                        });

            } else {
                GenericPostResultTask genericPostResultTask = new GenericPostResultTask();
                genericPostResultTask.execute(dataUrlContent);
            }
        } catch (Exception ex) {
            resultFriends = new DataApiResult<List<DataFriendContact>>(null, getErrorFromServerString(result, ex));
        }
        return resultFriends;
    }

    public DataApiResult<Boolean> UpdateFriends(DataGroupFriends groupFriendsInput,boolean async) {
        String result = "";
        String listString = "";
        DataGroupId groupFriends3 = new DataGroupId();
        groupFriends3.userid = groupFriendsInput.UserId;

        for (DataFriendContact s : groupFriendsInput.Members) {
            listString += s.PhoneNumber + ",";
        }
        if (listString.endsWith(","))
            listString = listString.substring(0, listString.length() - 1);

        groupFriends3.phonenumbers = listString;

        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_UPDATE_FRIENDS, groupFriends3, ResultArrayTypes.
                None);

        if(!async) {
            String res = getDataSync(dataUrlContent);
            return new DataApiResult<>(true,null);
        }
        else{
            GenericPostResultTask genericPostResultTask = new GenericPostResultTask();
            genericPostResultTask.execute(dataUrlContent);
            return new DataApiResult<>(true,null);
        }
    }

    /// <summary>
    /// Publishers Product by
    /// </summary>
    public DataApiResult<DataProduct> PublishProduct(DataProduct dataProduct,boolean async)
    {
        String result ="";
        DataApiResult<DataProduct> resultProduct = null;
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_PUBLISH_PRODUCT,dataProduct,ResultArrayTypes.OneProduct);
        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                resultProduct = mObjectMapper.readValue(result,
                                new TypeReference<DataApiResult<DataProduct>>() { } );

            }
            else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex)
        {
            resultProduct = new  DataApiResult<DataProduct>(null, getErrorFromServerString(result, ex));
        }
        return  resultProduct;
    }


    /// <summary>
    /// Upload erro
    /// </summary>
    public DataApiResult<Boolean> UploadError(String dataExeption2,boolean async)
    {
        DataExeption2 de = DataExeption2.getFromJson(dataExeption2);

        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_UPLOAD_ERROR, de,ResultArrayTypes.None);

        return getBooleanDataApiResult(async, dataUrlContent);
    }

    private String getErrorFromServerString(String result, Exception ex) {
        displayErrorSync4(result);
        //Toast.makeText(applicationContext, result + ex.toString(), Toast.LENGTH_LONG).show();
        return ERROR_CLOUD_ENGINE_CLIENT +" >> Server Result " + result +" >> " + ex.toString();
    }

    /// <summary>
    /// Get Product by publisherId
    /// </summary>
    public DataApiResult<List<DataProduct>> GetProducts(String publisherId,boolean async) {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_PRODUCTS, new DataUserId(publisherId), ResultArrayTypes.Products);
        DataApiResult<List<DataProduct>> resultProducts = null;
        String result = "";

        try {
            if (!async) {

                result = getDataSync(dataUrlContent);
                resultProducts = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<List<DataProduct>>>() { } );
            } else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex){
            resultProducts = new DataApiResult<List<DataProduct>>(null, getErrorFromServerString(result,ex));
        }
        return resultProducts;
    }


    /// <summary>
    /// Get Product by publisherId
    /// </summary>
    public DataApiResult<List<DataProduct>> GetTopProducts(String userId,boolean async) {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_TOP_BUYER_PRODUCTS, new DataUserId(userId), ResultArrayTypes.Products);
        DataApiResult<List<DataProduct>> resultProducts = null;
        String result = "";

        try {
            if (!async) {

                result = getDataSync(dataUrlContent);
                resultProducts = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<List<DataProduct>>>() { } );
            } else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex){
            resultProducts = new DataApiResult<List<DataProduct>>(null, getErrorFromServerString(result,ex));
        }
        return resultProducts;
    }

    /// <summary>
    /// Delete published Product
    /// </summary>
    /// <param name="productId"></param>
    public DataApiResult<Boolean> DeleteProduct(DataProduct dataProduct,boolean async)
    {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_DELETE_PRODUCT,dataProduct,ResultArrayTypes.None);

        return getBooleanDataApiResult(async, dataUrlContent);
    }

    /// <summary>
    /// Leave subcribed/friend Product
    /// </summary>
    /// <param name="productId"></param>
    public DataApiResult<Boolean> LeaveProduct(DataProduct dataProduct,String userId,boolean async)
    {
        DataProductId dp = new DataProductId(dataProduct.Id,userId);

        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_LEAVE_PRODUCT,dp,ResultArrayTypes.None);

        return getBooleanDataApiResult(async, dataUrlContent);
    }

    /// <summary>
    /// Delete published subsription
    /// </summary>
    /// <param name="subscriptionid"></param>
    public DataApiResult<Boolean> DeleteSubscription(DataSubscription dataSubscription,boolean async)
    {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_DELETE_SUBSCRIPTION,dataSubscription,ResultArrayTypes.None);
        return getBooleanDataApiResult(async, dataUrlContent);
    }

    private DataApiResult<Boolean> getBooleanDataApiResult(boolean async, DataUrlContent dataUrlContent) {
        DataApiResult<Boolean> resultVoid1 = null;
        String result = "";
        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                //todo - read result
                //resultVoid1 = new DataApiResult<Boolean>(true, getErrorFromServerString(result,ex));
                resultVoid1 = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<Boolean>>() { } );
            } else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex){
            resultVoid1 = new DataApiResult<Boolean>(false, getErrorFromServerString(result,ex));
        }
        return resultVoid1;
    }

    /// <summary>
    /// Add new subscription
    /// </summary>
    /// <param name="subscription"></param>
    public DataApiResult<DataSubscription> Subscribe(DataSubscription subscription,boolean async) {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_POST_SUBSCRIPTION,subscription,ResultArrayTypes.OneSubscription);
        DataApiResult<DataSubscription> resultSubscription = null;
        String result = "";
        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                resultSubscription = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<DataSubscription>>() { } );

            }
            else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        }
        catch(Exception ex)
        {
            resultSubscription = new  DataApiResult<DataSubscription>(null, getErrorFromServerString(result,ex));
        }
        return  resultSubscription;
    }

    /// <summary>
    /// Returns all subscriptions of given User
    /// </summary>
    /// <param name="userId"></param>
    /// <returns></returns>
    public DataApiResult<List<DataSubscription>> GetSubscriptions (String userId,boolean async) {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_SUBSCRIPTIONS, new DataUserId(userId), ResultArrayTypes.Subscriptions);
        DataApiResult<List<DataSubscription>> resultSubscriptions = null;
        String result ="";
        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                resultSubscriptions = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<List<DataSubscription>>>() {
                        });

            } else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        } catch (Exception ex) {
            resultSubscriptions = new DataApiResult<List<DataSubscription>> (null,getErrorFromServerString(result,ex));
        }
        return resultSubscriptions;
    }

    /// <summary>
    /// Get Product by subscription
    /// </summary>
    public DataApiResult<List<DataProduct>> GetProductsBySubscription(String userId,String subscriptionId,boolean async) {
        DataUrlContent dataUrlContent = getDataUrlContent(YMQConst.L_API_GET_PRODUCTS_BY_SUBSCRIPTION, new DataSearchId(userId, subscriptionId), ResultArrayTypes.Products);
        DataApiResult<List<DataProduct>> resultProducts = null;
        String result ="";
        try {
            if (!async) {
                result = getDataSync(dataUrlContent);
                resultProducts = mObjectMapper.readValue(result,
                        new TypeReference<DataApiResult<List<DataProduct>>>() {
                        });

            } else {
                GenericPostResultTask genericPostTask = new GenericPostResultTask();
                genericPostTask.execute(dataUrlContent);
            }
        } catch (Exception ex) {
            resultProducts = new DataApiResult<List<DataProduct>>(null, getErrorFromServerString(result,ex));
        }
        return resultProducts;
    }

    /// <summary>
    /// Get notificationModel
    /// </summary>
    private List<DataNotifications>  GetNotificationsFromModel(DataNotificationsModel model)
    {
        List<DataNotifications> notifications = new ArrayList<DataNotifications>();
        for (DataNotifications data:model.SellerNotifications) {
            notifications.add(data);
            data.setNotificationType(0);
        }
        for (DataNotifications data:model.BuyerNotifications) {
            notifications.add(data);
            data.setNotificationType(1);
        }
        for (DataNotifications data:model.NewProducts) {
            notifications.add(data);
            data.setNotificationType(2);
        }
        return  notifications;
    }

    private DataUrlContent getDataUrlContent(String relativeUrl,Object content,ResultArrayTypes res) {
        String url = GetFullUrl(relativeUrl);
        DataUrlContent dataUrlContent = new DataUrlContent(url,content);
        dataUrlContent.ExpectedArrayResult = res;
        return dataUrlContent;
    }

    private String GetFullUrl(String apiUrl)
    {
        String freeTxt = "";
        if ( this.applicationContext !=null){

            Resources resources =  this.applicationContext.getResources();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.applicationContext);
            String key = this.applicationContext.getResources().getString(R.string.pref_baseurl_key);
            String def = resources.getString(R.string.pref_baseurl_default);

            freeTxt =  prefs.getString(key, def);
            if( freeTxt.equals(""))
            {
                key = resources.getString(R.string.pref_server_key);
                def = resources.getString(R.string.pref_server_azure);
                freeTxt = prefs.getString(key,def);
            }

            if(freeTxt.endsWith("/"))
            {
                freeTxt = freeTxt.substring(0,freeTxt.length()-1);
            }
            if(!freeTxt.startsWith("http://"))
            {
                freeTxt = "http://"+ freeTxt;
            }

            freeTxt = freeTxt+apiUrl;
        }
        else {
            freeTxt = BaseUrl + apiUrl;
        }
        return freeTxt;
    }

    private String getDataSync(DataUrlContent data) {
        String res = null;
        try {
            res = UrlHelper.postJsonDataObj(data.Url, data.Content);

            //Toast.makeText(applicationContext, data.Url, Toast.LENGTH_SHORT)
            //        .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void displayErrorSync4(String error)
    {
        //Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show();
        //this.applicationContext.runOnUiThread(show_toast);

        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
        mainHandler.post(show_toast);
    }

    private Runnable show_toast = new Runnable()
    {
        public void run()
        {

            //ConnectivityManager cm =
                   // (ConnectivityManager)applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            //requires network access
            //NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            //boolean isConnected = activeNetwork != null &&
            //        activeNetwork.isConnectedOrConnecting();
            //if(!isConnected)
            //{
                Toast.makeText(applicationContext, "Network error !", Toast.LENGTH_SHORT)
                        .show();
            //}
            //else {
            //    Toast.makeText(applicationContext, "Unexpected error !", Toast.LENGTH_SHORT)
            //            .show();
            //}

            //Toast.makeText(getActivity().getBaseContext(), "Uploading image...",
            //Toast.LENGTH_SHORT).show();
        }
    };

    private class GenericPostResultTask extends AsyncTask<DataUrlContent, Integer, String> {

    String mExpectedResults;
    private final String LOG_TAG = GenericPostResultTask.class.getSimpleName();

    protected String doInBackground(DataUrlContent... dataUrlContents)
    {
        IsBusy = true;
        mExpectedResults = dataUrlContents[0].ExpectedArrayResult.toString();
        String result = getDataSync(dataUrlContents[0]);
        return result;
    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(String result) {
        try {
            //Toast.makeText(applicationContext, "cloud:"+result, Toast.LENGTH_LONG).show();
            IsBusy = false;
            switch (mExpectedResults) {
                case "Messages":
                    DataApiResult<List<DataMessage>> messages =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<List<DataMessage>>>() { } );
                    if (messagesReceivedListener != null)
                        messagesReceivedListener.fireOnMessagessReceived(messages.Result);
                    break;
                case "Products":
                     DataApiResult<List<DataProduct>> products =
                     mObjectMapper.readValue(result,
                      new TypeReference<DataApiResult<List<DataProduct>>>() { } );
                    if (productsReceivedListener != null)
                        productsReceivedListener.fireOnProductsReceived(products.Result);

                    break;
                case "Subscriptions":
                    //List<DataSubscription> subscriptions = DataSubscription.
                    // scriptionsFromJson(result);//JsonHelper.getSubscriptionsDataFromJson(result);
                    DataApiResult<List<DataSubscription>> subscriptions =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<List<DataSubscription>>>() { } );

                    if (subscriptionsReceivedListener != null)
                        subscriptionsReceivedListener.fireOnSubscriptionsReceived(subscriptions.Result);
                    break;
                case "Notifications":
                    DataApiResult<DataNotificationsModel> notifications =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<DataNotificationsModel>>() { } );

                    List<DataNotifications> data = GetNotificationsFromModel(notifications.Result);
                    if (notificationsReceivedListener != null)
                        notificationsReceivedListener.fireOnNotificationsReceived(data);
                    break;
                case "Contacts":
                    DataApiResult<List<DataFriendContact>> contacts =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<List<DataFriendContact>>>() { } );

                    List<DataFriendContact> data1 = contacts.Result;
                    if (contactsReceivedListener != null)
                        contactsReceivedListener.fireOnContactsReceived(data1);
                    break;
                case "OneProduct":
                    DataApiResult<DataProduct> product =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<DataProduct>>() { } );

                    if (productsReceivedListener != null)
                        productsReceivedListener.fireOnOneProductReceived(product.getResult());
                    break;
                case "OneSubscription":
                    DataApiResult<DataSubscription> subscription =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<DataSubscription>>() { } );

                    if (subscriptionsReceivedListener != null)
                        subscriptionsReceivedListener.fireOnOneSubscriptionReceived(subscription.getResult());
                    break;
                case "OneUser":
                    DataApiResult<DataUser> user =
                            mObjectMapper.readValue(result,
                                    new TypeReference<DataApiResult<DataUser>>() { } );

                    if (userReceivedListener != null)
                        userReceivedListener.fireOnOneUserReceived(user);
                    break;
            }
        }
        catch (Exception e) {
            //Toast.makeText(applicationContext, mExpectedResults + result + e + mExpectedResults, Toast.LENGTH_LONG).show();
            //displayErrorSync2(result + e.toString());
            e.printStackTrace();
        }
    }
}

    @Override
    public DataApiResult<Bitmap> GetImage(DataProduct dataProduct,boolean async) {
        DataApiResult<Bitmap> imageResult = null;

        try {
            if (!async) {
                URL url = new URL(dataProduct.Image);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                imageResult = new DataApiResult<>(image,null);
            }
            else{
                GetBitmapTask getBitmapTask = new GetBitmapTask();
                getBitmapTask.execute(dataProduct);
            }
        }
        catch(Exception ex)
        {
            imageResult = new DataApiResult<>(null, getErrorFromServerString("error in bitmap",ex));
        }
        return imageResult;
    }

    private class GetBitmapTask extends AsyncTask<DataProduct, Integer, DataApiResult<Bitmap>> {

        String mExpectedResults;
        private final String LOG_TAG =GenericPostResultTask.class.getSimpleName();

        protected DataApiResult<Bitmap> doInBackground(DataProduct ... dataProducts)
        {
            DataApiResult<Bitmap> bResult = GetImage(dataProducts[0],false);
            return bResult;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(DataApiResult<Bitmap> result) {
            if (imageReceivedListener!=null)
                imageReceivedListener.fireOnImageReceived(result.Result);
        }
    }


    //@Override
    //public DataApiResult<String> GetGcmRegistrationKey(String appKey, boolean async) {
    //    DataApiResult<String> keyResult = new DataApiResult<>("",null);
//
    //    try {
    //        if (!async) {
    //            keyResult = getStringDataApiResult();
    //        }
    //        else{
    //            GetGcmRegistrationKeyTask getGcmRegistrationKeyTask = new GetGcmRegistrationKeyTask();
    //            getGcmRegistrationKeyTask.execute("");
    //        }
    //        return keyResult;
    //    }
    //    catch(Exception ex)
    //    {
    //        keyResult = new DataApiResult<>(null, getErrorFromServerString("error in gcm",ex));
    //    }
    //    return keyResult;
    //}

    //private DataApiResult<String> getStringDataApiResult() {
    //    DataApiResult<String> keyResult;
    //    String msg = "";
    //    try {
    //        if (gcm == null) {
    //            gcm = GoogleCloudMessaging.getInstance(applicationContext);
    //        }
//
    //        String SENDER_ID = "228482397663";
    //        String regid = gcm.register(SENDER_ID);
    //        msg = "Device registered, registration ID=" + regid;
    //        keyResult = new DataApiResult<>(regid,null);
    //    } catch (IOException ex) {
    //        msg = "Error :" + ex.getMessage();
    //        // If there is an error, don't just keep trying to register.
    //        // Require the user to click a button again, or perform
    //        // exponential back-off.
    //        keyResult = new DataApiResult<>(null,msg);
    //    }
    //    return keyResult;
    //}


  // private class GetGcmRegistrationKeyTask extends AsyncTask<String, Integer, DataApiResult<String>> {

  //     String mExpectedResults;
  //     private final String LOG_TAG =GetGcmRegistrationKeyTask.class.getSimpleName();

  //     protected DataApiResult<String> doInBackground(String ... params)
  //     {
  //         DataApiResult<String> bResult = GetGcmRegistrationKey(null,false);
  //         return bResult;
  //     }

  //     protected void onProgressUpdate(Integer... progress) {
  //         //setProgressPercent(progress[0]);
  //     }

  //     protected void onPostExecute(DataApiResult<String> result) {
  //         if (gcmKeyReceivedListener!=null)
  //             gcmKeyReceivedListener.fireOnGcmKeyReceived(result);
  //     }
  // }



}
