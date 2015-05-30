package com.ymarq.eu.common;

/**
 * Created by eu on 12/30/2014.
 */
public enum ResultArrayTypes {
    None ("None"),
    Messages ("Messages"),
    Products ("Products"),
    Subscriptions ("Subscriptions"),
    Notifications ("Notifications"),
    Contacts ("Contacts"),
    OneProduct ("OneProduct"),
    OneSubscription ("OneSubscription"),
    OneUser ("OneUser");


    private final String name;

    private ResultArrayTypes(String s) {
        name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
        return name;
    }
}
