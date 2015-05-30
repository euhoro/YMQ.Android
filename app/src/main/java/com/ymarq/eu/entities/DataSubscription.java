package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by eu on 12/16/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataSubscription implements Serializable {

    public UUID Id ;
    public String UserId ;
    public String SearchText ;

    public DataSubscription()
    {
    }

    public DataSubscription(String searchText,String userId)
    {
        this.SearchText = searchText;
        this.UserId = userId;
        this.Id = UUID.randomUUID();
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getSearchText() {
        return SearchText;
    }

    public void setSearchText(String searchText) {
        SearchText = searchText;
    }

    @Override
    public String toString() {
        return this.SearchText;
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

    public static DataSubscription getFromJson(String json)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DataSubscription dataSubscription = mapper.readValue(json, DataSubscription.class);
            return dataSubscription;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            return null;
        }
    }



    public static List<DataSubscription> getSubscriptionsFromJson(String json)
    {
        List<DataSubscription> dataSubscriptions = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            //DataMessage[] dataMessage = mapper.readValue(json, DataMessage[].class);

            dataSubscriptions = mapper.readValue(
                    json,
                    mapper.getTypeFactory().constructCollectionType(
                            List.class, DataSubscription.class));

            return dataSubscriptions;
        }
        catch (Exception ex)//throws JsonProcessingException
        {

        }
        return dataSubscriptions;
    }
}
