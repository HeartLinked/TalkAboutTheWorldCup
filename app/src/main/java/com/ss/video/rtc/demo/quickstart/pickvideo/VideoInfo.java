package com.ss.video.rtc.demo.quickstart.pickvideo;

import android.net.Uri;

public class VideoInfo {
    private int id;
    private String title;
    private String album;
    private String artist;
    public Uri uri;
    private String displayName;
    private String mimeType;
    private String path;
    private long size;
    private long duration;

    public VideoInfo(int id, String title, String album, Uri uri,
                     String displayName, String mimeType,
                     String path, long size, long duration) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.uri = uri;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}