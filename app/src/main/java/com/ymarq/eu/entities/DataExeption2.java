package com.ymarq.eu.entities;

/**
 * Created by eu on 5/29/2015.
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eu on 12/13/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataExeption2 implements Serializable
{
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getStackTrace() {
        return StackTrace;
    }

    public void setStackTrace(String stackTrace) {
        StackTrace = stackTrace;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String UserId;
    public String StackTrace;
    public String FileName ;

    @JsonIgnore
    public String getAsJSON() {
        String result;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(this);
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            result = null;
        }
        return result;
    }

    public static DataExeption2 getFromJson(String json)
    {
        DataExeption2 dataproduct = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataproduct = mapper.readValue(json, DataExeption2.class);
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            //return null;
        }
        return dataproduct;
    }
}
