package com.ymarq.eu.ymarqdb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.data.ProductsDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by eu on 1/14/2015.
 */
public class TestDB extends AndroidTestCase {

    private long mLocationId;//it will be assigned in readLocation
    private long mUserId;//it will be assigned in readUsers
    private long mProductId;//it will be assigned in readProducts


    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(ProductsDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ProductsDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    private void AtestInsertReadLocation() {
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues testValues = getLocationValues();

        //long locationRowId;
        mLocationId = db.insert(ProductsContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(mLocationId != -1);
        Log.d(LOG_TAG, "New row id: " + mLocationId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ProductsContract.LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        dbHelper.close();
    }

    private void AtestInsertReadUser() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues testValues = getUserValues();

        //long userId;
        mUserId = db.insert(ProductsContract.UserEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(mUserId != -1);
        Log.d(LOG_TAG, "New row id: " + mUserId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ProductsContract.UserEntry.TABLE_NAME,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        dbHelper.close();
    }

    private void AtestInsertReadProducts() {

        //testInsertReadLocation();
        //testInsertReadUser();

        // Test data we're going to insert into the DB to see if it works.
        String productDescription = "mazda 3";

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = getProductsValues();

        long productId;
        productId = db.insert(ProductsContract.ProductEntry.TABLE_NAME, null, testValues);
        mProductId = productId;

        // Verify we got a row back.
        assertTrue(productId != -1);
        Log.d(LOG_TAG, "New row id: " + productId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ProductsContract.ProductEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);
        dbHelper.close();
    }

    private void AtestInsertReadSubscription() {

        //testInsertReadLocation();
        //testInsertReadUser();
        //testInsertReadProducts();

        // Test data we're going to insert into the DB to see if it works.
        String searchSubscriptionText = "mazda 3";

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = getSubscriptionValues();

        long subscriptionId;
        subscriptionId = db.insert(ProductsContract.SubscriptionEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(subscriptionId != -1);
        Log.d(LOG_TAG, "New row id: " + subscriptionId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ProductsContract.SubscriptionEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);
        dbHelper.close();
    }

    public void testInsertReadMessage() {

        AtestInsertReadLocation();
        AtestInsertReadUser();
        AtestInsertReadProducts();

        // Test data we're going to insert into the DB to see if it works.
        String productDescription = "mazda 3";

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMesageValues(mProductId,mUserId);

        long messageId;
        messageId = db.insert(ProductsContract.MessageEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(messageId != -1);
        Log.d(LOG_TAG, "New row id: " + messageId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ProductsContract.MessageEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue("there is no record",cursor.moveToFirst());

        validateCursor(cursor, testValues);

        assertFalse("there is more than one record",cursor.moveToNext());

        dbHelper.close();
    }


    private ContentValues getUserValues() {
        //ContentValues values = new ContentValues();
        //// Test data we're going to insert into the DB to see if it works.
        //String user_id = "123456";
        //String user_email = "eu@eu.com";
        //String user_nickname = "eu_nick";
        //double user_phone_country = 972.05;
        //double user_phone_number = 4545.05;
        //String user_password = "12346";
////
        //values.put(ProductsContract.UserEntry.COLUMN_USER_ID2, user_id);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_EMAIL, user_email);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_NICKNAME, user_nickname);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_COUNTRY, user_phone_country);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER, user_phone_number);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_IS_ME,1);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_PASSWORD, user_password);
////
        //values.put(ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND, 0);
        //values.put(ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL, "+972545989828");
        //return values;
        return TestUtilities.createUserValues();
    }

    private ContentValues getProductsValues() {
        //String productDescription = "mazda 3";
        //ContentValues values = new ContentValues();
        //values.put(ProductsContract.ProductEntry.COLUMN_USER_ID,mUserId);
        //values.put(ProductsContract.ProductEntry.COLUMN_USER_ID2,"1234");
        //values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,"1234");
        //values.put(ProductsContract.ProductEntry.COLUMN_DESCRIPTION,productDescription);
        //values.put(ProductsContract.ProductEntry.COLUMN_LOC_KEY,mLocationId);
        //values.put(ProductsContract.ProductEntry.COLUMN_HASHTAG ,productDescription);
        //values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK ,"");
        //values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL ,"");
        //values.put(ProductsContract.ProductEntry.COLUMN_DATETEXT ,"");
        //values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,0);
        //return values;
        return TestUtilities.createProductValues(mUserId);
    }

    private ContentValues getSubscriptionValues() {
        String subscriptionText = "mazda 3";
        ContentValues values = new ContentValues();
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID,mUserId);
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_SEARCH_TEXT,subscriptionText);
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2,"1234");
        values.put(ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2,"1234");
        return values;
    }

    private ContentValues getLocationValues() {

        // Test data we're going to insert into the DB to see if it works.
        String testLocationSetting = "99705";
        String testCityName = "North Pole";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        ContentValues values = new ContentValues();
        values.put(ProductsContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(ProductsContract.LocationEntry.COLUMN_CITY_NAME, testCityName);
        values.put(ProductsContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(ProductsContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}


