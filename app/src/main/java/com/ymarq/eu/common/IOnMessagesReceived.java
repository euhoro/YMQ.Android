package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataMessage;

import java.util.List;

/**
 * Created by eu on 12/28/2014.
 */
public interface IOnMessagesReceived {
    void fireOnMessagessReceived(List<DataMessage> messages);
}
