package com.frost.vkvideomanager.feed;

import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import org.json.JSONObject;


public class FeedSection {

    private int sourceId;
    private long date;
    private String icon;
    private String name;
    private VKList<VKApiVideo> videoList = new VKList<>();

    public FeedSection (JSONObject from) {
        date = from.optLong("date");
        sourceId = from.optInt("source_id");
    }

    public int getSourceId() {
        return sourceId;
    }

    public long getDate() {
        return date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public VKList<VKApiVideo> getVideoList() {
        return videoList;
    }

    public void setVideoList(VKList<VKApiVideo> videoList) {
        this.videoList = videoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
