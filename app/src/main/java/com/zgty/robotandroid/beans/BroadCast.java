package com.zgty.robotandroid.beans;

public class BroadCast {

    private int id;
    private String content;
    private int train_state;
    private int minute;
    private int status;
    private String contentEnglish;
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTrain_state() {
        return train_state;
    }

    public void setTrain_state(int train_state) {
        this.train_state = train_state;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContentEnglish() {
        return contentEnglish;
    }

    public void setContentEnglish(String contentEnglish) {
        this.contentEnglish = contentEnglish;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
