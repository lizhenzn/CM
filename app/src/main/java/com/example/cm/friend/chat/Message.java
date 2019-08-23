package com.example.cm.friend.chat;

import android.graphics.Bitmap;

import java.util.Date;

public class Message {
    public static final int SELF_MSG = 1;//自己的消息
    public static final int FRIENDS_MSG = 2;//对方的消息
    private String from;
    private String body;
    private Bitmap photo;              //图片消息
    private String to;
    private int type;//区分自己还是好友的消息
    private String messageType;        //text  photo
    private String stanzaId;
    private Date date;           //消息时间

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStanzaId() {
        return stanzaId;
    }

    public void setStanzaId(String stanzaId) {
        this.stanzaId = stanzaId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
