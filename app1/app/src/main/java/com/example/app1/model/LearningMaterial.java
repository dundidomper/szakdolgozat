package com.example.app1.model;

public class LearningMaterial {
    private String id;
    private String title;
    private String description;
    private String link;

    public LearningMaterial() {
    }

    public LearningMaterial(String id, String title, String description, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }
}