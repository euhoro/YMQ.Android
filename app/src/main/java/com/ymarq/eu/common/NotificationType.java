package com.ymarq.eu.common;

/**
 * Created by eu on 2/3/2015.
 */
public enum NotificationType {
        None ("None"),//other data
        SellerMessage ("SellerMessage"),//seller wrote something
        BuyerMessage ("BuyerMessage"),//buyer wrote something
        NewProduct ("NewProduct"),//friend added a new product or match by subscription
        NewSubscription ("NewSubscription"),//friend added a new subscription or match by one of my products
        Summary ("Summary");//lots of stuff - messages + products

        private final String name;

        private NotificationType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName){
            return (otherName == null)? false:name.equals(otherName);
        }

        public String toString(){
            return name;
        }
    }