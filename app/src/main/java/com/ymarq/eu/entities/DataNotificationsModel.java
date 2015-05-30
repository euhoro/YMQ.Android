package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eu on 1/23/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataNotificationsModel implements Serializable {
    public List<DataNotifications> BuyerNotifications;
    public List<DataNotifications> SellerNotifications;
    public List<DataNotifications> NewProducts;

    public List<DataNotifications> getNewSubscriptions() {
        return NewSubscriptions;
    }

    public void setNewSubscriptions(List<DataNotifications> newSubscriptions) {
        NewSubscriptions = newSubscriptions;
    }

    public List<DataNotifications> NewSubscriptions;

    public List<DataNotifications> getNewProducts() {
        return NewProducts;
    }

    public void setNewProducts(List<DataNotifications> newProducts) {
        NewProducts = newProducts;
    }

    public List<DataNotifications> getBuyerNotifications() {
        return BuyerNotifications;
    }

    public void setBuyerNotifications(List<DataNotifications> buyerNotifications) {
        BuyerNotifications = buyerNotifications;
    }

    public List<DataNotifications> getSellerNotifications() {
        return SellerNotifications;
    }

    public void setSellerNotifications(List<DataNotifications> sellerNotifications) {
        SellerNotifications = sellerNotifications;
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

    public static DataNotificationsModel getFromJson(String json)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DataNotificationsModel dataNotificationsModel = mapper.readValue(json, DataNotificationsModel.class);
            return dataNotificationsModel;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            return null;
        }
    }
}
