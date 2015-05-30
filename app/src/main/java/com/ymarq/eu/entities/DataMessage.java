package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by eu on 12/1/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataMessage implements Serializable
{
    public DataMessage() {
    }

    public DataMessage(String message,String userId,String productId)
    {
        this.Content = message;
        this.SenderId = userId;
        this.ProductId = productId;
        this.Id = UUID.randomUUID().toString();
    }

    public String Id ;
    public String ProductId;
    public String SenderId ;
    public String Content ;
    public String CreateDate;
    public String ToUserId;
    public String SenderName;

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }


    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }


    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }


    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }


    public String getToUserId() {
        return ToUserId;
    }

    public void setToUserId(String toUserId) {
        ToUserId = toUserId;
    }


    @JsonIgnore
    public String getAsJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            return null;
        }
    }

    public static DataMessage getFromJson(String json)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DataMessage dataMessage = mapper.readValue(json, DataMessage.class);
            return dataMessage;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            return null;
        }
    }

    public static List<DataMessage> getMessagesFromJson(String json)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //DataMessage[] dataMessage = mapper.readValue(json, DataMessage[].class);

            List<DataMessage> dataMessages = mapper.readValue(
                    json,
                    mapper.getTypeFactory().constructCollectionType(
                            List.class, DataMessage.class));

            return dataMessages;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            return null;
        }
    }

    @Override
    public String toString(){
        return this.SenderName+":"+this.Content;
    }
}
