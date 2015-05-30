package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataNotifications;

import java.util.List;

/**
 * Created by eu on 2/12/2015.
 */
public interface IOnNotificationsReceived {
    void fireOnNotificationsReceived(List<DataNotifications> notifications);
}