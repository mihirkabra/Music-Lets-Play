package com.miklabs.music;

import java.io.Serializable;

public class SongsModel implements Serializable {

    String songName;
    String artistName;
    int albumArt;
    String duration;
    String data;

    public SongsModel(String songName, String artistName, int albumArt, String duration, String data) {
        this.songName = songName;
        this.artistName = artistName;
        this.albumArt = albumArt;
        this.duration = duration;
        this.data = data;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(int albumArt) {
        this.albumArt = albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
