package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * Created by eu on 12/30/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataApiNotification implements Serializable {

    public int MessageType;

    public String getData() {
        return Data;
    }

    public void setData(String resultContent) {
        this.Data = resultContent;
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int resultType) {
        this.MessageType = resultType;
    }

    public String Data;

    public static DataApiNotification getFromJson(String json)
    {
        DataApiNotification user = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(json, DataApiNotification.class);
            return user;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
           String s = ex.toString();
        }
        return user;
    }

}
