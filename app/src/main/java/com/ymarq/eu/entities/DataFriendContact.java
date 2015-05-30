package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by eu on 2/5/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataFriendContact implements Serializable {

    public String OriginalUserId ;
    public String PhoneNumber ;//this holds the formatated phone

    public boolean isKnownUser() {
        return IsKnownUser;
    }

    public void setKnownUser(boolean isKnownUser) {
        IsKnownUser = isKnownUser;
    }

    public String getOriginalUserId() {
        return OriginalUserId;
    }

    public void setOriginalUserId(String originalUserId) {
        OriginalUserId = originalUserId;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public boolean IsKnownUser;

    @JsonIgnore
    public boolean mIsSelected;

    @JsonIgnore
    public String Name;

    //this will hold the original number
    @JsonIgnore
    public String PhoneNumberOriginal;

    public String getmIconUri() {
        return mIconUri;
    }

    public void setmIconUri(String mIconUri) {
        this.mIconUri = mIconUri;
    }

    @JsonIgnore
    public String mIconUri;

    public long getmPhoneContactId() {
        return mPhoneContactId;
    }

    public void setmPhoneContactId(long mPhoneContactId) {
        this.mPhoneContactId = mPhoneContactId;
    }

    @JsonIgnore
    public long mPhoneContactId;

    public String getmContactPhoneId() {
        return mContactPhoneId;
    }

    public void setmContactPhoneId(String mContactPhoneId) {
        this.mContactPhoneId = mContactPhoneId;
    }

    @JsonIgnore
    public String mContactPhoneId;

    public String getName() {
        return Name;
    }

    public void setName(String mName) {
        this.Name = mName;
    }

    public String getPhoneNumberOriginal() {
        return PhoneNumberOriginal;
    }

    public void setPhoneNumberOriginal(String phoneNumberOriginal) {
        this.PhoneNumberOriginal = phoneNumberOriginal;
    }

    public DataFriendContact(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public DataFriendContact() {
    }

    public boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

}
