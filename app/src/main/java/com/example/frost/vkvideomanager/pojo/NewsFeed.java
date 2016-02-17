package com.example.frost.vkvideomanager.pojo;

import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.List;
import java.util.Map;


public class NewsFeed {

    private List<FeedPost> feedPostList;
    private Map<Integer, Object> owners;
    private VKList<VKApiVideo> videoList;

    public NewsFeed(List<FeedPost> feedPostList, Map<Integer, Object> owners,
                    VKList<VKApiVideo> videoList) {
        this.feedPostList = feedPostList;
        this.owners = owners;
        this.videoList = videoList;
    }

    public List<FeedPost> getFeedPostList() {
        return feedPostList;
    }

    public Map<Integer, Object> getOwners() {
        return owners;
    }

    public VKList<VKApiVideo> getVideoList() {
        return videoList;
    }
}
