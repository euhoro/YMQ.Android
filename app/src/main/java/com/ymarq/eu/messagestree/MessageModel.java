package com.ymarq.eu.messagestree;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.ymarq.eu.entities.DataMessage;

import java.util.Date;

/**
 * Message is a Custom Object to encapsulate message information/fields
 *
 * @author Adil Soomro
 *
 */
public class MessageModel extends DataMessage {
    /**
     * The content of the message
     */
    //String message;
    /**
     * boolean to determine, who is sender of this message
     */
    protected boolean isMine;
    /**
     * boolean to determine, whether the message is a status message or not.
     * it reflects the changes/updates about the sender is writing, have entered text etc
     */
    protected boolean isStatusMessage;

    /**
     * Constructor to make a Message object
     */
    //public MessageModel(String message, boolean isMine) {
    //	super();
    //	//this.message = message;
    //	this.isMine = isMine;
    //	this.isStatusMessage = false;
    //}

    public MessageModel(DataMessage message,String userId) {
        super();
        //this.message = message;
        this.isMine = message.SenderId.equals(userId);
        this.Content = message.Content;
        this.isStatusMessage = false;
        this.SenderName = message.SenderName;
        this.SenderId = message.SenderId;
        this.ToUserId = message.ToUserId;

        if (message.CreateDate != null) {
            Date date = new Date(Long.parseLong(message.CreateDate.replaceAll(".*?(\\d+).*", "$1")));

            this.CreateDate = FormatFriendlyDate(date);
        }
        else
        {
            this.CreateDate= "";
        }
    }

    private	 String FormatFriendlyDate(Date date)
    {
        if (DateUtils.isToday(date.getTime()))
            return DateFormat.format("HH:mm", date).toString();
        else
            return  DateFormat.format("MMM-dd HH:mm", date).toString();
    }

    /**
     * Constructor to make a status Message object
     * consider the parameters are swaped from default Message constructor,
     *  not a good approach but have to go with it.
     */
    public MessageModel(boolean status, String message) {
        super();
        this.Content = message;
        this.isMine = false;
        this.isStatusMessage = status;
    }
    //public String getMessage() {
    //	return message;
    //}
    //public void setMessage(String message) {
    //	this.message = message;
    //}
    public boolean isMine() {
        return isMine;
    }

    public boolean isPrivate()//.getToUserId().equals("00000000-0000-0000-0000-000000000000")
    {
        return getToUserId() != null && !getToUserId().equals("");
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }
    public boolean isStatusMessage() {
        return isStatusMessage;
    }
    public void setStatusMessage(boolean isStatusMessage) {
        this.isStatusMessage = isStatusMessage;
    }


}
