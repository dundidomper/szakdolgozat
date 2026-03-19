package com.example.app1.model;

public class DiaryEntry {
    private String id;
    private String userId;
    private String text;
    private long timestamp;

    public DiaryEntry() {

    }

    public DiaryEntry(String id, String userId, String text, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
