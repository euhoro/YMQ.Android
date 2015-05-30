package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataUser;

/**
 * Created by eu on 2/21/2015.
 */
public interface IOnUserReceived {
        void fireOnOneUserReceived( DataApiResult<DataUser> result);
}
