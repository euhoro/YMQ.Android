package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataUser;

/**
 * Created by eu on 3/5/2015.
 */
public interface IOnUsersReceived {
        void fireOnOneUserReceived( DataApiResult<DataUser> result);
}
