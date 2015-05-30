package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataFriendContact;

import java.util.List;

/**
 * Created by eu on 12/28/2014.
 */
public interface IOnContactsReceived {
    void fireOnContactsReceived(List<DataFriendContact> contacts);
}
