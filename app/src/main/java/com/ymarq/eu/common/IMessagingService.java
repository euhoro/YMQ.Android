package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataNotificationsModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by eu on 12/13/2014.
 */
public interface IMessagingService {
    /// <summary>
    /// Publish new sell message
    /// </summary>
    DataApiResult<Boolean> SendMessage(DataMessage message,boolean async);

    /// <summary>
    /// Returns all new messages
    /// </summary>
    DataApiResult<List<DataMessage>> GetMessages(UUID productId,boolean async);

    /// <summary>
    /// Returns all new messages by date
    // this is no longer used or implemented correctly
    /// </summary>
    DataApiResult<List<DataMessage>> GetMessagesByDate(UUID productId,Date from,boolean async);

    /// <summary>
    /// Returns all new notifications by date
    /// </summary>
    DataApiResult<DataNotificationsModel> GetNotificationsByUserDate(String userId,Date from,boolean async);
}
