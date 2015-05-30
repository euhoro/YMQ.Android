package com.ymarq.eu.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by eu on 1/14/2015.
 */
public class ProductsContract {

    public static final String CONTENT_AUTHORITY = "com.ymarq.eu.ymarq.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER ="User";
    public static final String PATH_PRODUCTS = "Products";

    public static final String PATH_SUBSCRIPTIONS ="Subscriptions";
    public static final String PATH_MESSAGES ="Messages";
    public static final String PATH_LOCATIONS ="Locations";

    public static final class MessageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGES;

        // Table name
        public static final String TABLE_NAME = "message";

        public static final String COLUMN_MESSAGE_ID2 = "message_id";
        public static final String COLUMN_MESSAGE_PRODUCT_ID2 = "message_product_id";
        public static final String COLUMN_MESSAGE_SENDER_ID2 = "message_sender_id";
        public static final String COLUMN_MESSAGE_CONTENT = "message_content";
        public static final String COLUMN_MESSAGE_TO_USER_ID = "message_to_user_id";
        public static final String COLUMN_MESSAGE_CREATE_DATE = "message_create_date";
        public static final String COLUMN_MESSAGE_PRODUCT_ID = "message_product_id1";
        public static final String COLUMN_MESSAGE_SENDER_ID = "message_sender_id1";
        public static final String COLUMN_MESSAGE_SENDER_NAME = "message_sender_name";

        public static Uri buildMessageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMessagesWithProductId2(String productId2) {
            return CONTENT_URI.buildUpon().appendPath(productId2).build();
        }

        public static Uri buildMessagesUserWithStartDate( //with sStartDate???
                                                          String productId2, long date) {
            return CONTENT_URI.buildUpon().appendPath(productId2)
                    .appendQueryParameter(COLUMN_MESSAGE_CREATE_DATE, String.valueOf(date)).build();
        }

        public static Uri buildMesssageProductWithMessageId2(String productId2,String messageId)  {
            return CONTENT_URI.buildUpon().appendPath(productId2)
                        .appendPath(messageId).build();
        }

        public static String getProductIdId2FromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getMessageId2FromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_MESSAGE_CREATE_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

        // To make it easy to query for the exact date, we normalize all dates that go into
        // the database to the start of the the Julian day at UTC.
        public static long normalizeDate(long startDate) {
            // normalize the start date to the beginning of the (UTC) day
            Time time = new Time();
            time.set(startDate);
            int julianDay = Time.getJulianDay(startDate, time.gmtoff);
            return time.setJulianDay(julianDay);
        }

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SubscriptionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSCRIPTIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIPTIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIPTIONS;

        // Table name
        public static final String TABLE_NAME = "subscription";

        public static final String COLUMN_SUBSCRIPTION_ID2 = "subscription_id";
        public static final String COLUMN_SUBSCRIPTION_USER_ID2 = "subscription_user_id";
        public static final String COLUMN_SUBSCRIPTION_USER_ID = "subscription_user_id1";
        public static final String COLUMN_SUBSCRIPTION_SEARCH_TEXT = "subscription_search_text";

        public static Uri buildSubscriptionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSubscriptionsWithUserId2(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }

        public static Uri buildSubscriptionUserWithSubscriptionId2(String userId, String subscritionId2)  {
            return CONTENT_URI.buildUpon().appendPath(userId)
                    .appendPath(subscritionId2).build();
        }

        public static String getUserId2FromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getSubscriptionsId2FromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        // Table name
        public static final String TABLE_NAME = "User";

        //columns
        public static final String COLUMN_USER_ID2 = "user_id";
        public static final String COLUMN_USER_EMAIL = "user_email";
        public static final String COLUMN_USER_NICKNAME = "user_nickname";
        public static final String COLUMN_USER_PHONE_COUNTRY = "user_phone_country";
        public static final String COLUMN_USER_PHONE_NUMBER = "user_phone_number";
        public static final String COLUMN_USER_PASSWORD = "user_password";
        public static final String COLUMN_USER_IS_ME = "user_is_me";
        public static final String COLUMN_USER_PHONE_NUMBER_FULL  = "user_phone_number_full";
        public static final String COLUMN_USER_IS_FRIEND = "user_is_friend";
        public static final String COLUMN_USER_IS_CONTACT = "user_is_contact";
        public static final String COLUMN_USER_REGISTRATION_ID = "RegistrationId";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // Table name
        public static final String TABLE_NAME = "Product";

        public static final String COLUMN_USER_ID2 = "user_id";
        public static final String COLUMN_USER_ID = "user_id1";
        public static final String COLUMN_PRODUCT_ID2 = "product_id";
        public static final String COLUMN_DESCRIPTION = "product_description";
        public static final String COLUMN_LOC_KEY = "product_location";
        public static final String COLUMN_HASHTAG = "product_hashtag";
        public static final String COLUMN_IMAGE_LINK = "product_image_link";
        public static final String COLUMN_IMAGE_LOCAL = "product_image_local";
        public static final String COLUMN_DATETEXT = "date";
        // Product id as returned by API, to identify the icon to be used
        public static final String COLUMN_PRODUCT_TYPE_ID = "product_type_id";
        public static final String COLUMN_PRODUCT_NOTIFICATIONS = "product_notifications";

        public static final String COLUMN_PRODUCT_GIVEAWAY = "product_giveaway";
        public static final String COLUMN_PRODUCT_NOTIFY_FRIENDS = "product_notify_friends";
        public static final String COLUMN_PRODUCT_NOTIFY_OTHERS = "product_notify_others";


        public static Uri buildProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildProductsWithUserId2(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }

        public static Uri buildProductsUserWithStartDate( //with sStartDate???
                String userId, long date) {
            return CONTENT_URI.buildUpon().appendPath(userId)
                    .appendQueryParameter(COLUMN_DATETEXT,String.valueOf(date)).build();
        }

        public static Uri buildProductsUserWithProductId2(String userId, String productId2)  {
            return CONTENT_URI.buildUpon().appendPath(userId)
                    .appendPath(productId2).build();
        }

        public static String getUserId2FromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getProductId2FromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATETEXT);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

        // To make it easy to query for the exact date, we normalize all dates that go into
        // the database to the start of the the Julian day at UTC.
        public static long normalizeDate(long startDate) {
            // normalize the start date to the beginning of the (UTC) day
            Time time = new Time();
            time.set(startDate);
            int julianDay = Time.getJulianDay(startDate, time.gmtoff);
            return time.setJulianDay(julianDay);
        }
    }

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATIONS;

        // Table name
        public static final String TABLE_NAME = "location";
        // The location setting string is what will be sent to openproductsmap
// as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        // Human readable location string, provided by the API. Because for styling,
// "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_CITY_NAME = "city_name";
        // In order to uniquely pinpoint the location on the map when we launch the
// map intent, we store the latitude and longitude as returned by openproductsmap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
    }
}