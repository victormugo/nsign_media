package com.victormugo.nsign_media.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoMedia {

    @SerializedName("schedule")
    private VoSchedule schedule;

    @SerializedName("playlists")
    private List<VoPlaylists> playlists;

    public VoSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(VoSchedule schedule) {
        this.schedule = schedule;
    }

    public List<VoPlaylists> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<VoPlaylists> playlists) {
        this.playlists = playlists;
    }
}
