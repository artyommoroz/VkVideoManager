package com.example.frost.vkvideomanager.model;


import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CatalogSection {

    private String id;
    private String name;
    private String type;
    private String icon;
    private String next;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private List<Album> albumList = new ArrayList<>();

    public CatalogSection(JSONObject from) {
        id = from.optString("id");
        name = from.optString("name");
        type = from.optString("type");
        next = from.optString("next");
        icon = from.optString("icon_2x");
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNext() {
        return next;
    }

    public VKList<VKApiVideo> getVideoList() {
        return videoList;
    }

    public void setVideoList(VKList<VKApiVideo> videoList) {
        this.videoList = videoList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

}
