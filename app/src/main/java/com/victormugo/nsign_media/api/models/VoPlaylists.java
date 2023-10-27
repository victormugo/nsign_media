package com.victormugo.nsign_media.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoPlaylists {

    @SerializedName("id")
    private String id;

    @SerializedName("x")
    private int x;

    @SerializedName("y")
    private int y;

    @SerializedName("width")
    private int width;

    @SerializedName("heigh")
    private int heigh;

    @SerializedName("resources")
    private List<VoResource> resources;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigh() {
        return heigh;
    }

    public void setHeigh(int heigh) {
        this.heigh = heigh;
    }

    public List<VoResource> getResources() {
        return resources;
    }

    public void setResources(List<VoResource> resources) {
        this.resources = resources;
    }
}
