package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eu on 1/23/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataNotifications implements Serializable {

    public DataProduct Product;
    public List<DataMessage> Messages;
    public String PublisherPhoneNumber;
    boolean IsNewProduct;
    DataSubscription Subscription;

    @JsonProperty("Publisher")
    public DataUser User;


    @JsonIgnore
    int NotificationType ;

    public DataUser getUser() {
        return User;
    }

    public void setUser(DataUser user) {
        user = user;
    }



    public String getPublisherPhoneNumber() {
        return PublisherPhoneNumber;
    }

    public void setPublisherPhoneNumber(String publisherPhoneNumber) {
        PublisherPhoneNumber = publisherPhoneNumber;
    }

    @JsonIgnore
    public int getNotificationType() {
        return this.NotificationType;
    }

    @JsonIgnore
    public void setNotificationType(int notificationType) {
        this.NotificationType = notificationType;
    }

    public DataProduct getProduct() {
        return Product;
    }

    public void setProduct(DataProduct productId) {
        Product = productId;
    }

    public boolean isNewProduct() {
        return IsNewProduct;
    }

    public void setNewProduct(boolean isNewProduct) {
        IsNewProduct = isNewProduct;
    }

    public DataSubscription getSubscription() {
        return this.Subscription;
    }

    public void setSubscription(DataSubscription subscription) {
        subscription = subscription;
    }

    public List<DataMessage> getMessages() {
        return Messages;
    }

    public void setMessages(List<DataMessage> messages) {
        Messages = messages;
    }

}
