package com.example.frost.vkvideomanager.wall;


import com.vk.sdk.api.model.VKApiVideo;

public class WallVideo {

    private VKApiVideo vkApiVideo;
    private String name;
    private String icon;
    private long date;
    private int postId;

    public WallVideo(VKApiVideo vkApiVideo, String name, String icon, long date, int postId) {
        this.vkApiVideo = vkApiVideo;
        this.name = name;
        this.icon = icon;
        this.date = date;
        this.postId = postId;
    }

    public VKApiVideo getVideo() {
        return vkApiVideo;
    }

    public void setVideo(VKApiVideo vkApiVideo) {
        this.vkApiVideo = vkApiVideo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
