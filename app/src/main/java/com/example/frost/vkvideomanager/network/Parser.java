package com.example.frost.vkvideomanager.network;

import android.util.Log;

import com.example.frost.vkvideomanager.pojo.FeedPost;
import com.example.frost.vkvideomanager.pojo.NewsFeed;
import com.example.frost.vkvideomanager.pojo.Wall;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parser {

    public static NewsFeed parseNewsFeed(VKResponse response) {
        List<FeedPost> feedPostList = new ArrayList<>();
        VKList<VKApiVideo> videoList = new VKList<>();
        VKList<VKApiUser> profileList = new VKList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();
        Map<Integer, Object> owners;

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

            JSONArray jreposts = jitems.optJSONObject(i).optJSONArray("copy_history");
            if (jreposts != null) {
                for (int z = 0; z < jreposts.length(); z++) {
                    FeedPost repost = new FeedPost(jreposts.optJSONObject(z));
                    VKAttachments repostAttachments = repost.getAttachments();
                    for (int j = 0; j < repostAttachments.size(); j++) {
                        if (repostAttachments.get(j).getType().equals("video")) {
                            VKApiVideo vkApiVideo = (VKApiVideo) repostAttachments.get(j);
                            videoList.add(vkApiVideo);
                            feedPostList.add(feedPost);
                        }
                    }
                }
            }

            VKAttachments vkAttachments = feedPost.getAttachments();
            for (int j = 0; j < vkAttachments.size(); j++) {
                if (vkAttachments.get(j).getType().equals("video")) {
                    VKApiVideo vkApiVideo = (VKApiVideo) vkAttachments.get(j);
                    videoList.add(vkApiVideo);
                    feedPostList.add(feedPost);
                }
            }
        }

        owners = new HashMap<>(feedPostList.size());
        for (int n = 0; n < feedPostList.size(); n++) {
            if (feedPostList.get(n).getSourceId() < 0) {
                for (int m = 0; m < communityList.size(); m++) {
                    if (feedPostList.get(n).getSourceId() == -1 * communityList.get(m).id) {
                        owners.put(n, communityList.get(m));
                    }
                }
            } else {
                for (int m = 0; m < profileList.size(); m++) {
                    if (feedPostList.get(n).getSourceId() == profileList.get(m).id) {
                        owners.put(n, profileList.get(m));
                    }
                }
            }
        }
        return new NewsFeed(feedPostList, owners, videoList);
    }

    public static Wall parseWall(VKResponse response) {
        VKList<VKApiPost> wallPostList = new VKList<>();
        VKList<VKApiVideo> videoList = new VKList<>();
        VKList<VKApiUser> profileList = new VKList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();
        Map<Integer, Object> owners;

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
        Log.d("ParserProfiles", String.valueOf(profileList.size()));

        JSONArray jcommunties = response1.optJSONArray("groups");
        for (int i = 0; i < jcommunties.length(); i++) {
            JSONObject jcommunity =  jcommunties.optJSONObject(i);
            VKApiCommunity community = new VKApiCommunity(jcommunity);
            communityList.add(community);
        }

        JSONArray jitems = response1.optJSONArray("items");
        for(int i = 0; i < 99; i++) {
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

        owners = new HashMap<>(wallPostList.size());
        Log.d("ParserPosts", String.valueOf(wallPostList.size()));
        for (int n = 0; n < wallPostList.size(); n++) {
            if (wallPostList.get(n).id < 0) {
                for (int m = 0; m < communityList.size(); m++) {
                    if (wallPostList.get(n).id == -1 * communityList.get(m).id) {
                        owners.put(n, communityList.get(m));
                    }
                }
            } else {
                for (int m = 0; m < profileList.size(); m++) {
                    if (wallPostList.get(n).from_id == profileList.get(m).id) {
                        owners.put(n, profileList.get(m));
                    }
                }
            }
        }
        Log.d("ParserOwners", String.valueOf(owners.size()));
        return new Wall(wallPostList, owners, videoList);
    }
}
