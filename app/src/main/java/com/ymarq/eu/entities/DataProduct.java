package com.ymarq.eu.entities;

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
public class DataProduct implements Serializable
{
    //"Description":"Suzuki Swift","Hashtag":"Nice car","Id":"e7b6646b-4718-4abf-8260-73188d395c30","Image":"","PublisherId":""}]
    public String Id ;
    public String UserId;

    public String UserName;
    public String Description ;
    public String Hashtag ;
    public String ImageContent ;
    public String Image ;
    public double LocationLong;
    public double LocationLat;
    public String CreateTime;
    public boolean GiveAway;
    public boolean NotifyFriends;

    @JsonIgnore
    public int NumberOfNotifications;

    @JsonIgnore
    public boolean NotifyOthers;

    public boolean isGiveAway() {
        return GiveAway;
    }

    public void setGiveAway(boolean giveAway) {
        GiveAway = giveAway;
    }

    public DataProduct() {
    }

    public DataProduct(String id) {
        Id = id;
    }

    public DataProduct(String id, String userId, String description, String hashtag, String imageContent) {
        this(id);
        UserId = userId;
        Description = description;
        Hashtag = hashtag;
        ImageContent = imageContent;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getNumberOfNotifications() {
        return NumberOfNotifications;
    }

    public void setNumberOfNotifications(int numberOfNotifications) {
        NumberOfNotifications = numberOfNotifications;
    }

    public boolean isNotifyFriends() {
        return NotifyFriends;
    }

    public void setNotifyFriends(boolean notifyFriends) {
        NotifyFriends = notifyFriends;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public double getLocationLat() {
        return LocationLat;
    }

    public void setLocationLat(double locationLat) {
        LocationLat = locationLat;
    }

    public double getLocationLong() {
        return LocationLong;
    }

    public void setLocationLong(double locationLong) {
        LocationLong = locationLong;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }

    public String getImageContent() {
        return ImageContent;
    }

    public void setImageContent(String imageContent) {
        ImageContent = imageContent;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

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

    public static DataProduct getFromJson(String json)
    {
        DataProduct dataproduct = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataproduct = mapper.readValue(json, DataProduct.class);
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            //return null;
        }
        return dataproduct;
    }

    @Override
    public String toString(){
        return this.Description;
    }

    public static List<DataProduct> getProductsFromJson(String json)
    {
        List<DataProduct> dataProducts = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            //DataMessage[] dataMessage = mapper.readValue(json, DataMessage[].class);

            dataProducts = mapper.readValue(
                    json,
                    mapper.getTypeFactory().constructCollectionType(
                            List.class, DataProduct.class));

            return dataProducts;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            Log.e("Error",ex.toString());
        }
        return dataProducts;
    }

    public static String getJsonFromList(List<DataProduct> dataProducts)
    {
        String result;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(dataProducts);
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            result = null;
        }
        return result;
    }
}
