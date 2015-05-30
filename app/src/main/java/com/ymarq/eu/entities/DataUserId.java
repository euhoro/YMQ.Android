package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

/**
 * Created by eu on 12/30/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataUserId implements Serializable {
    public DataUserId(String userId) {
        this.userId = userId;
    }

    public String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
