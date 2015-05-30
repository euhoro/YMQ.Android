package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * Created by eu on 2/21/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataGroupContainer implements Serializable{
    public DataGroupFriends getGroup() {
        return group;
    }

    public void setGroup(DataGroupFriends group) {
        this.group = group;
    }

    public DataGroupFriends group ;

    public DataGroupContainer(DataGroupFriends group) {
        this.group = group;
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
}
