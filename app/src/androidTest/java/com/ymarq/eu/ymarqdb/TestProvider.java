package com.ymarq.eu.ymarqdb;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.data.ProductsDbHelper;
import com.ymarq.eu.data.ProductsProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ProductsContract.ProductEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                ProductsContract.UserEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                ProductsContract.SubscriptionEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                ProductsContract.MessageEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from products table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from user table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ProductsContract.SubscriptionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from subscription table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ProductsContract.MessageEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from message table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(ProductsContract.ProductEntry.TABLE_NAME, null, null);
        db.delete(ProductsContract.UserEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    // Since we want each product_item_small2 to start with a clean slate, run deleteAllRecords
    // in setUp (called by the product_item_small2 runner before each product_item_small2).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This product_item_small2 checks to make sure that the content provider is registered correctly.
        Students: Uncomment this product_item_small2 to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ProductsProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + ProductsContract.CONTENT_AUTHORITY,
                    providerInfo.authority, ProductsContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This product_item_small2 doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this product_item_small2 to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {

        String type = mContext.getContentResolver().getType(ProductsContract.ProductEntry.CONTENT_URI);
        assertEquals("Error: the ProductsEntry CONTENT_URI should return ProductsEntry.CONTENT_TYPE",
                ProductsContract.ProductEntry.CONTENT_TYPE, type);

        String testUserId2 = "76971ea670ff89be";
        type = mContext.getContentResolver().getType(
                ProductsContract.ProductEntry.buildProductsWithUserId2(testUserId2));

        assertEquals("Error: the ProductsEntry CONTENT_URI with location should return ProductsEntry.CONTENT_TYPE",
                ProductsContract.ProductEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(ProductsContract.UserEntry.CONTENT_URI);
        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
                ProductsContract.UserEntry.CONTENT_TYPE, type);

        String productId2 = "0c27f102-c312-4af3-9826-eebe6d47382a"; // December 21st, 2014
        type = mContext.getContentResolver().getType(
                ProductsContract.ProductEntry.buildProductsUserWithProductId2(testUserId2, productId2));
        assertEquals("Error: the ProductsEntry CONTENT_URI with location and date should return ProductsEntry.CONTENT_ITEM_TYPE",
                ProductsContract.ProductEntry.CONTENT_ITEM_TYPE, type);


        type = mContext.getContentResolver().getType(ProductsContract.SubscriptionEntry.CONTENT_URI);
        assertEquals("Error: the ProductsEntry CONTENT_URI should return ProductsEntry.CONTENT_TYPE",
                ProductsContract.SubscriptionEntry.CONTENT_TYPE, type);

        testUserId2 = "76971ea670ff89be";
        type = mContext.getContentResolver().getType(
                ProductsContract.SubscriptionEntry.buildSubscriptionsWithUserId2(testUserId2));

        assertEquals("Error: the subscription CONTENT_URI with location should return ProductsEntry.CONTENT_TYPE",
                ProductsContract.SubscriptionEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(ProductsContract.MessageEntry.CONTENT_URI);
        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
                ProductsContract.MessageEntry.CONTENT_TYPE, type);

        productId2 = "0c27f102-c312-4af3-9826-eebe6d47382a"; // December 21st, 2014
        type = mContext.getContentResolver().getType(
                ProductsContract.MessageEntry.buildMesssageProductWithMessageId2(testUserId2, productId2));
        assertEquals("Error: the ProductsEntry CONTENT_URI with location and date should return ProductsEntry.CONTENT_ITEM_TYPE",
                ProductsContract.MessageEntry.CONTENT_ITEM_TYPE, type);

    }


    /*
        This product_item_small2 uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this product_item_small2 to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicProductsQuery() {
        // insert our product_item_small2 records into the database
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createUserValues();
        long locationRowId = TestUtilities.insertUserValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createProductValues(locationRowId);

        long weatherRowId = db.insert(ProductsContract.ProductEntry.TABLE_NAME, null, weatherValues);
        assertTrue("Unable to Insert ProductEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicProductsQuery", weatherCursor, weatherValues);
    }


    public void testBasicSubscriptionQuery() {
        // insert our product_item_small2 records into the database
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createUserValues();
        long userRow = TestUtilities.insertUserValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues subscriptionValues = TestUtilities.createSubscriptionValues(userRow);

        long subscriptoinRowId = db.insert(ProductsContract.SubscriptionEntry.TABLE_NAME, null, subscriptionValues);
        assertTrue("Unable to Insert SubscriptionEntry into the Database", subscriptoinRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor subscristionCursor = mContext.getContentResolver().query(
                ProductsContract.SubscriptionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", subscristionCursor, subscriptionValues);
    }



    public void testsBasicMessagesQuery() {
        // insert our product_item_small2 records into the database
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createUserValues();
        long userRow = TestUtilities.insertUserValues(mContext);

        ContentValues productValues = TestUtilities.createProductValues(userRow);
        long productROw = TestUtilities.insertProductsValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues messageValues = TestUtilities.createMesageValues(productROw, userRow);

        long messageRowId = db.insert(ProductsContract.MessageEntry.TABLE_NAME, null, messageValues);
        assertTrue("Unable to Insert MessageEntry into the Database", messageRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor subscristionCursor = mContext.getContentResolver().query(
                ProductsContract.MessageEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", subscristionCursor, messageValues);
    }

    public void testCopyDataBase()
    {
        File f=new File("/data/data/com.ymarq.eu.ymarq/databases/ymarq.db");
        FileInputStream fis=null;
        FileOutputStream fos=null;

        try
        {
            fis=new FileInputStream(f);
            fos=new FileOutputStream("/mnt/sdcard/db_dump.db");
            while(true)
            {
                int i=fis.read();
                if(i!=-1)
                {fos.write(i);}
                else
                {break;}
            }
            fos.flush();
            //Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
        }
        finally
        {
            try
            {
                fos.close();
                fis.close();
            }
            catch(IOException ioe)
            {}
        }

    }

    /*
        This product_item_small2 uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this product_item_small2 to see if your location queries are
        performing correctly.
     */
    public void testBasicUserQueries() {
        // insert our product_item_small2 records into the database
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createUserValues();
        long locationRowId = TestUtilities.insertUserValues(mContext);

        // Test the basic content provider query
        Cursor locationCursor = mContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only product_item_small2 this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    locationCursor.getNotificationUri(), ProductsContract.UserEntry.CONTENT_URI);
        }
    }

    /*
        This product_item_small2 uses the provider to insert and then update the data. Uncomment this product_item_small2 to
        see if your update location is functioning correctly.
     */
    public void testUpdateUser() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createUserValues();

        Uri locationUri = mContext.getContentResolver().
                insert(ProductsContract.UserEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(ProductsContract.UserEntry._ID, locationRowId);
        updatedValues.put(ProductsContract.UserEntry.COLUMN_USER_NICKNAME, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(ProductsContract.UserEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                ProductsContract.UserEntry.CONTENT_URI, updatedValues, ProductsContract.UserEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                null,   // projection
                ProductsContract.UserEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    /*
    This product_item_small2 uses the provider to insert and then update the data. Uncomment this product_item_small2 to
    see if your update location is functioning correctly.
 */
    //public void testUpdateProduct() {
    //    // Create a new map of values, where column names are the keys
    //    ContentValues values = TestUtilities.createProductValues();
//
    //    Uri locationUri = mContext.getContentResolver().
    //            insert(ProductsContract.ProductEntry.CONTENT_URI, values);
    //    long locationRowId = ContentUris.parseId(locationUri);
//
    //    // Verify we got a row back.
    //    assertTrue(locationRowId != -1);
    //    Log.d(LOG_TAG, "New row id: " + locationRowId);
//
    //    ContentValues updatedValues = new ContentValues(values);
    //    updatedValues.put(ProductsContract.UserEntry._ID, locationRowId);
    //    updatedValues.put(ProductsContract.UserEntry.COLUMN_USER_NICKNAME, "Santa's Village");
//
    //    // Create a cursor with observer to make sure that the content provider is notifying
    //    // the observers as expected
    //    Cursor locationCursor = mContext.getContentResolver().query(ProductsContract.UserEntry.CONTENT_URI, null, null, null, null);
//
    //    TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
    //    locationCursor.registerContentObserver(tco);
//
    //    int count = mContext.getContentResolver().update(
    //            ProductsContract.UserEntry.CONTENT_URI, updatedValues, ProductsContract.UserEntry._ID + "= ?",
    //            new String[] { Long.toString(locationRowId)});
    //    assertEquals(count, 1);
//
    //    // Test to make sure our observer is called.  If not, we throw an assertion.
    //    //
    //    // Students: If your code is failing here, it means that your content provider
    //    // isn't calling getContext().getContentResolver().notifyChange(uri, null);
    //    tco.waitForNotificationOrFail();
//
    //    locationCursor.unregisterContentObserver(tco);
    //    locationCursor.close();
//
    //    // A cursor is your primary interface to the query results.
    //    Cursor cursor = mContext.getContentResolver().query(
    //            ProductsContract.UserEntry.CONTENT_URI,
    //            null,   // projection
    //            ProductsContract.UserEntry._ID + " = " + locationRowId,
    //            null,   // Values for the "where" clause
    //            null    // sort order
    //    );
//
    //    TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
    //            cursor, updatedValues);
//
    //    cursor.close();
    //}

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this product_item_small2 after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this product_item_small2 can be used.
 public void testInsertReadProvider() {
     ContentValues testValues = TestUtilities.createUserValues();

     // Register a content observer for our insert.  This time, directly with the content resolver
     TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
     mContext.getContentResolver().registerContentObserver(ProductsContract.UserEntry.CONTENT_URI, true, tco);
     Uri locationUri = mContext.getContentResolver().insert(ProductsContract.UserEntry.CONTENT_URI, testValues);

     // Did our content observer get called?  Students:  If this fails, your insert location
     // isn't calling getContext().getContentResolver().notifyChange(uri, null);
     tco.waitForNotificationOrFail();
     mContext.getContentResolver().unregisterContentObserver(tco);

     long locationRowId = ContentUris.parseId(locationUri);

     // Verify we got a row back.
     assertTrue(locationRowId != -1);

     // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
     // the round trip.

     // A cursor is your primary interface to the query results.
     Cursor cursor = mContext.getContentResolver().query(
             ProductsContract.UserEntry.CONTENT_URI,
             null, // leaving "columns" null just returns all the columns.
             null, // cols for "where" clause
             null, // values for "where" clause
             null  // sort order
     );

     TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
             cursor, testValues);

     // Fantastic.  Now that we have a location, add some products!
     ContentValues productsValues = TestUtilities.createProductValues(locationRowId);
     // The TestContentObserver is a one-shot class
     tco = TestUtilities.getTestContentObserver();

     mContext.getContentResolver().registerContentObserver(ProductsContract.ProductEntry.CONTENT_URI, true, tco);

     Uri productsInsertUri = mContext.getContentResolver()
             .insert(ProductsContract.ProductEntry.CONTENT_URI, productsValues);
     assertTrue(productsInsertUri != null);

     // Did our content observer get called?  Students:  If this fails, your insert products
     // in your ContentProvider isn't calling
     // getContext().getContentResolver().notifyChange(uri, null);
     tco.waitForNotificationOrFail();
     mContext.getContentResolver().unregisterContentObserver(tco);

     // A cursor is your primary interface to the query results.
     Cursor productsCursor = mContext.getContentResolver().query(
             ProductsContract.ProductEntry.CONTENT_URI,  // Table to Query
             null, // leaving "columns" null just returns all the columns.
             null, // cols for "where" clause
             null, // values for "where" clause
             null // columns to group by
     );

     TestUtilities.validateCursor("testInsertReadProvider. Error validating productsEntry insert.",
             productsCursor, productsValues);

     // Add the location values in with the weather data so that we can make
     // sure that the join worked and we actually get all the values back
     productsValues.putAll(testValues);

     // Get the joined Weather and Location data
     productsCursor = mContext.getContentResolver().query(
             ProductsContract.ProductEntry.buildProductsWithUserId2(TestUtilities.TEST_USER_ID2),
             null, // leaving "columns" null just returns all the columns.
             null, // cols for "where" clause
             null, // values for "where" clause
             null  // sort order
     );
     TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined products and Location Data.",
             productsCursor, productsValues);

     // Get the joined products and Location data with a start date
     productsCursor = mContext.getContentResolver().query(
             ProductsContract.ProductEntry.buildProductsUserWithStartDate(
                     TestUtilities.TEST_USER_ID2, TestUtilities.TEST_DATE3),
             null, // leaving "columns" null just returns all the columns.
             null, // cols for "where" clause
             null, // values for "where" clause
             null  // sort order
     );
     TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined products and Location Data with start date.",
             productsCursor, productsValues);

     // Get the joined Weather data for a specific date
     productsCursor = mContext.getContentResolver().query(
             ProductsContract.ProductEntry.buildProductsUserWithProductId2(TestUtilities.TEST_USER_ID2, TestUtilities.TEST_PRODUCT_ID2),
             null,
             null,
             null,
             null
     );
     TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
             productsCursor, productsValues);
 }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this product_item_small2 after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this product_item_small2 can be used.
    public void xxxxtestDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProductsContract.UserEntry.CONTENT_URI, true, locationObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProductsContract.ProductEntry.CONTENT_URI, true, weatherObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        locationObserver.waitForNotificationOrFail();
        weatherObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(locationObserver);
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertProductsValues(long userRowId) {
        long currentTestDate = TestUtilities.TEST_DATE_BULK;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {

            //TestUtilities.createProductValues();
            String productDescription = "mazda 3";
            ContentValues values = new ContentValues();
            values.put(ProductsContract.ProductEntry.COLUMN_USER_ID,userRowId);
            values.put(ProductsContract.ProductEntry.COLUMN_USER_ID2,String.valueOf(userRowId));
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,"17676"+String.valueOf(i));
            values.put(ProductsContract.ProductEntry.COLUMN_DESCRIPTION,productDescription);
            values.put(ProductsContract.ProductEntry.COLUMN_LOC_KEY,TestUtilities.TEST_LOCTION_ID2);
            values.put(ProductsContract.ProductEntry.COLUMN_HASHTAG ,productDescription);
            values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK ,"");
            values.put(ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL ,"");
            values.put(ProductsContract.ProductEntry.COLUMN_DATETEXT ,currentTestDate);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID ,0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS ,0);

            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS ,0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS ,0);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY ,0);

            returnContentValues[i] = values;
        }
        return returnContentValues;
    }

    private void AtestBulkInsert2() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createUserValues();
        Uri locationUri = mContext.getContentResolver().insert(ProductsContract.UserEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertProductsValues(locationRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProductsContract.ProductEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ProductsContract.ProductEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause,
                null
                //ProductsContract.ProductEntry.COLUMN_DATETEXT+ " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createUserValues();
        Uri locationUri = mContext.getContentResolver().insert(ProductsContract.UserEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ProductsContract.UserEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather. In fact, we only implement BulkInsert for weather
        // entries. With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertProductsValues(locationRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProductsContract.ProductEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ProductsContract.ProductEntry.CONTENT_URI, bulkInsertContentValues);

        // Students: If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ProductsContract.ProductEntry.COLUMN_DATETEXT + " ASC" // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
