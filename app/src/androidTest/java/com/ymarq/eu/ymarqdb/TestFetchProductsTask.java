package com.ymarq.eu.ymarqdb;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataUser;

import java.util.List;

public class TestFetchProductsTask extends AndroidTestCase{
    public static final String ADD_USER_ID2 = "Sunnydale, CA";
    public static final String ADD_USER_EMAIL = "";
    public static final String ADD_USER_NICKNAME = "Sunnydale";
    public static final String ADD_USER_PHONE_COUNTRY = "";
    public static final String ADD_USER_PHONE_NUMBER = "";
    public static final String ADD_USER_PASSWORD = "";
    public static final int ADD_USER_IS_ME = (false) ? 1 : 0;
    public static final String ADD_USER_PHONE_NUMBER_FULL  = "";
    public static final int ADD_USER_IS_FRIEND = (false) ? 1 : 0;


    public void testFetchTask(){
        //FetchProductsTask fwt = new FetchProductsTask(getContext(), null);
        PhoneEngine phoneEngine = PhoneEngine.getInstance();
        phoneEngine.setApplicationContext(getContext().getApplicationContext());

        try {
            getContext().getContentResolver().delete(ProductsContract.ProductEntry.CONTENT_URI,null,null);

            //DataApiResult<List<DataProduct>> products = fwt.execute("76971ea670ff89be").get();

            //maybe access through device so you can download data first
            DataApiResult<List<DataProduct>> products = phoneEngine.getProductsDataById2("76971ea670ff89be");//,false);

            // Get the joined product and Location data
            Cursor productsCursor = mContext.getContentResolver().query(
                    ProductsContract.ProductEntry.buildProductsWithUserId2(TestUtilities.TEST_USER_ID2),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );

            assertTrue("Error: number of products differes in content provider",
                    productsCursor.getCount() == products.getResult().size());

        }catch(Exception ex)
        {
            String s = ex.toString();
        }
    }

    @TargetApi(11)
    public void testAddUser() {
        // start from a clean state
        getContext().getContentResolver().delete(ProductsContract.UserEntry.CONTENT_URI,
                ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ?",
                new String[]{ADD_USER_ID2});

        //FetchProductsTask fwt = new FetchProductsTask(getContext(), null);
        //long userId = fwt.addUser(ADD_USER_ID2, ADD_USER_NICKNAME,
        //        false, false,"",  "0","0","",false);

        PhoneEngine phoneEngine = PhoneEngine.getInstance();
        phoneEngine.setApplicationContext(getContext().getApplicationContext());
        long userId = phoneEngine.addUserAsync(new DataUser(ADD_USER_ID2,"", ADD_USER_NICKNAME,""));



        // does addLocation return a valid record ID?
        assertFalse("Error: addLocation returned an invalid ID on insert",
                userId == -1);

        // product_item_small2 all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor locationCursor = getContext().getContentResolver().query(
                    ProductsContract.UserEntry.CONTENT_URI,
                    new String[]{
                            ProductsContract.UserEntry._ID,
                            ProductsContract.UserEntry.COLUMN_USER_ID2,
                            ProductsContract.UserEntry.COLUMN_USER_EMAIL,
                            ProductsContract.UserEntry.COLUMN_USER_NICKNAME,
                            ProductsContract.UserEntry.COLUMN_USER_PHONE_COUNTRY,
                            ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER,
                            ProductsContract.UserEntry.COLUMN_USER_IS_ME,
                            ProductsContract.UserEntry.COLUMN_USER_PASSWORD,
                            ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND,
                            ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT,
                            ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID,
                            ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL
                    },
                    ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ?",
                    new String[]{ADD_USER_ID2},
                    null);

            // these match the indices of the projection
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value" +
                        "from addLocation", locationCursor.getLong(0), userId);
                assertEquals("Error: the queried value of location setting is incorrect",
                        locationCursor.getString(1), ADD_USER_ID2);
                //assertEquals("Error: the queried value of location city is incorrect",
                //        locationCursor.getString(2), COLUMN_USER_EMAIL);
                //assertEquals("Error: the queried value of latitude is incorrect",
                //        locationCursor.getDouble(3), ADD_LOCATION_LAT);
                //assertEquals("Error: the queried value of longitude is incorrect",
                //        locationCursor.getDouble(4), ADD_LOCATION_LON);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a location query",
                    locationCursor.moveToNext());

            // add the location again
            //long newUserId = fwt.addUser(ADD_USER_ID2,ADD_USER_NICKNAME,
            //        false, false,"",  "0","0","",false);
            long newUserId = phoneEngine.addUserAsync(new DataUser(ADD_USER_ID2,"", ADD_USER_NICKNAME,""));



            assertEquals("Error: inserting a location again should return the same ID",
                    userId, newUserId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(ProductsContract.UserEntry.CONTENT_URI,
                ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ?",
                new String[]{ADD_USER_ID2});

        // clean up the product_item_small2 so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(ProductsContract.UserEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
