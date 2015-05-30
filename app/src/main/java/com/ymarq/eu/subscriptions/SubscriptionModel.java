package com.ymarq.eu.subscriptions;

import com.ymarq.eu.entities.DataSubscription;

/**
 * Created by eu on 2/11/2015.
 */
public class SubscriptionModel {
    public DataSubscription getmDataSubscription() {
        return mDataSubscription;
    }

    DataSubscription mDataSubscription;
    public SubscriptionModel(DataSubscription subscription) {
        mDataSubscription = subscription;
    }
}
