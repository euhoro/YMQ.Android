package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by eu on 2/5/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataGroupFriends implements Serializable {
    public String UserId ;

    public List<DataFriendContact> getMembers() {
        return Members;
    }

    public void setMembers(List<DataFriendContact> members) {
        Members = members;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public List<DataFriendContact> Members ;

    //public List<DataFriendContact> MembersFriends ;
    public static DataGroupFriends getFromJson(String json)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DataGroupFriends dataGroupFriends = mapper.readValue(json, DataGroupFriends.class);
            return dataGroupFriends;
        }
        catch (IOException ex)
        {
            return null;
        }
    }


    @JsonIgnore
    public String getAsJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
        catch (JsonProcessingException ex)//throws JsonProcessingException
        {
            return null;
        }
    }

}
