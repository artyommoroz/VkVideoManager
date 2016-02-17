package com.example.frost.vkvideomanager.pojo;

import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.Map;


public class Wall {

    private VKList<VKApiPost> wallPostList;
    private VKList<VKApiVideo> videoList;
    private Map<Integer, Object> owners;

    public Wall(VKList<VKApiPost> wallPostList, Map<Integer, Object> owners,
                    VKList<VKApiVideo> videoList) {
        this.wallPostList = wallPostList;
        this.owners = owners;
        this.videoList = videoList;
    }

    public VKList<VKApiPost> getWallPostList() {
        return wallPostList;
    }

    public Map<Integer, Object> getOwners() {
        return owners;
    }

    public VKList<VKApiVideo> getVideoList() {
        return videoList;
    }
}
