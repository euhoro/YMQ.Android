package com.ymarq.eu.messagestree;

import com.ymarq.eu.entities.DataMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eu on 2/19/2015.
 */
public class PersonConversationModel extends MessageModel{

    private List<MessageModel> personMessageModelList;

    public PersonConversationModel(DataMessage firstMessage,String userId ) {
        super(firstMessage,userId);
        personMessageModelList = new ArrayList<MessageModel>();
    }

    public List<MessageModel> getPersonMessageModelList() {
        return personMessageModelList;
    }
}

