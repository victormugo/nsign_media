package com.victormugo.nsign_media.bus;

import com.victormugo.nsign_media.api.models.VoResource;

public class IntentServiceResult {

    int x;
    int y;

    int width;

    int heigh;

    VoResource resource;

    public IntentServiceResult(int x, int y, int width, int heigh, VoResource voResource) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigh = heigh;
        this.resource = voResource;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeigh() {
        return heigh;
    }

    public VoResource getResource() {
        return resource;
    }
}
