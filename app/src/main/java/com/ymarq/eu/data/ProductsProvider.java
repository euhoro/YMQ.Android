package com.ymarq.eu.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ProductsProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProductsDbHelper mOpenHelper;

    public static final int PRODUCTS = 100;
    public static final int PRODUCTS_WITH_USER_ID2 = 101;
    public static final int PRODUCTS_WITH_USER_ID2_AND_PRODUCT_ID2 = 102;
    public static final int USER = 300;

    public static final int LOCATION = 400;

    public static final int SUBSCRIOPTIONS = 500;
    public static final int SUBSCRIOPTIONS_WITH_USER_ID2 = 501;
    public static final int SUBSCRIOPTIONS_WITH_USER_ID2_AND_SUBSCRIOPTIONS_ID2 = 502;

    public static final int MESSAGES = 600;
    public static final int MESSAGES_WITH_PRODUCT_ID2 = 601;
    public static final int MESSAGES_WITH_PRODUCT_ID2_AND_MESSAGE_ID2 = 602;

    private static final SQLiteQueryBuilder sProductsByUserQueryBuilder;
    private static final SQLiteQueryBuilder sSubscriptionsByUserQueryBuilder2;
    private static final SQLiteQueryBuilder sMessagesByProductQueryBuilder2;

    static {
        sProductsByUserQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //product INNER JOIN location ON product.location_id = location._id
        sProductsByUserQueryBuilder.setTables(
                ProductsContract.ProductEntry.TABLE_NAME + " INNER JOIN " +
                        ProductsContract.UserEntry.TABLE_NAME +
                        " ON " + ProductsContract.ProductEntry.TABLE_NAME +
                        "." + ProductsContract.ProductEntry.COLUMN_USER_ID +
                        " = " + ProductsContract.UserEntry.TABLE_NAME +
                        "." + ProductsContract.UserEntry._ID);
    }

        static {

            sSubscriptionsByUserQueryBuilder2 = new SQLiteQueryBuilder();

            sSubscriptionsByUserQueryBuilder2.setTables(
                    ProductsContract.SubscriptionEntry.TABLE_NAME + " INNER JOIN " +
                            ProductsContract.UserEntry.TABLE_NAME +
                            " ON " + ProductsContract.SubscriptionEntry.TABLE_NAME +
                            "." + ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID +
                            " = " + ProductsContract.UserEntry.TABLE_NAME +
                            "." + ProductsContract.UserEntry._ID);
        }


    static{
        sMessagesByProductQueryBuilder2 = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //product INNER JOIN location ON product.location_id = location._id
        sMessagesByProductQueryBuilder2.setTables(
                ProductsContract.MessageEntry.TABLE_NAME + " INNER JOIN " +
                        ProductsContract.ProductEntry.TABLE_NAME +
                        " ON " + ProductsContract.MessageEntry.TABLE_NAME +
                        "." +ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID +
                        " = " + ProductsContract.ProductEntry.TABLE_NAME +
                        "." + ProductsContract.ProductEntry._ID);

        }

    //User.usrId2 = ?
    private static final String sUserId2Selection =
            ProductsContract.UserEntry.TABLE_NAME+
                    "." + ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ? ";

    private static final String sProductId2Selection =
            ProductsContract.ProductEntry.TABLE_NAME+
                    "." + ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " = ? ";

    //User.usrId2 = ? AND date >= ?
    private static final String sUserId2SelectionWithStartDateSelection =
            ProductsContract.UserEntry.TABLE_NAME+
                    "." + ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ? AND " +
                    ProductsContract.ProductEntry.COLUMN_DATETEXT + " >= ? ";

    //User.usrId2 = ? AND productId2 = ?
    private static final String sUserId2SelectionWithProductdId2Selection  =
            ProductsContract.UserEntry.TABLE_NAME +
                    "." + ProductsContract.UserEntry.COLUMN_USER_ID2 + " = ? AND " +
                    ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " = ? ";

    private Cursor getProductsByUserId2(Uri uri, String[] projection, String sortOrder) {
        String userId2 = ProductsContract.ProductEntry.getUserId2FromUri(uri);
        long startDate = ProductsContract.ProductEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sUserId2Selection;
            selectionArgs = new String[]{userId2};
        } else {
            selectionArgs = new String[]{userId2, Long.toString(startDate)};
            selection = sUserId2SelectionWithStartDateSelection;
        }

        return sProductsByUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getProductsByUserId2AndProductId(
            Uri uri, String[] projection, String sortOrder) {
        String userId2 = ProductsContract.ProductEntry.getUserId2FromUri(uri);
        String productId2 = ProductsContract.ProductEntry.getProductId2FromUri(uri);

        return sProductsByUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sUserId2SelectionWithProductdId2Selection,
                new String[]{userId2,productId2},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSubscriptionsByUserId2(Uri uri, String[] projection, String sortOrder) {
        String userId2 = ProductsContract.SubscriptionEntry.getUserId2FromUri(uri);
        //long startDate = ProductsContract.SubscriptionEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        //if (startDate == 0) {
            selection = sUserId2Selection;
            selectionArgs = new String[]{userId2};
        //} else {
        //    selectionArgs = new String[]{userId2, Long.toString(startDate)};
        //    selection = sUserId2SelectionWithStartDateSelection;
        //}

        return sSubscriptionsByUserQueryBuilder2.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMessagesByProduct2(Uri uri, String[] projection, String sortOrder) {
        String productId = ProductsContract.MessageEntry.getProductIdId2FromUri(uri);
        //long startDate = ProductsContract.SubscriptionEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        //if (startDate == 0) {
        selection = sProductId2Selection;
        selectionArgs = new String[]{productId};
        //} else {
        //    selectionArgs = new String[]{userId2, Long.toString(startDate)};
        //    selection = sUserId2SelectionWithStartDateSelection;
        //}

        return sMessagesByProductQueryBuilder2.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }



    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the product, product_WITH_LOCATION,product_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can product_item_small2 this by uncommenting the
        testUriMatcher product_item_small2 within TestUriMatcher.
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProductsContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ProductsContract.PATH_PRODUCTS, PRODUCTS);
        matcher.addURI(authority, ProductsContract.PATH_PRODUCTS + "/*", PRODUCTS_WITH_USER_ID2);
        matcher.addURI(authority, ProductsContract.PATH_PRODUCTS + "/*/*", PRODUCTS_WITH_USER_ID2_AND_PRODUCT_ID2);//last was #

        //new

        matcher.addURI(authority, ProductsContract.PATH_SUBSCRIPTIONS, SUBSCRIOPTIONS);
        matcher.addURI(authority, ProductsContract.PATH_SUBSCRIPTIONS + "/*", SUBSCRIOPTIONS_WITH_USER_ID2);
        matcher.addURI(authority, ProductsContract.PATH_SUBSCRIPTIONS + "/*/*", SUBSCRIOPTIONS_WITH_USER_ID2_AND_SUBSCRIOPTIONS_ID2);

        matcher.addURI(authority, ProductsContract.PATH_MESSAGES, MESSAGES);
        matcher.addURI(authority, ProductsContract.PATH_MESSAGES + "/*", MESSAGES_WITH_PRODUCT_ID2);
        matcher.addURI(authority, ProductsContract.PATH_MESSAGES + "/*/*", MESSAGES_WITH_PRODUCT_ID2_AND_MESSAGE_ID2);

        matcher.addURI(authority, ProductsContract.PATH_LOCATIONS, LOCATION);
        //todo .. add a location query and date query
        //end new

        matcher.addURI(authority, ProductsContract.PATH_USER, USER);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ProductsDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        product_item_small2 this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case PRODUCTS_WITH_USER_ID2_AND_PRODUCT_ID2:
                return ProductsContract.ProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCTS_WITH_USER_ID2:
                return ProductsContract.ProductEntry.CONTENT_TYPE;
            case PRODUCTS:
                return ProductsContract.ProductEntry.CONTENT_TYPE;
            case USER:
                return ProductsContract.UserEntry.CONTENT_TYPE;



            case SUBSCRIOPTIONS_WITH_USER_ID2_AND_SUBSCRIOPTIONS_ID2:
                return ProductsContract.SubscriptionEntry.CONTENT_ITEM_TYPE;
            case SUBSCRIOPTIONS_WITH_USER_ID2:
                return ProductsContract.SubscriptionEntry.CONTENT_TYPE;
            case SUBSCRIOPTIONS:
                return ProductsContract.SubscriptionEntry.CONTENT_TYPE;


            case MESSAGES_WITH_PRODUCT_ID2_AND_MESSAGE_ID2:
                return ProductsContract.MessageEntry.CONTENT_ITEM_TYPE;
            case MESSAGES_WITH_PRODUCT_ID2:
                return ProductsContract.MessageEntry.CONTENT_TYPE;
            case MESSAGES:
                return ProductsContract.MessageEntry.CONTENT_TYPE;



            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "product/*/*"
            case PRODUCTS_WITH_USER_ID2_AND_PRODUCT_ID2:
            {
                retCursor = getProductsByUserId2AndProductId(uri, projection, sortOrder);
                break;
            }
            case PRODUCTS_WITH_USER_ID2: {
                retCursor = getProductsByUserId2(uri, projection, sortOrder);
                break;
            }
            case PRODUCTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductsContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            case USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductsContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }





            case SUBSCRIOPTIONS_WITH_USER_ID2: {
                retCursor = getSubscriptionsByUserId2(uri, projection, sortOrder);
                break;
            }
            case SUBSCRIOPTIONS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductsContract.SubscriptionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }



            case MESSAGES_WITH_PRODUCT_ID2: {
                retCursor = getMessagesByProduct2(uri, projection, sortOrder);
                break;
            }
            case MESSAGES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductsContract.MessageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PRODUCTS: {
                normalizeDate(values);
                long _id = db.insert(ProductsContract.ProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ProductsContract.ProductEntry.buildProductUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER: {
                //normalizeDate(values);
                long _id = db.insert(ProductsContract.UserEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ProductsContract.UserEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }



            case SUBSCRIOPTIONS: {
                //normalizeDate(values);
                long _id = db.insert(ProductsContract.SubscriptionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ProductsContract.SubscriptionEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case MESSAGES: {
                //normalizeDate(values);
                long _id = db.insert(ProductsContract.MessageEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ProductsContract.MessageEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mOpenHelper.getReadableDatabase();

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case PRODUCTS:
                rowsDeleted = db.delete(
                        ProductsContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER:
                rowsDeleted = db.delete(
                        ProductsContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGES:
                rowsDeleted = db.delete(
                        ProductsContract.MessageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBSCRIOPTIONS:
                rowsDeleted = db.delete(
                        ProductsContract.SubscriptionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ProductsContract.ProductEntry.COLUMN_DATETEXT)) {
            long dateValue = values.getAsLong(ProductsContract.ProductEntry.COLUMN_DATETEXT);
            values.put(ProductsContract.ProductEntry.COLUMN_DATETEXT,ProductsContract.ProductEntry.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
        Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            int rowsUpdated;

            switch (match) {
                case PRODUCTS:
                    normalizeDate(values);
                    rowsUpdated = db.update(ProductsContract.ProductEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                case USER:
                    rowsUpdated = db.update(ProductsContract.UserEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(ProductsContract.ProductEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
            finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case USER:
                db.beginTransaction();
                int returnCount1 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ProductsContract.UserEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount1++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount1;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}