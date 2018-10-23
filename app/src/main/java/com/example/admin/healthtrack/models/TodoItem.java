package com.example.admin.healthtrack.models;

public class TodoItem {
    String time, text;
    boolean finished;

    public TodoItem(String time, String text, boolean finished) {
        this.time = time;
        this.text = text;
        this.finished = finished;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
