package com.ymarq.eu.common;

import com.ymarq.eu.entities.DataApiResult;

/**
 * Created by eu on 4/26/2015.
 */
public interface IOnGcmKeyReceived {
        void fireOnGcmKeyReceived(DataApiResult<String> key);
}
