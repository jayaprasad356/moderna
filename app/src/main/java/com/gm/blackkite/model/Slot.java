package com.gm.blackkite.model;

public class Slot {
    public final String id;
    public final String title;
    public final String lastOrderTime;

    public Slot(String id, String title, String lastOrderTime) {
        this.id = id;
        this.title = title;

        this.lastOrderTime = lastOrderTime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLastOrderTime() {
        return lastOrderTime;
    }
}
