package com.ymarq.eu.news;

import com.ymarq.eu.entities.DataNotifications;

/**
 * Created by eu on 2/12/2015.
 */
public class NewsModel{
    public DataNotifications getNewsModel() {
        return _newsModel;
    }

    DataNotifications _newsModel;
    public NewsModel(DataNotifications notificationsModel) {
        _newsModel = notificationsModel;
    }
}