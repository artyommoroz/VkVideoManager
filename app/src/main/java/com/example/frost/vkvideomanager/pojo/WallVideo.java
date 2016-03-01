package com.example.frost.vkvideomanager.pojo;


import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiVideo;

public class WallVideo {

    VKApiVideo vkApiVideo;
    VKApiPost vkApiPost;
    VKApiUser vkApiUser;
    VKApiCommunity vkApiCommunity;
    String flag;

    public WallVideo(VKApiPost vkApiPost, VKApiVideo vkApiVideo, VKApiUser vkApiUser, String flag) {
        this.vkApiPost = vkApiPost;
        this.vkApiVideo = vkApiVideo;
        this.vkApiUser = vkApiUser;
        this.flag = flag;
    }

    public WallVideo(VKApiPost vkApiPost, VKApiVideo vkApiVideo, VKApiCommunity vkApiCommunity, String flag) {
        this.vkApiPost = vkApiPost;
        this.vkApiVideo = vkApiVideo;
        this.vkApiCommunity = vkApiCommunity;
        this.flag = flag;
    }

    public VKApiCommunity getVkApiCommunity() {
        return vkApiCommunity;
    }

    public VKApiPost getVkApiPost() {
        return vkApiPost;
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
