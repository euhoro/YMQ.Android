package com.ymarq.eu.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by eu on 12/30/2014.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataProductId implements Serializable {
    public DataProductId(String productId) {
        this.productId = productId;
    }

    public DataProductId(String productId,String userId) {
        this.userId = userId;
        this.productId = productId;
    }

    public DataProductId(String productId,Date dateFrom) {
        this(productId);

        from = android.text.format.DateFormat.format("YYYY-MM-ddThh:mm:ss", dateFrom).toString();
    }

    public String productId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String userId;

    public String from;//0001-01-01T00:00:00

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getFrom() {
        //0001-01-01T00:00:00
        //String res = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", from).toString();
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
