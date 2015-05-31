package com.ymarq.eu.ymarqdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.data.ProductsDbHelper;
import com.ymarq.eu.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by eu on 3/18/2015.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_USER_ID2 = "76971ea670ff89be";
    static final String TEST_SUBSCRIPTION_ID2 = "76971ea670ff89be_subscription";
    static final String TEST_SEARCH_TEXT = "MUTZAR";
    static final long TEST_DATE_BULK = 1419033600; //December 20th, 2014
    static final long TEST_DATE3 = 0;
    static final String TEST_PRODUCT_ID2 = "0c27f102-c312-4af3-9826-eebe6d47382a";  // December 20th, 2014
    static final String TEST_MESSAGE_ID = "0c27f102-c312-4af3-9826-eebe6d47382a_message";
    static final long TEST_LOCTION_ID2 = 999;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default product values for your database tests.
     */
    static ContentValues createProductValues(long userRowId) {
        String productDescription = "mazda 3";
        ContentValues values = new ContentValues();
        values.put(ProductsContract.ProductEntry.COLUMN_USER_ID,userRowId);
        values.put(ProductsContract.ProductEntry.COLUMN_USER_ID2,TEST_USER_ID2);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,TEST_PRODUCT_ID2);
        values.put(ProductsContract.ProductEntry.COLUMN_DESCRIPTION,productDescription);
        values.put(ProductsContract.ProductEntry.COLUMN_LOC_KEY,TEST_LOCTION_ID2);
        values.put(ProductsContract.ProductEntry.COLUMN_HASHTAG ,productDescription);
        values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK ,"");
        values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL ,"");
        values.put(ProductsContract.ProductEntry.COLUMN_DATETEXT ,0L);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,0);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS ,0);

        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS ,0);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS ,0);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY ,0);

        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_SERVER_STATUS ,0);

        return values;
    }

    public static ContentValues createUserValues() {
        ContentValues values = new ContentValues();
        // Test data we're going to insert into the DB to see if it works.
        String user_id = TEST_USER_ID2;
        String user_email = "eu@eu.com";
        String user_nickname = "eu_nick";
        String user_phone_country = "975";
        String user_phone_number = "545989827";
        String user_password = "989828";
        String user_registration = "123456";

        values.put(ProductsContract.UserEntry.COLUMN_USER_ID2, user_id);
        values.put(ProductsContract.UserEntry.COLUMN_USER_EMAIL, user_email);
        values.put(ProductsContract.UserEntry.COLUMN_USER_NICKNAME, user_nickname);
        values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_COUNTRY, user_phone_country);
        values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER, user_phone_number);
        values.put(ProductsContract.UserEntry.COLUMN_USER_IS_ME,1);
        values.put(ProductsContract.UserEntry.COLUMN_USER_PASSWORD, user_password);

        values.put(ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID, user_registration);

        values.put(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND, 0);
        values.put(ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT, 0);
        values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL, "+972545989828");
        return values;
    }


    public static ContentValues createSubscriptionValues(long userRowId) {
        ContentValues values = new ContentValues();

        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2, TEST_SUBSCRIPTION_ID2);
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_SEARCH_TEXT, TEST_SEARCH_TEXT);
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID, userRowId);
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2, TEST_USER_ID2);

        return values;
    }

    public static ContentValues createMesageValues(long productRowId,long userRowId) {
        ContentValues values = new ContentValues();

        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_ID2,"message1");
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2 , TEST_PRODUCT_ID2);
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID2, TEST_USER_ID2);
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_CONTENT , TEST_MESSAGE_ID);
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_TO_USER_ID , "");
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_CREATE_DATE , "");
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID , productRowId);
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID, userRowId);
        values.put(ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_NAME, "moshe");

        return values;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the ProductContract as well as the productDbHelper.
     */
    static long insertUserValues(Context context) {
        // insert our product_item_small2 records into the database
        ProductsDbHelper dbHelper = new ProductsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createUserValues();

        long userRowId;
        userRowId = db.insert(ProductsContract.UserEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert UserValues Values", userRowId != -1);

        return userRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to product_item_small2
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not product_item_small2 that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to product_item_small2 your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }


    //additional
    static long insertProductsValues(Context context) {

        long user = insertUserValues(context);

        ProductsDbHelper dbHelper = new ProductsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createProductValues(user);

        long productId;
        productId = db.insert(ProductsContract.ProductEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert UserValues Values", productId != -1);

        return productId;
    }

    static long insertSubscriptionValues(Context context) {

        long userId = insertUserValues(context);

        ProductsDbHelper dbHelper = new ProductsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSubscriptionValues(userId);

        long subscriptionId;
        subscriptionId = db.insert(ProductsContract.SubscriptionEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert subscriptionValues Values", subscriptionId != -1);

        return subscriptionId;
    }


    static long insertMessageValues(Context context) {

        long userId = insertUserValues(context);

        long productId = insertProductsValues(context);

        ProductsDbHelper dbHelper = new ProductsDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMesageValues(userId,productId);

        long userRowId;
        userRowId = db.insert(ProductsContract.MessageEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert UserValues Values", userRowId != -1);

        return userRowId;
    }
}