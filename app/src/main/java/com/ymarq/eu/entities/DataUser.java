package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;


/**
 * Created by eu on 12/13/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataUser implements Serializable
{
    public DataUser(String id, String email,String name, String phone) {
        //this(userId,email);
        //this(userId);
        Id = id;
        Email = email;
        Name = name;
        Phone = phone;
        Image = "";
        LastNotificationTime = "";
    }

    //public DataUser(String userId, String email) {
    //    this(userId);
    //    Email = email;
    //}

    //public DataUser(String userId) {
    //    Id = id;
    //}

    public DataUser() {
    }

    public String getRegistrationId() {
        return RegistrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.RegistrationId = registrationId;
    }

    public String getIsPushRequired() {
        return IsPushRequired;
    }

    public void setIsPushRequired(String isPushRequired) {
        IsPushRequired = isPushRequired;
    }

    public String RegistrationId;
    @JsonIgnore
    public String Image;
    @JsonIgnore
    public String Password;
    public String Name;
    public String Phone;
    public String Id;
    public String Email;
    public String IsPushRequired = "false"; // this is for notifying server that is android
    @JsonIgnore
    public String LastNotificationTime;

    public String getLastNotificationTime() {
        return LastNotificationTime;
    }

    public void setLastNotificationTime(String lastNotificationTime) {
        LastNotificationTime = lastNotificationTime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
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

    public static DataUser getFromJson(String json)
    {
        DataUser user = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(json, DataUser.class);
            return user;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            //return null;
        }
        return user;
    }
}
