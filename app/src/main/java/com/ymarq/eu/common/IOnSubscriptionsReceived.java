package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataSubscription;

import java.util.List;

/**
 * Created by eu on 12/27/2014.
 */
public interface IOnSubscriptionsReceived {
        void fireOnSubscriptionsReceived(List<DataSubscription> subscriptions);
        void fireOnOneSubscriptionReceived(DataSubscription subscription);
}