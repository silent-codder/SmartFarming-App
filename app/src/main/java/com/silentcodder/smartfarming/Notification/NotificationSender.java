package com.silentcodder.smartfarming.Notification;

public class NotificationSender {
    public Data data;
    public String to;

    public NotificationSender() {
    }

    public NotificationSender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
