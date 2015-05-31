package com.ymarq.eu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eu on 1/14/2015.
 */
public class ProductsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 28;

    public static final String DATABASE_NAME = "ymarq.db";

    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold locations. A location consists of the string supplied in the
// location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " +  ProductsContract.UserEntry.TABLE_NAME + " (" +
                ProductsContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                ProductsContract.UserEntry.COLUMN_USER_ID2 + " TEXT , " +
                ProductsContract.UserEntry.COLUMN_USER_EMAIL + " TEXT , " +
                ProductsContract.UserEntry.COLUMN_USER_NICKNAME + " TEXT , " +
                ProductsContract.UserEntry.COLUMN_USER_PHONE_COUNTRY + " TEXT , " +
                ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER + " TEXT , " +
                ProductsContract.UserEntry.COLUMN_USER_IS_ME + " INTEGER NOT NULL, " +
                ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND + " INTEGER NOT NULL, " +
                ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT + " INTEGER NOT NULL, " +
                ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER_FULL + " TEXT NOT NULL, " +
                ProductsContract.UserEntry.COLUMN_USER_REGISTRATION_ID + " TEXT , " +
                ProductsContract.UserEntry.COLUMN_USER_PASSWORD + " TEXT " + ")";

        // Create a table to hold locations. A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " +  ProductsContract.LocationEntry.TABLE_NAME + " (" +
                ProductsContract.LocationEntry._ID + " INTEGER PRIMARY KEY," +
                ProductsContract.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                ProductsContract.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                ProductsContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                ProductsContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                "UNIQUE (" +  ProductsContract.LocationEntry.COLUMN_LOCATION_SETTING +") ON CONFLICT IGNORE"+
                " );";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductsContract.ProductEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for producs
                // for a certain date and all dates *following*, so the products data
                // should be sorted accordingly.
                ProductsContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2 + " TEXT UNIQUE NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_USER_ID2 + " TEXT NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_HASHTAG + " TEXT NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_IMAGE_LINK + " TEXT NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_IMAGE_LOCAL + " TEXT NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                ProductsContract.ProductEntry.COLUMN_PRODUCT_TYPE_ID + " INTEGER NOT NULL," +
                ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS + " INTEGER NOT NULL," +

                ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS + " INTEGER NOT NULL," +
                ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS + " INTEGER NOT NULL," +
                ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY + " INTEGER NOT NULL," +
                ProductsContract.ProductEntry.COLUMN_PRODUCT_SERVER_STATUS + " INTEGER NOT NULL," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ProductsContract.ProductEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                ProductsContract.LocationEntry.TABLE_NAME + " (" + ProductsContract.LocationEntry._ID + "), " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ProductsContract.ProductEntry.COLUMN_USER_ID2 + ") REFERENCES " +
                ProductsContract.UserEntry.TABLE_NAME + " (" + ProductsContract.UserEntry._ID + ")" +

                //", " +
                //// To assure the application have just one products entry per day
                //// per location, it's created a UNIQUE constraint with REPLACE strategy
                //" UNIQUE (" + ProductsContract.ProductEntry.COLUMN_DATETEXT + ", " +
                //ProductsContract.ProductEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE"+
                ");";

        // Create a table to hold users.
        final String SQL_CREATE_SUBSCRIPTION_TABLE = "CREATE TABLE " +  ProductsContract.SubscriptionEntry.TABLE_NAME + " (" +
                ProductsContract.SubscriptionEntry._ID + " INTEGER PRIMARY KEY," +
                ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2 + " TEXT UNIQUE NOT NULL, " +
                ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID2 + " TEXT TEXT NOT NULL, " +
                ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_SEARCH_TEXT + " TEXT TEXT NOT NULL, " +
                ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID + " TEXT TEXT NOT NULL, " +


                // Set up the User column as a foreign key to subscription table.
                //" FOREIGN KEY (" + ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID + ") REFERENCES " +
                //ProductsContract.UserEntry.TABLE_NAME + " (" + ProductsContract.UserEntry._ID + ") " +
                //")";

                " FOREIGN KEY (" + ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_USER_ID + ") REFERENCES " +
                ProductsContract.UserEntry.TABLE_NAME + " (" + ProductsContract.UserEntry._ID + "), " +
                " UNIQUE (" + ProductsContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_ID2 +" ) ON CONFLICT IGNORE);";

        // Create a table to hold users.
        final String SQL_CREATE_MESSAGE_TABLE = "CREATE TABLE " +  ProductsContract.MessageEntry.TABLE_NAME + " (" +
                ProductsContract.MessageEntry._ID + " INTEGER PRIMARY KEY, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_ID2 + " TEXT UNIQUE NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID2 + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID2 + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_CONTENT + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_TO_USER_ID + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_CREATE_DATE  + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID + " TEXT TEXT NOT NULL, " +
                ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_NAME + " TEXT TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ProductsContract.MessageEntry.COLUMN_MESSAGE_PRODUCT_ID + ") REFERENCES " +
                ProductsContract.ProductEntry.TABLE_NAME + " (" + ProductsContract.ProductEntry._ID + "), " +

                // Set up the User column as a foreign key to subscription table.
                " FOREIGN KEY (" + ProductsContract.MessageEntry.COLUMN_MESSAGE_SENDER_ID + ") REFERENCES " +
                ProductsContract.UserEntry.TABLE_NAME + " (" + ProductsContract.UserEntry._ID + ") " +
                        ")";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBSCRIPTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsContract.UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsContract.LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsContract.ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsContract.SubscriptionEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductsContract.MessageEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
