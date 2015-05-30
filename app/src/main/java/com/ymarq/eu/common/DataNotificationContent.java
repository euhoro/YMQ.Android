package com.ymarq.eu.common;

/**
 * Created by eu on 2/3/2015.
 */
public class DataNotificationContent {

        public String Title;
        public String Description;
        public String Content;
        public String ContentSecondary;

    public String getContentSecondary() {
        return ContentSecondary;
    }

    public void setContentSecondary(String contentSecondary) {
        ContentSecondary = contentSecondary;
    }

        public NotificationType NotificationTypeResult;

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public NotificationType getNotificationTypeResult() {
            return NotificationTypeResult;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }


        public void setNotificationTypeResult(NotificationType notificationTypeResult) {
            NotificationTypeResult = notificationTypeResult;
        }

        //public DataNotificationContent(String title, String content) {
        //    Title = title;
        //    Content = content;
        //}

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }
    }