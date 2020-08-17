package com.atcampus.morefriendsmorefun.Model;

public class SenderModel {

    private NotificationDataModel notificationDataModel;
    private String to;

    public SenderModel() {
    }

    public SenderModel(NotificationDataModel notificationDataModel, String to) {
        this.notificationDataModel = notificationDataModel;
        this.to = to;
    }

    public NotificationDataModel getNotificationDataModel() {
        return notificationDataModel;
    }

    public void setNotificationDataModel(NotificationDataModel notificationDataModel) {
        this.notificationDataModel = notificationDataModel;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
