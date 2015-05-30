package com.ymarq.eu.contacts;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by eu on 2/5/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ContactItem {
    private String mName = new String();
    private String mTelephone = new String();
    private boolean mIsSelected = true;
    private boolean mIsSelected4Invite = false;

    public boolean getIsSelected4Invite() {
        return mIsSelected4Invite;
    }

    public void setIsSelected4Invite(boolean mHasAp) {
        this.mIsSelected4Invite = mHasAp;
    }

    public String getmKnownUserId() {
        return mKnownUserId;
    }

    public void setmKnownUserId(String mKnownUserId) {
        this.mKnownUserId = mKnownUserId;
    }

    private String mKnownUserId = new String();;

    public boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String mTelephone) {
        this.mTelephone = mTelephone;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public static List<ContactItem> getContactItemsFromJson(String json)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();

            List<ContactItem> dataContacts = mapper.readValue(
                    json,
                    mapper.getTypeFactory().constructCollectionType(
                            List.class, ContactItem.class));

            return dataContacts;
        }
        catch (Exception ex)//throws JsonProcessingException
        {
            return null;
        }
    }

    public static String getJsonFromList(List<ContactItem> dataProducts)
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
