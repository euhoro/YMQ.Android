package com.ymarq.eu.ymarqdb;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.data.ProductsProvider;

public class TestUriMatcher extends AndroidTestCase {
    private static final String USER_QUERY = "/76971 ea670ff89be";
    private static final String PRODUCT_QUERY = "/0c27f102-c312-4af3-9826-eebe6d47382a";

    private static final String PRODUCT_ID2 = "0c27f102-c312-4af3-9826-eebe6d47382a";  // December 20th, 2014
    private static final String SUBSRIPTION_ID2 = "0c27f102-c312-4af3-9826-eebe6d47382a";  // December 20th, 2014
    private static final String MESSAGE_ID2 = "0c27f102-c312-4af3-9826-eebe6d47382a";  // December 20th, 2014

    private static final long TEST_LOCATION_ID = 10L;

    public static final Uri TEST_PRODUCTS_DIR = ProductsContract.ProductEntry.CONTENT_URI;
    public static final Uri TEST_PRODUCTS_WITH_USERID2_DIR = ProductsContract.ProductEntry.buildProductsWithUserId2(USER_QUERY);
    public static final Uri TEST_PRODUCTS_WITH_USERID2_AND_PRODUCTID2_DIR = ProductsContract.ProductEntry.buildProductsUserWithProductId2(USER_QUERY, PRODUCT_ID2);

    public static final Uri TEST_USER_DIR = ProductsContract.UserEntry.CONTENT_URI;

    //newer

    public static final Uri TEST_SUBSCRIPTION_DIR = ProductsContract.SubscriptionEntry.CONTENT_URI;
    public static final Uri TEST_SUBSCRIPTION_WITH_USERID2_DIR = ProductsContract.SubscriptionEntry.buildSubscriptionsWithUserId2(USER_QUERY);
    public static final Uri TEST_SUBSCRIPTION_WITH_USERID2_AND_SUBSCRIPTIONID2_DIR = ProductsContract.SubscriptionEntry.buildSubscriptionUserWithSubscriptionId2(USER_QUERY, SUBSRIPTION_ID2);

    public static final Uri TEST_MESSAGES_DIR = ProductsContract.MessageEntry.CONTENT_URI;
    public static final Uri TEST_MESSAGES_WITH_USERID2_DIR = ProductsContract.MessageEntry.buildMessagesWithProductId2(PRODUCT_ID2);
    public static final Uri TEST_MESSAGES_WITH_PRODUCTID2_AND_MESSAGEID2_DIR = ProductsContract.MessageEntry.buildMesssageProductWithMessageId2(PRODUCT_QUERY, MESSAGE_ID2);

    public static final Uri TEST_LOCATION_DIR = ProductsContract.LocationEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = ProductsProvider.buildUriMatcher();

        assertEquals("Error: The PRODUCTS URI was matched incorrectly.",
                testMatcher.match(TEST_PRODUCTS_DIR), ProductsProvider.PRODUCTS);
        assertEquals("Error: The PRODUCTS WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_PRODUCTS_WITH_USERID2_DIR), ProductsProvider.PRODUCTS_WITH_USER_ID2);
        assertEquals("Error: The PRODUCTS WITH USER AND PRODUCT ID was matched incorrectly.",
                testMatcher.match(TEST_PRODUCTS_WITH_USERID2_AND_PRODUCTID2_DIR), ProductsProvider.PRODUCTS_WITH_USER_ID2_AND_PRODUCT_ID2);

        assertEquals("Error: The USER URI was matched incorrectly.",
                testMatcher.match(TEST_USER_DIR), ProductsProvider.USER);

        //newer
        assertEquals("Error: The LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_LOCATION_DIR), ProductsProvider.LOCATION);

        assertEquals("Error: The SUBSCRIPTION URI was matched incorrectly.",
                testMatcher.match(TEST_SUBSCRIPTION_DIR), ProductsProvider.SUBSCRIOPTIONS);
        assertEquals("Error: The SUBSCRIPTION WITH USER URI was matched incorrectly.",
                testMatcher.match(TEST_SUBSCRIPTION_WITH_USERID2_DIR), ProductsProvider.SUBSCRIOPTIONS_WITH_USER_ID2);
        assertEquals("Error: The SUBSCRIPTION WITH USER AND SUBSCRIPTION ID was matched incorrectly.",
                testMatcher.match(TEST_SUBSCRIPTION_WITH_USERID2_AND_SUBSCRIPTIONID2_DIR), ProductsProvider.SUBSCRIOPTIONS_WITH_USER_ID2_AND_SUBSCRIOPTIONS_ID2);


        assertEquals("Error: The MESSAGES URI was matched incorrectly.",
                testMatcher.match(TEST_MESSAGES_DIR), ProductsProvider.MESSAGES);
        assertEquals("Error: The MESSAGES WITH PRODUCT URI was matched incorrectly.",
                testMatcher.match(TEST_MESSAGES_WITH_USERID2_DIR), ProductsProvider.MESSAGES_WITH_PRODUCT_ID2);
        assertEquals("Error: The MESSAGES WITH PRODUCT AND MESAGE ID was matched incorrectly.",
                testMatcher.match(TEST_MESSAGES_WITH_PRODUCTID2_AND_MESSAGEID2_DIR), ProductsProvider.MESSAGES_WITH_PRODUCT_ID2_AND_MESSAGE_ID2);



    }
}
