package com.example.omi.niggachatdemo.model;

/**
 * Created by omi on 11/17/2016.
 */

public class ChatMessage {

    String from;
    String message;
    String time;
    boolean showSentImage;

    public ChatMessage() {
        this.showSentImage = false;
    }

    public boolean isShowSentImage()
    {
        return this.showSentImage;
    }


    public void setShouldShowSentImage()
    {
        this.showSentImage = true;
    }

    public ChatMessage(String from, String message, String time) {

        this.from = from;
        this.message = message;
        this.time = time;
    }

    public void setFrom(String from) {

        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {

        return message;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {

        return from;
    }
}
