package com.victormugo.nsign_media.api.models;

import com.google.gson.annotations.SerializedName;

public class VoResource {

    @SerializedName("id")
    private String id;

    @SerializedName("order")
    private int order;

    @SerializedName("name")
    private String name;

    @SerializedName("duration")
    private long duration;

    private boolean done;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
