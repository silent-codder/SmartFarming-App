package com.silentcodder.smartfarming.Notification;

public class Data {
    String title;
    String msg;

    public Data() {
    }

    public Data(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
