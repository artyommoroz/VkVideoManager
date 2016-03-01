package com.example.frost.vkvideomanager.pojo;

import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiVideo;


public class FeedVideo {

    VKApiVideo vkApiVideo;
    FeedPost feedPost;
    VKApiUser vkApiUser;
    VKApiCommunity vkApiCommunity;
    String flag;

    public FeedVideo(FeedPost feedPost, VKApiVideo vkApiVideo, VKApiUser vkApiUser, String flag) {
        this.feedPost = feedPost;
        this.vkApiVideo = vkApiVideo;
        this.vkApiUser = vkApiUser;
        this.flag = flag;
    }

    public FeedVideo(FeedPost feedPost, VKApiVideo vkApiVideo, VKApiCommunity vkApiCommunity, String flag) {
        this.feedPost = feedPost;
        this.vkApiVideo = vkApiVideo;
        this.vkApiCommunity = vkApiCommunity;
        this.flag = flag;
    }

    public VKApiCommunity getVkApiCommunity() {
        return vkApiCommunity;
    }

    public FeedPost getFeedPost() {
        return feedPost;
    }

    public VKApiUser getVkApiUser() {
        return vkApiUser;
    }

    public VKApiVideo getVkApiVideo() {
        return vkApiVideo;
    }

    public String getFlag() {
        return flag;
    }
}
