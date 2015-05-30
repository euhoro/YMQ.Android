package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by eu on 1/23/2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataUserIdDate implements Serializable {
    public DataUserIdDate(String userid, Date startDate) {
        this.userid = userid;

    }

    public String userid;
    //public String startDate;//0001-01-01T00:00:00

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
