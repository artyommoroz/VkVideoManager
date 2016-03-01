package com.example.frost.vkvideomanager.network;

import com.example.frost.vkvideomanager.pojo.FeedPost;
import com.example.frost.vkvideomanager.pojo.FeedVideo;
import com.example.frost.vkvideomanager.pojo.WallVideo;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Parser {

    public static List<FeedVideo> parseNewsFeed(VKResponse response) {
        List<FeedPost> feedPostList = new ArrayList<>();
        VKList<VKApiVideo> videoList = new VKList<>();
        VKList<VKApiUser> profileList = new VKList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();
        List<FeedVideo> feedVideoList = new ArrayList<>();

        JSONObject response1 = response.json.optJSONObject("response");

        JSONArray jprofiles = response1.optJSONArray("profiles");
        for (int i = 0; i < jprofiles.length(); i++) {
            JSONObject jprofile =  jprofiles.optJSONObject(i);
            try {
                VKApiUser profile = new VKApiUser(jprofile);
                profileList.add(profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray jcommunties = response1.optJSONArray("groups");
        for (int i = 0; i < jcommunties.length(); i++) {
            JSONObject jcommunity =  jcommunties.optJSONObject(i);
            VKApiCommunity community = new VKApiCommunity(jcommunity);
            communityList.add(community);
        }

        JSONArray jitems = response1.optJSONArray("items");

        for(int i = 0; i < jitems.length(); i++) {
            FeedPost feedPost = new FeedPost(jitems.optJSONObject(i));
//            JSONArray jreposts = jitems.optJSONObject(i).optJSONArray("copy_history");
//            if (jreposts != null) {
//                for (int z = 0; z < jreposts.length(); z++) {
//                    FeedPost repost = new FeedPost(jreposts.optJSONObject(z));
//                    VKAttachments repostAttachments = repost.getAttachments();
//                    for (int j = 0; j < repostAttachments.size(); j++) {
//                        if (repostAttachments.get(j).getType().equals("video")) {
//                            VKApiVideo vkApiVideo = (VKApiVideo) repostAttachments.get(j);
//                            videoList.add(vkApiVideo);
//                            feedPostList.add(feedPost);
//                        }
//                    }
//                }
//            }

            VKAttachments vkAttachments = feedPost.getAttachments();
            for (int j = 0; j < vkAttachments.size(); j++) {
                if (vkAttachments.get(j).getType().equals("video")) {
                    VKApiVideo vkApiVideo = (VKApiVideo) vkAttachments.get(j);
                    videoList.add(vkApiVideo);
                    feedPostList.add(feedPost);
                }
            }
        }

        for (int n = 0; n < feedPostList.size(); n++) {
            if (feedPostList.get(n).getSourceId() < 0) {
                for (int m = 0; m < communityList.size(); m++) {
                    if (feedPostList.get(n).getSourceId() == -1 * communityList.get(m).id) {
                        FeedVideo feedVideo = new FeedVideo(feedPostList.get(n), videoList.get(n),
                                communityList.get(m), "community");
                        feedVideoList.add(feedVideo);
                    }
                }
            } else {
                for (int m = 0; m < profileList.size(); m++) {
                    if (feedPostList.get(n).getSourceId() == profileList.get(m).id) {
                        FeedVideo feedVideo = new FeedVideo(feedPostList.get(n), videoList.get(n),
                                profileList.get(m), "user");
                        feedVideoList.add(feedVideo);
                    }
                }
            }
        }

        return feedVideoList;
    }

    public static List<WallVideo> parseWall(VKResponse response) {
        VKList<VKApiPost> wallPostList = new VKList<>();
        VKList<VKApiVideo> videoList = new VKList<>();
        VKList<VKApiUser> profileList = new VKList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();
        List<WallVideo> wallVideoList = new ArrayList<>();

        JSONObject response1 = response.json.optJSONObject("response");

        JSONArray jprofiles = response1.optJSONArray("profiles");
        for (int i = 0; i < jprofiles.length(); i++) {
            JSONObject jprofile =  jprofiles.optJSONObject(i);
            try {
                VKApiUser profile = new VKApiUser(jprofile);
                profileList.add(profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray jcommunties = response1.optJSONArray("groups");
        for (int i = 0; i < jcommunties.length(); i++) {
            JSONObject jcommunity =  jcommunties.optJSONObject(i);
            VKApiCommunity community = new VKApiCommunity(jcommunity);
            communityList.add(community);
        }

        JSONArray jitems = response1.optJSONArray("items");
        for(int i = 0; i < jitems.length(); i++) {
            VKApiPost wallPost = null;
            try {
                wallPost = new VKApiPost(jitems.optJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray jreposts = jitems.optJSONObject(i).optJSONArray("copy_history");
            if (jreposts != null) {
                for (int z = 0; z < jreposts.length(); z++) {
                    VKApiPost repost = null;
                    try {
                        repost = new VKApiPost(jreposts.optJSONObject(z));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    VKAttachments repostAttachments = repost.attachments;
                    for (int j = 0; j < repostAttachments.size(); j++) {
                        if (repostAttachments.get(j).getType().equals("video")) {
                            VKApiVideo vkApiVideo = (VKApiVideo) repostAttachments.get(j);
                            videoList.add(vkApiVideo);
                            wallPostList.add(wallPost);
                        }
                    }
                }
            }

            VKAttachments vkAttachments = wallPost.attachments;
            for (int j = 0; j < vkAttachments.size(); j++) {
                if (vkAttachments.get(j).getType().equals("video")) {
                    VKApiVideo vkApiVideo = (VKApiVideo) vkAttachments.get(j);
                    videoList.add(vkApiVideo);
                    wallPostList.add(wallPost);
                }
            }
        }

        for (int n = 0; n < wallPostList.size(); n++) {
            if (wallPostList.get(n).id < 0) {
                for (int m = 0; m < communityList.size(); m++) {
                    if (wallPostList.get(n).from_id == -1 * communityList.get(m).id) {
                        WallVideo wallVideo = new WallVideo(wallPostList.get(n), videoList.get(n),
                                communityList.get(m), "community");
                        wallVideoList.add(wallVideo);
                    }
                }
            } else {
                for (int m = 0; m < profileList.size(); m++) {
                    if (wallPostList.get(n).from_id == profileList.get(m).id) {
                        WallVideo wallVideo = new WallVideo(wallPostList.get(n), videoList.get(n),
                                profileList.get(m), "user");
                        wallVideoList.add(wallVideo);
                    }
                }
            }
        }

        return wallVideoList;
    }
}
