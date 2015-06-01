package com.ymarq.eu.business;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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
import com.ymarq.eu.products.FragmentBuyerOld;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by eu on 3/19/2015.
 */
public class PhoneEngine {
    private static PhoneEngine ourInstance = new PhoneEngine();

    public static PhoneEngine getInstance() {
        return ourInstance;
    }

    public void setApplicationContext(Context applicationContext) {
        //it should be set only one time
        if (this.applicationContext == null)
            this.applicationContext = applicationContext;
    }

    public Context applicationContext;

    private PhoneEngine() {
    }

    public String getContactNameByUserId(String userId)
    {
        return getContactNameByColumn(ProductsContract.UserEntry.COLUMN_USER_ID2,userId);
    }

    public String getContactNameByPhone(String phoneNumber)
    {
        return getContactNameByColumn(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL,phoneNumber);
    }

    private String getContactNameByColumn(String column,String parameter)
    {
        String result = "";

        String condition =  column + " = ?" ;
        String[] parameters = new String[]{parameter};

        String userData = "";
        Cursor userCursor = this.applicationContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.UserEntry._ID,
                        ProductsContract.UserEntry.COLUMN_USER_ID2,
                        ProductsContract.UserEntry.COLUMN_USER_EMAIL,
                        ProductsContract.UserEntry.COLUMN_USER_NICKNAME,
                        ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL},
                condition,
                parameters ,
                null);

        //if (userCursor.getCount()>0 && userCursor.moveToFirst())
        while (userCursor.moveToNext()){
            int nickIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_NICKNAME);
            result = userCursor.getString(nickIdIndex);
        }
        userCursor.close();
        return result;
    }

    public DataApiResult<DataNotificationsModel> getNotifications(String userIdMine)
    {
        DataApiResult<DataNotificationsModel> notificationsResult = new DataApiResult<>(new DataNotificationsModel(),"");
        notificationsResult.Result.BuyerNotifications = new ArrayList<>() ;
        notificationsResult.Result.SellerNotifications = new ArrayList<>() ;
        notificationsResult.Result.NewProducts = new ArrayList<>() ;

        DataProduct du =null;

        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.ProductEntry._ID,
                        ProductsContract.ProductEntry.COLUMN_USER_ID,
                        ProductsContract.ProductEntry.COLUMN_USER_ID2,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,
                        ProductsContract.ProductEntry.COLUMN_DESCRIPTION,
                        ProductsContract.ProductEntry.COLUMN_LOC_KEY,
                        ProductsContract.ProductEntry.COLUMN_HASHTAG,
                        ProductsContract.ProductEntry.COLUMN_IMAGE_LINK,
                        ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL,
                        ProductsContract.ProductEntry.COLUMN_DATETEXT,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS,

                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY
                },
                ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS + ">0",
                null,
                null);

        if (userCursor.moveToNext()) {
            int userIdIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_USER_ID2);
            String userId = userCursor.getString(userIdIndex);

            int productIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2);
            String productid = userCursor.getString(productIndex);

            int descriptionIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_DESCRIPTION);
            String description = userCursor.getString(descriptionIndex);

            int linkIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK);
            String link = userCursor.getString(linkIndex);

            int notificationsIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS);
            int notifications = userCursor.getInt(notificationsIndex);

            int friendsIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS);
            int friends = userCursor.getInt(notificationsIndex);

            int othersIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS);
            int others = userCursor.getInt(notificationsIndex);

            int giveAwayIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY);
            int giveAway = userCursor.getInt(notificationsIndex);

            du = new DataProduct(productid,userId,description,"","");

            du.GiveAway = giveAway==1 ? true : false;
            du.NotifyFriends = friends == 1 ? true : false;
            du.NotifyOthers = others == 1? true : false;

            du.setImage(link);
            du.setNumberOfNotifications(notifications);

            DataNotifications dataNoti = new DataNotifications();
            dataNoti.Product = du;

            DataApiResult<List<DataMessage>>  messages = getMessagesByProductId2(du.Id);
            dataNoti.Messages = messages.Result;

            if (userId.equals(userIdMine))//seller
            {
                notificationsResult.Result.SellerNotifications.add(dataNoti);
            }
            else //buyer
            {
                notificationsResult.Result.BuyerNotifications.add(dataNoti);
            }

        }
        userCursor.close();

        return notificationsResult;
    }

    public String getUserDataById2(String userId2,boolean isMe)
    {
        String isMeString = isMe? "1":"0";
        String condition =  ProductsContract.UserEntry.COLUMN_USER_IS_ME + " = ?" ;
        String[] parameters = new String[]{isMeString};

        if (userId2 !=null && userId2.length()>0) {
            //condition += " AND " + ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ?";
           //parameters = new String[]{isMeString,userId2};

            condition = ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ?";
            parameters = new String[]{userId2};
        }

        String userData = "";
        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.UserEntry._ID,
                        ProductsContract.UserEntry.COLUMN_USER_ID2,
                        ProductsContract.UserEntry.COLUMN_USER_EMAIL,
                        ProductsContract.UserEntry.COLUMN_USER_NICKNAME,
                        ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL,
                        ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID,
                        ProductsContract.UserEntry.COLUMN_USER_IS_ME},
                condition,
                parameters ,
                null);

        if (userCursor.getCount()==1 && userCursor.moveToFirst()) {
            int userIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_ID2);
            String userId = userCursor.getString(userIdIndex);

            int emailIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_EMAIL);
            String email = userCursor.getString(emailIdIndex);

            int nickIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_NICKNAME);
            String nick = userCursor.getString(nickIdIndex);

            int fullPhoneIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL);
            String fullPhone = userCursor.getString(fullPhoneIdIndex);

            int registrationIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID);
            String registration = userCursor.getString(registrationIndex);

            DataUser du = new DataUser(userId,email,nick,fullPhone);
            du.RegistrationId = registration;
            userData = du.getAsJSON();


        }
        userCursor.close();
        return userData;
    }


    public DataProduct getProductsDataByProductId(String productId2)
    {
        DataProduct du =null;

        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.ProductEntry._ID,
                        ProductsContract.ProductEntry.COLUMN_USER_ID,
                        ProductsContract.ProductEntry.COLUMN_USER_ID2,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,
                        ProductsContract.ProductEntry.COLUMN_DESCRIPTION,
                        ProductsContract.ProductEntry.COLUMN_LOC_KEY,
                        ProductsContract.ProductEntry.COLUMN_HASHTAG,
                        ProductsContract.ProductEntry.COLUMN_IMAGE_LINK,
                        ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL,
                        ProductsContract.ProductEntry.COLUMN_DATETEXT,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS,

                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY
                },
                ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " = ?",
                new String[]{productId2},
                null);

        if (userCursor.moveToNext()) {
            int userIdIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_USER_ID2);
            String userId = userCursor.getString(userIdIndex);

            int productIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2);
            String productid = userCursor.getString(productIndex);

            int descriptionIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_DESCRIPTION);
            String description = userCursor.getString(descriptionIndex);

            int linkIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK);
            String link = userCursor.getString(linkIndex);

            int notificationsIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS);
            int notifications = userCursor.getInt(notificationsIndex);

            int friendsIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS);
            int friends = userCursor.getInt(notificationsIndex);

            int othersIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS);
            int others = userCursor.getInt(notificationsIndex);

            int giveAwayIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY);
            int giveAway = userCursor.getInt(notificationsIndex);

            du = new DataProduct(productid,userId,description,"","");

            du.GiveAway = giveAway==1 ? true : false;
            du.NotifyFriends = friends == 1 ? true : false;
            du.NotifyOthers = others == 1? true : false;

            du.setImage(link);
            du.setNumberOfNotifications(notifications);
        }
        userCursor.close();

        return du;
    }


    public DataApiResult<List<DataMessage>> getMessagesByProductId2(String productId2)
    {
        List<DataMessage> messages = new ArrayList<DataMessage>();

        String userData = "";
        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.MessageEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.MessageEntry._ID,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_ID2 ,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID2 ,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_CONTENT ,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_TO_USER_ID ,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_CREATE_DATE,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID,
                        ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_NAME,

                },
                ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2 + " = ?",
                new String[]{productId2},
                null);

        while (userCursor.moveToNext()) {
            int userIdIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_ID2);
            String mesId2 = userCursor.getString(userIdIndex);

            int productIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2);
            String productid2 = userCursor.getString(productIndex);

            int descriptionIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID2);
            String senderId2 = userCursor.getString(descriptionIndex);

            int linkIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_CONTENT);
            String content = userCursor.getString(linkIndex);

            int notificationsIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_TO_USER_ID);
            String toUserId = userCursor.getString(notificationsIndex);

            int createDateIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_CREATE_DATE);
            String createDate = userCursor.getString(createDateIndex);

            int othersIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID);
            String productId = userCursor.getString(notificationsIndex);

            int giveAwayIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID);
            String senderId = userCursor.getString(notificationsIndex);

            int sendNameIndex = userCursor.getColumnIndex(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_NAME);
            String senderName= userCursor.getString(sendNameIndex);

            DataMessage du = new DataMessage();
            du.ToUserId = toUserId;
            du.Content = content;
            du.CreateDate = createDate;
            du.Id = mesId2;

            du.ProductId = productId2;
            du.SenderId = senderId2;
            du.ToUserId = toUserId;
            du.SenderName = senderName;

            messages.add(du);
        }
        userCursor.close();

        DataApiResult<List<DataMessage>> result = new DataApiResult<List<DataMessage>>(messages,"");

        return result;
    }


    public DataApiResult<List<DataSubscription>> getSubscriptionsDataById2(String userId2)
    {
        List<DataSubscription> products = new ArrayList<DataSubscription>();

        String userData = "";
        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.SubscriptionEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.SubscriptionEntry._ID,
                        ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2,
                        ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2,
                        ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID,
                        ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_SEARCH_TEXT

                },
                ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2+ " = ?",
                new String[]{userId2},
                null);

        while (userCursor.moveToNext()) {
            int userIdIndex = userCursor.getColumnIndex(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2);
            String userId = userCursor.getString(userIdIndex);

            int productIndex = userCursor.getColumnIndex(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2);
            String productid = userCursor.getString(productIndex);

            int descriptionIndex = userCursor.getColumnIndex(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID);
            String description = userCursor.getString(descriptionIndex);

            int linkIndex = userCursor.getColumnIndex(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_SEARCH_TEXT);
            String link = userCursor.getString(linkIndex);

            DataSubscription du = new DataSubscription();
            du.Id = UUID.fromString(productid);
            du.SearchText  =link ;
            du.UserId = userId;

            products.add(du);
        }
        userCursor.close();

        DataApiResult<List<DataSubscription>> result = new DataApiResult<List<DataSubscription>>(products,"");

        return result;
    }

    public DataApiResult<List<DataProduct>> getProductsDataById2(String userId2)
    {
        List<DataProduct> products = new ArrayList<DataProduct>();

        String userData = "";
        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.ProductEntry._ID,
                        ProductsContract.ProductEntry.COLUMN_USER_ID,
                        ProductsContract.ProductEntry.COLUMN_USER_ID2,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,
                        ProductsContract.ProductEntry.COLUMN_DESCRIPTION,
                        ProductsContract.ProductEntry.COLUMN_LOC_KEY,
                        ProductsContract.ProductEntry.COLUMN_HASHTAG,
                        ProductsContract.ProductEntry.COLUMN_IMAGE_LINK,
                        ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL,
                        ProductsContract.ProductEntry.COLUMN_DATETEXT,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS,

                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY
                },
                ProductsContract.ProductEntry.COLUMN_USER_ID2 + " = ?",
                new String[]{userId2},
                null);

        while (userCursor.moveToNext()) {
            int userIdIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_USER_ID2);
            String userId = userCursor.getString(userIdIndex);

            int productIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2);
            String productid = userCursor.getString(productIndex);

            int descriptionIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_DESCRIPTION);
            String description = userCursor.getString(descriptionIndex);

            int linkIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK);
            String link = userCursor.getString(linkIndex);

            int notificationsIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS);
            int notifications = userCursor.getInt(notificationsIndex);

            int friendsIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS);
            int friends = userCursor.getInt(notificationsIndex);

            int othersIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS);
            int others = userCursor.getInt(notificationsIndex);

            int giveAwayIndex = userCursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY);
            int giveAway = userCursor.getInt(notificationsIndex);

            DataProduct du = new DataProduct(productid,userId,description,"","");

            du.GiveAway = giveAway==1 ? true : false;
            du.NotifyFriends = friends == 1 ? true : false;
            du.NotifyOthers = others == 1? true : false;

            du.setImage(link);
            du.setNumberOfNotifications(notifications);
            userData = du.getAsJSON();

            products.add(du);
        }
        userCursor.close();

        DataApiResult<List<DataProduct>> result = new DataApiResult<List<DataProduct>>(products,"");

        return result;
    }

    public boolean updateProductNotification (String productId,int numberOfNotifications)
    {
        long productRowId = getProductIdByProductId2(productId);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(ProductsContract.ProductEntry._ID, productRowId);
        updatedValues.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS, numberOfNotifications);

        int count =applicationContext.getContentResolver().update(
                ProductsContract.ProductEntry.CONTENT_URI, updatedValues, ProductsContract.UserEntry._ID + "= ?",
                new String[]{Long.toString(productRowId)});

        return count>0;
    }

    public List<DataFriendContact> readContactFromDevice(){
        if (null== this)
            return null;

        Cursor phones = applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        List<DataFriendContact> mPhoneList = new ArrayList<>();

        while (phones.moveToNext())
        {
            DataFriendContact c = new DataFriendContact();
            c.setmContactPhoneId (phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

            String dataMime = (phones.getString(phones.getColumnIndex(ContactsContract.Data.MIMETYPE)));


            if (dataMime!=null && dataMime.equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                        .parseLong(c.getmContactPhoneId()));
                Uri uri= Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                c.setmIconUri(uri.toString());
            }

            c.Name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            c.PhoneNumberOriginal= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            mPhoneList.add(c);
        }

        phones.close();

        return mPhoneList;
    }

    public boolean deleteProduct(String productId)
    {
        String filter = ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " = ?";
        String[] param = new String[]{productId};

        int deleteCount = this.applicationContext.getContentResolver().delete(
                ProductsContract.ProductEntry.CONTENT_URI,
                filter,
                param
        );


        String filter2 = ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2 + " = ?";
        String[] param2 = new String[]{productId};

        int deleteCount2 = this.applicationContext.getContentResolver().delete(
                ProductsContract.MessageEntry.CONTENT_URI,
                filter2,
                param2
        );

        return deleteCount>0;
    }


    public List<DataFriendContact> readContactFromProvider(boolean onlySelected) {
        List<DataFriendContact> contacts = new ArrayList<>();

        String filter = null;
        String[] filterValues = null;

        if (onlySelected)
        {
            filter =   ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND + " = ?";
            filterValues = new String[]{"1"};
        }

        Cursor userCursor = applicationContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.UserEntry._ID,
                        ProductsContract.UserEntry.COLUMN_USER_ID2,
                        ProductsContract.UserEntry.COLUMN_USER_NICKNAME,
                        ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER,
                        ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL,
                        ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND,
                        ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT,
                },
                //ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT + " = ?",
                //new String[]{"1"},
                filter,
                filterValues,
                null);

        if (userCursor.getCount()>0 ) {
            while( userCursor.moveToNext())
            {
                int userIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_ID2);
                String userId = userCursor.getString(userIdIndex);

                int nickIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_NICKNAME);
                String nick = userCursor.getString(nickIdIndex);

                int phoneIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL);
                String phoneFullFormated = userCursor.getString(phoneIndex);

                int phoneIndex2 = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER);
                String phoneOriginal = userCursor.getString(phoneIndex2);

                int isFriendIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND);
                int isFriend = userCursor.getInt(isFriendIndex);

                int isContactIndex = userCursor.getColumnIndex(ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT);
                int isContact = userCursor.getInt(isContactIndex);

                int isContactIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry._ID);
                long contactId = userCursor.getLong(isContactIdIndex);

                if (isContact ==0)
                    continue;

                DataFriendContact c = new DataFriendContact();
                c.setOriginalUserId(userId);
                c.PhoneNumberOriginal=phoneOriginal;//original
                c.PhoneNumber = phoneFullFormated;//formated
                c.setName(nick);
                c.setIsSelected( isFriend == 1);
                c.setmPhoneContactId(contactId);
                contacts.add(c);
            }
        }
        userCursor.close();
        return  contacts;
    }

    public Boolean writeContactsToProvider(List<DataFriendContact> phoneList,Boolean isFriend,int countryCodeCurentUser)
    {
        addUsersContactsBulk(phoneList, isFriend, countryCodeCurentUser);
        return true;
    }

    public boolean updateAllUsersFriends3(boolean isFriend)
    {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND, isFriend ? 1 : 0);

        int count = applicationContext.getContentResolver().update(
                ProductsContract.UserEntry.CONTENT_URI, updatedValues, ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT + "= 1",
                null);

        return count>0;
    }

    public boolean updateUserFriend(long userIdInternal,boolean isFriend) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(ProductsContract.UserEntry._ID, userIdInternal);
        updatedValues.put(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND, isFriend ? 1 : 0);

        int count = this.applicationContext.getContentResolver().update(
                ProductsContract.UserEntry.CONTENT_URI, updatedValues, ProductsContract.UserEntry._ID + "= ?",
                new String[] { Long.toString(userIdInternal)});

        return count>0;
    }

    public boolean updateUsersProvider (String userId,List<DataFriendContact> contactsStatus )
    {
        List<DataFriendContact> contactsWithApp = new ArrayList<>();
        for(DataFriendContact friend : contactsStatus)
        {
            if (friend.OriginalUserId!= null && friend.OriginalUserId.length()!=0)
                contactsWithApp.add(friend);
        }

        int countAll = 0;
        for(DataFriendContact friend : contactsWithApp) {

            long locationRowId = getUserIdByUserId2(friend.PhoneNumber);

            ContentValues updatedValues = new ContentValues();

            updatedValues.put(ProductsContract.UserEntry.COLUMN_USER_ID2, friend.OriginalUserId);

            int count = this.applicationContext.getContentResolver().update(
                    ProductsContract.UserEntry.CONTENT_URI, updatedValues, ProductsContract.UserEntry._ID + "= ?",
                    new String[] { Long.toString(locationRowId)});
            countAll+=count;
        }
        return countAll>0;
    }

    public long getProductIdByProductId2(String productId2) {

        long rowId = -1;
        Cursor userCursor = this.applicationContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.ProductEntry._ID,
                        ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2},

                ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " = ?",
                new String[]{productId2},
                null);
        if (userCursor.moveToLast()) {
            int colid = userCursor.getColumnIndex(ProductsContract.ProductEntry._ID);
            rowId = userCursor.getLong(colid);
        }
        userCursor.close();
        return rowId;
    }

    public long getUserIdByUserId2(String phoneNumber)
    {
        long userid = -1;
        Cursor userCursor = this.applicationContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                new String[]{
                        ProductsContract.UserEntry._ID,
                        ProductsContract.UserEntry.COLUMN_USER_ID2},

                ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL + " = ?",
                new String[]{phoneNumber},
                null);
        if (userCursor.moveToLast()) {
            int colid = userCursor.getColumnIndex(ProductsContract.UserEntry._ID);
            userid = userCursor.getLong(colid);
        }
        userCursor.close();
        return userid;

    }

    public boolean updateServerContactsStatus(String userId,List<DataFriendContact> phoneList)
    {
        DataGroupFriends df = new DataGroupFriends();
        df.Members = new ArrayList<>();
        df.UserId = userId;

        for(DataFriendContact c:phoneList )
        {
            if (c.getIsSelected()) {
                DataFriendContact dfc = new DataFriendContact();
                dfc.PhoneNumber = c.PhoneNumber;
                df.Members.add(dfc);
            }
        }

        CloudEngine c = CloudEngine.getInstance();
        c.setApplicationContext(this.applicationContext);
        DataApiResult<List<DataFriendContact>> users = c.GetFriendsStatus(df, false);
        return true;
    }

    public static String normalizePhone3(DataFriendContact c,int ownerCountryId){
        String phoneFormated = "";
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        String countryAbreviation2 = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(ownerCountryId);

        Phonenumber.PhoneNumber numberProto = null;
        try {
            numberProto  = phoneUtil.parse(c.PhoneNumberOriginal, countryAbreviation2);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            return "UNKNOWN_FORMATED_NUMBER";
        }

        String formatedPhone1 = PhoneNumberUtil.getInstance().format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164.E164);
        return formatedPhone1;
    }

    public boolean addProductToProvider(DataProduct dp)
    {
        String contactName = "";
        if (dp.UserName!= null)
            contactName = dp.UserName;

        long userid = addUser(dp.UserId, dp.UserName, false, false, "", "", "", "", false, "");
        long longprodId = addProduct(userid, dp);
        return true;
    }

    public boolean addMessageToProvider(DataMessage message,String sellerUserId)
    {
        long userid = addUser(message.SenderId, "", false, false, "", "", "", "", false, "");

        DataProduct dp1 = new DataProduct();
        dp1.Id = message.ProductId;
        dp1.UserId = sellerUserId;

        long longprodId = addProduct(userid, dp1);

        addMessage(message,userid,longprodId);

        return true;
    }

    private long addMessage(DataMessage dm, long userId,long productId)
     {
         try {
             long messageInternalId;

             ContentValues values = new ContentValues();

             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_ID2, dm.Id);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2, dm.ProductId);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID2, dm.SenderId);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_CONTENT, dm.Content);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_TO_USER_ID, dm.ToUserId);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_CREATE_DATE, dm.CreateDate);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID, productId);
             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_NAME, dm.SenderName);

             values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID, userId);

             // Finally, insert location data into the database.
             Uri insertedUri = applicationContext.getContentResolver().insert(
                     ProductsContract.MessageEntry.CONTENT_URI,
                     values
             );

             // The resulting URI contains the ID for the row.  Extract the User from the Uri.
             messageInternalId = ContentUris.parseId(insertedUri);
             return messageInternalId;
         }
         catch(Exception ex) {
            String str = ex.toString();
             return  -1;
         }
    }

    public long addUserAsync(DataUser mUserData){
        ///FetchProductsTask ftw = new FetchProductsTask(applicationContext,null);
        return addUser(mUserData.Id, mUserData.Name, true, false, mUserData.getEmail(), "0","0","",false,"");
    }

     private final String LOG_TAG = PhoneEngine.class.getSimpleName();

    FragmentBuyerOld.ImageAdapterFrag mProductsAdapter;

    public boolean addUsersContactsBulk(List<DataFriendContact> phoneList,Boolean isFriend,int countryCodeCurentUser)
    {
        int contactsDeleted = applicationContext.getContentResolver().delete(
                ProductsContract.UserEntry.CONTENT_URI,
                ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT + " = ?",
                new String[]{"1"}
        );

        Vector<ContentValues> cVVector = new Vector<ContentValues>(phoneList.size());

        for(DataFriendContact dp : phoneList) {
            ContentValues values = new ContentValues();

            values.put(ProductsContract.UserEntry.COLUMN_USER_ID2, "");
            values.put(ProductsContract.UserEntry.COLUMN_USER_EMAIL, "");
            values.put(ProductsContract.UserEntry.COLUMN_USER_NICKNAME, dp.getName());
            values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_COUNTRY, toString().valueOf(countryCodeCurentUser));
            values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER, dp.PhoneNumberOriginal);//.replace("+",""));
            values.put(ProductsContract.UserEntry.COLUMN_USER_IS_ME,0);
            values.put(ProductsContract.UserEntry.COLUMN_USER_PASSWORD, "");
            values.put(ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID, "");

            boolean isFriendOverwrite = dp.getIsSelected();
            if (isFriend != null)
                isFriendOverwrite = isFriend.booleanValue();

            values.put(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND, isFriendOverwrite);
            values.put(ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT, true);
            values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL,PhoneEngine.normalizePhone3(dp, countryCodeCurentUser));

            cVVector.add(values);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = applicationContext.getContentResolver().bulkInsert(ProductsContract.UserEntry.CONTENT_URI, cvArray);
        }
        return inserted > 0;
    }

    public long addUser(String userId,
                            String nickName,
                            boolean isMe,
                            boolean isFriend,
                            String email,
                            String phoneCountryCode,
                            String phoneNumber,
                            String phoneFull,
                            boolean isContact,
                            String registrationId
        ) {
            long userInternalId;

            // First, check if the location with this city name exists in the db
            Cursor userCursor = applicationContext.getContentResolver().query(
                    ProductsContract.UserEntry.CONTENT_URI,
                    new String[]{ProductsContract.UserEntry._ID},
                    ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ?",
                    new String[]{userId},
                    null);

            if (userCursor.moveToFirst()) {
                int userIdIndex = userCursor.getColumnIndex(ProductsContract.UserEntry._ID);
                userInternalId = userCursor.getLong(userIdIndex);
            } else {
                // Now that the content provider is set up, inserting rows of data is pretty simple.
                // First create a ContentValues object to hold the data you want to insert.
                ContentValues values = new ContentValues();

                values.put(ProductsContract.UserEntry.COLUMN_USER_ID2, userId);
                values.put(ProductsContract.UserEntry.COLUMN_USER_EMAIL, email);
                values.put(ProductsContract.UserEntry.COLUMN_USER_NICKNAME, nickName);
                values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_COUNTRY, phoneCountryCode);
                values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER, phoneNumber);
                values.put(ProductsContract.UserEntry.COLUMN_USER_IS_ME,(isMe) ? 1 : 0);
                values.put(ProductsContract.UserEntry.COLUMN_USER_PASSWORD, "");

                values.put(ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID, registrationId);
                values.put(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND, (isFriend) ? 1 : 0);
                values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL, phoneFull);
                values.put(ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT, (isContact) ? 1 : 0);

                // Finally, insert location data into the database.
                Uri insertedUri = applicationContext.getContentResolver().insert(
                        ProductsContract.UserEntry.CONTENT_URI,
                        values
                );

                // The resulting URI contains the ID for the row.  Extract the User from the Uri.
                userInternalId = ContentUris.parseId(insertedUri);
            }

            userCursor.close();
            // Wait, that worked?  Yes!
            return userInternalId;
        }



    public long addProduct(long userRowId,
                           DataProduct dp ) {
        long prodInternalId;

        Uri productsForUserIdUri = ProductsContract.ProductEntry.buildProductsUserWithProductId2(
                dp.UserId,dp.Id);

        String[] columns = new String[]{ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,ProductsContract.UserEntry.TABLE_NAME + "." + ProductsContract.UserEntry._ID};
        Cursor prodCursor = applicationContext.getContentResolver().query(
                productsForUserIdUri,
                columns,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2+ " = ? " + ProductsContract.UserEntry.TABLE_NAME + "." + ProductsContract.UserEntry._ID + " = ? " ,
                new String[]{dp.Id,String.valueOf(userRowId)},
                null);

        // First, check if the location with this city name exists in the db
        //Cursor prodCursor = mContext.getContentResolver().query(
        //        ProductsContract.ProductEntry.CONTENT_URI,
        //        new String[]{ProductsContract.ProductEntry._ID},
        //        ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " = ? " + ProductsContract.ProductEntry.COLUMN_USER_ID+ " = ? ",
        //        new String[]{productId2,String.valueOf(userRowId)},
        //        null);
//
        if (prodCursor.moveToFirst()) {
            int prodIdIndex = prodCursor.getColumnIndex(ProductsContract.ProductEntry._ID);
            prodInternalId = prodCursor.getLong(prodIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues values = new ContentValues();

            values.put(ProductsContract.ProductEntry.COLUMN_USER_ID,userRowId);
            values.put(ProductsContract.ProductEntry.COLUMN_USER_ID2,dp.UserId);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,dp.Id);
            values.put(ProductsContract.ProductEntry.COLUMN_DESCRIPTION,dp.Description);
            values.put(ProductsContract.ProductEntry.COLUMN_LOC_KEY,0);
            values.put(ProductsContract.ProductEntry.COLUMN_HASHTAG ,"");
            values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK ,dp.Image);
            values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL ,"");
            values.put(ProductsContract.ProductEntry.COLUMN_DATETEXT ,1419033600);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS ,0);

            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS ,dp.NotifyFriends? 1:0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS,dp.NotifyOthers? 1:0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY ,dp.GiveAway ? 1:0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_SERVER_STATUS ,0);


            // Finally, insert location data into the database.
            Uri insertedUri = applicationContext.getContentResolver().insert(
                    ProductsContract.ProductEntry.CONTENT_URI,
                    values
            );

            // The resulting URI contains the ID for the row.  Extract the User from the Uri.
            prodInternalId = ContentUris.parseId(insertedUri);
        }

        prodCursor.close();
        // Wait, that worked?  Yes!
        return prodInternalId;
    }


    public boolean addProductToProviderOwner(DataUser mCurrentDataUser, DataProduct product) {

        //FetchProductsTask ftw = new FetchProductsTask(this, null);
        long userrow = PhoneEngine.getInstance().addUser(mCurrentDataUser.Id, mCurrentDataUser.Name, true, false, mCurrentDataUser.getEmail(), "0", "0", "", false, "");
        addProduct(userrow, product);

        return true;
    }

    public void insertSubscriptionDataInProvider(List<DataSubscription> products,
                                             String userId){

        try{
            //if User is not in the system add it
            long userRowId = addUser(userId, "", false, false,"", "0","0","",false,"");

            // Insert the new product information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(products.size());

            for(DataSubscription dp : products) {
                ContentValues values = new ContentValues();

                values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2,dp.Id.toString());
                values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2 ,dp.UserId);
                values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID,userRowId);
                values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_SEARCH_TEXT ,dp.SearchText);


                cVVector.add(values);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = applicationContext.getContentResolver().bulkInsert(ProductsContract.SubscriptionEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchProductsTask Complete. " + inserted + " Inserted");

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void insertProductsDataInProvider(List<DataProduct> products){

            try{
                // Insert the new product information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(products.size());

                for(DataProduct dp : products) {

                    long userRowId = addUser(dp.UserId, "", false, false, "", "0", "0", "", false, "");

                    ContentValues values = new ContentValues();

                    values.put(ProductsContract.ProductEntry.COLUMN_USER_ID,userRowId);
                    values.put(ProductsContract.ProductEntry.COLUMN_USER_ID2,dp.UserId);
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,dp.Id);
                    values.put(ProductsContract.ProductEntry.COLUMN_DESCRIPTION,dp.Description);
                    values.put(ProductsContract.ProductEntry.COLUMN_LOC_KEY,999);
                    values.put(ProductsContract.ProductEntry.COLUMN_HASHTAG ,"");
                    values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK ,dp.Image);
                    values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL ,"");
                    values.put(ProductsContract.ProductEntry.COLUMN_DATETEXT ,System.currentTimeMillis());
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,0);
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS ,0);

                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS ,dp.NotifyFriends? 1:0);
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS ,0);
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY ,1);//dp.GiveAway? 1:0);
                    values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_SERVER_STATUS ,0);//dp.GiveAway? 1:0);

                    cVVector.add(values);
                }

                int inserted = 0;
                // add to database
                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = applicationContext.getContentResolver().bulkInsert(ProductsContract.ProductEntry.CONTENT_URI, cvArray);
                }

                Log.d(LOG_TAG, "FetchProductsTask Complete. " + inserted + " Inserted");

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }


        //@Override
        protected DataApiResult<List<DataProduct>> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            String userId = params[0];

            DataApiResult<List<DataProduct>> result =  getProductsDataById2(userId);
            if (result == null || result.Result.size()==0) {
                result = CloudEngine.getInstance().GetProducts(userId, false);
                insertProductsDataInProvider(result.Result);//, userId);

                result =  getProductsDataById2(userId);//again from provider
            }
            return result;
        }

    public boolean deleteSusbcriptionByUserId(String userId)
    {
        String filter = ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID+ " = ?";
        String[] param = new String[]{userId};

        int deleteCount = applicationContext.getContentResolver().delete(
                ProductsContract.SubscriptionEntry.CONTENT_URI,
                filter,
                param
        );

        return deleteCount>0;
    }

    public boolean deleteProductsByUserId(String userId)
    {
        String filter = ProductsContract.ProductEntry.COLUMN_USER_ID2 + " = ?";
        String[] param = new String[]{userId};

        int deleteCount = applicationContext.getContentResolver().delete(
                ProductsContract.ProductEntry.CONTENT_URI,
                filter,
                param
        );

        return deleteCount>0;
    }
       //@Override
        protected void onPostExecute(DataApiResult<List<DataProduct>> result) {
            if (result != null && result.Result != null && mProductsAdapter != null) {
                mProductsAdapter.clear();
                mProductsAdapter.addAll(result.Result);
                mProductsAdapter.notifyDataSetChanged();
            }
        }
    //}
}
