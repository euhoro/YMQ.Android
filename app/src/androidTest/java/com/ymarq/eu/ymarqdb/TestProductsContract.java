package com.ymarq.eu.ymarqdb;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.ymarq.eu.data.ProductsContract;

public class TestProductsContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_USER = "/76971 ea670ff89be";
    private static final String TEST_PRODUCT = "0c27f102-c312-4af3-9826-eebe6d47382a";

    public void testBuildUserUri() {
        Uri userUri = ProductsContract.ProductEntry.buildProductsWithUserId2(TEST_USER);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildProductsUser in " +
                        "ProductsContract.",
                userUri);
        assertEquals("Error: Products User not properly appended to the end of the Uri",
                TEST_USER, userUri.getLastPathSegment());

        String result = userUri.toString();
        assertEquals("Error: Products User Uri doesn't match our expected result",
                result,
                "content://com.ymarq.eu.ymarq.app/Products/%2F76971%20ea670ff89be");



        userUri = ProductsContract.SubscriptionEntry.buildSubscriptionsWithUserId2(TEST_USER);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildProductsUser in " +
                        "ProductsContract.",
                userUri);
        assertEquals("Error: Products User not properly appended to the end of the Uri",
                TEST_USER, userUri.getLastPathSegment());

        result = userUri.toString();
        assertEquals("Error: Products User Uri doesn't match our expected result",
                result,
                "content://com.ymarq.eu.ymarq.app/Subscriptions/%2F76971%20ea670ff89be");



        userUri = ProductsContract.MessageEntry.buildMessagesWithProductId2(TEST_PRODUCT);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildProductsUser in " +
                        "ProductsContract.",
                userUri);
        assertEquals("Error: Products User not properly appended to the end of the Uri",
                TEST_PRODUCT, userUri.getLastPathSegment());

        result = userUri.toString();
        assertEquals("Error: Products User Uri doesn't match our expected result",
                result,
                "content://com.ymarq.eu.ymarq.app/Messages/0c27f102-c312-4af3-9826-eebe6d47382a");

    }
}
