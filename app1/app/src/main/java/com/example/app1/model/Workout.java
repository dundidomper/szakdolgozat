package com.example.app1.model;

import java.io.Serializable;

public class Workout implements Serializable {
    private int workoutId;
    private String title;
    private String description;
    private int day;
    private int target;
    private String technique;
    private String type;

    public Workout() {

    }

    public int getWorkoutId() {
        return workoutId;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getDay() {
        return day;
    }
    public int getTarget() {
        return target;
    }
    public String getTechnique() {
        return technique;
    }
    public String getType() {
        return type;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public void setTarget(int target) {
        this.target = target;
    }
    public void setTechnique(String technique) {
        this.technique = technique;
    }
    public void setType(String type) {
        this.type = type;
    }
}
