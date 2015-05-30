package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

/**
 * Created by eu on 2/21/2015.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataGroupId implements Serializable {
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhonenumbers() {
        return phonenumbers;
    }

    public void setPhonenumbers(String phonenumbers) {
        this.phonenumbers = phonenumbers;
    }

    public String userid;
    public String phonenumbers;
}
