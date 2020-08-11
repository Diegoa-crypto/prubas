package com.Marche.Notificaciones;

public class Sender {
    public Data notification;
    public String to;

    public Sender(Data notification, String to) {
        this.notification = notification;
        this.to = to;
    }

    public Data getNotification() {
        return notification;
    }

    public void setNotification(Data notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
