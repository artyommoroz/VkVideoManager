package com.frost.vkvideomanager.network;

import com.frost.vkvideomanager.album.Album;
import com.frost.vkvideomanager.catalog.CatalogSection;
import com.frost.vkvideomanager.feed.FeedSection;
import com.frost.vkvideomanager.wall.WallVideo;
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

    public static VKList<VKApiVideo> parseVideos(VKResponse response) {
        VKList<VKApiVideo> videoList = new VKList<>();

        JSONObject jResponse = response.json.optJSONObject("response");
        JSONArray jVideos = jResponse.optJSONArray("items");
        for (int i = 0; i < jVideos.length(); i++) {
            try {
                VKApiVideo vkApiVideo = new VKApiVideo(jVideos.optJSONObject(i));
                videoList.add(vkApiVideo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return videoList;
    }

    public static List<Album> parseAlbums(VKResponse response) {
        List<Album> albumList = new ArrayList<>();

        JSONObject jResponse = response.json.optJSONObject("response");
        JSONArray jAlbums = jResponse.optJSONArray("items");
        for (int i = 0; i < jAlbums.length(); i++) {
            Album album = null;
            try {
                JSONObject jAlbum = jAlbums.getJSONObject(i);
                album = new Album(jAlbum);
                JSONObject jPrivacy = jAlbum.optJSONObject("privacy");
                if (jPrivacy != null) {
                    album.setPrivacy(jPrivacy.optString("type"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            albumList.add(album);
        }
        return albumList;
    }

    public static List<WallVideo> parseWall(VKResponse response) {
        VKList<VKApiPost> wallPostList = new VKList<>();
        VKList<VKApiVideo> videoList = new VKList<>();
        VKList<VKApiUser> profileList = new VKList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();
        List<WallVideo> wallVideoList = new ArrayList<>();

        JSONObject jResponse = response.json.optJSONObject("response");
        JSONArray jProfiles = jResponse.optJSONArray("profiles");
        for (int i = 0; i < jProfiles.length(); i++) {
            JSONObject jprofile =  jProfiles.optJSONObject(i);
            try {
                profileList.add(new VKApiUser(jprofile));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray jCommunities = jResponse.optJSONArray("groups");
        for (int i = 0; i < jCommunities.length(); i++) {
            JSONObject jCommunity =  jCommunities.optJSONObject(i);
            communityList.add(new VKApiCommunity(jCommunity));
        }

        JSONArray jPosts = jResponse.optJSONArray("items");
        for(int i = 0; i < jPosts.length(); i++) {
            VKApiPost wallPost = null;
            try {
                wallPost = new VKApiPost(jPosts.optJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray jReposts = jPosts.optJSONObject(i).optJSONArray("copy_history");
            if (jReposts != null) {
                for (int z = 0; z < jReposts.length(); z++) {
                    VKApiPost repost = null;
                    try {
                        repost = new VKApiPost(jReposts.optJSONObject(z));
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

        for (int i = 0; i < wallPostList.size(); i++) {
            int fromId = wallPostList.get(i).from_id;
            if (fromId < 0) {
                for (int j = 0; j < communityList.size(); j++) {
                    if (fromId == -1 * communityList.get(j).id) {
                        WallVideo wallVideo = new WallVideo(videoList.get(i), communityList.get(j).name,
                                communityList.get(j).photo_100, wallPostList.get(i).date, wallPostList.get(i).id);
                        wallVideoList.add(wallVideo);
                    }
                }
            } else {
                for (int j = 0; j < profileList.size(); j++) {
                    if (fromId == profileList.get(j).id) {
                        WallVideo wallVideo = new WallVideo(videoList.get(i), profileList.get(j).first_name
                                + " " + profileList.get(j).last_name, profileList.get(j).photo_100,
                                wallPostList.get(i).date, wallPostList.get(i).id);
                        wallVideoList.add(wallVideo);
                    }
                }
            }
        }

        return wallVideoList;
    }

    public static List<FeedSection> parseNewsFeed(VKResponse response) {
        List<FeedSection> feedSectionList = new ArrayList<>();
        VKList<VKApiUser> profileList = new VKList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();

        JSONObject jResponse = response.json.optJSONObject("response");
        JSONArray jProfiles = jResponse.optJSONArray("profiles");
        for (int i = 0; i < jProfiles.length(); i++) {
            JSONObject jprofile =  jProfiles.optJSONObject(i);
            try {
                VKApiUser profile = new VKApiUser(jprofile);
                profileList.add(profile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray jCommunties = jResponse.optJSONArray("groups");
        for (int i = 0; i < jCommunties.length(); i++) {
            JSONObject jcommunity =  jCommunties.optJSONObject(i);
            VKApiCommunity community = new VKApiCommunity(jcommunity);
            communityList.add(community);
        }

        JSONArray jItems = jResponse.optJSONArray("items");
        for(int i = 0; i < jItems.length(); i++) {
            JSONObject jItem = jItems.optJSONObject(i);
            FeedSection feedSection = new FeedSection(jItem);
            JSONObject jVideo = jItem.optJSONObject("video");
            JSONArray jVideos = jVideo.optJSONArray("items");
            VKList<VKApiVideo> videoList = new VKList<>();
            for (int j = 0; j < jVideos.length(); j++) {
                try {
                    VKApiVideo vkApiVideo = new VKApiVideo(jVideos.optJSONObject(j));
                    videoList.add(vkApiVideo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            feedSection.setVideoList(videoList);
            feedSectionList.add(feedSection);
        }


        for (int i = 0; i < feedSectionList.size(); i++) {
            int sourceId = feedSectionList.get(i).getSourceId();
            if (sourceId < 0) {
                for (int j = 0; j < communityList.size(); j++) {
                    if (sourceId == -1 * communityList.get(j).id) {
                        feedSectionList.get(i).setIcon(communityList.get(j).photo_100);
                        feedSectionList.get(i).setName(communityList.get(j).name);
                    }
                }
            } else {
                for (int j = 0; j < profileList.size(); j++) {
                    if (sourceId == profileList.get(j).id) {
                        feedSectionList.get(i).setIcon(profileList.get(j).photo_100);
                        feedSectionList.get(i).setName(profileList.get(j).first_name + " " + profileList.get(j).last_name);
                    }
                }
            }
        }
        return feedSectionList;
    }

    public static List<CatalogSection> parseCatalog(VKResponse response) {
        List<CatalogSection> catalogSectionList = new ArrayList<>();
        VKList<VKApiCommunity> communityList = new VKList<>();

        JSONObject jResponse = response.json.optJSONObject("response");
        JSONArray jCommunties = jResponse.optJSONArray("groups");
        for (int i = 0; i < jCommunties.length(); i++) {
            JSONObject jCommunity =  jCommunties.optJSONObject(i);
            VKApiCommunity community = new VKApiCommunity(jCommunity);
            communityList.add(community);
        }

        JSONArray jSections = jResponse.optJSONArray("items");
        for (int i = 0; i < jSections.length(); i++) {
            JSONObject jSection = jSections.optJSONObject(i);
            CatalogSection catalogSection = new CatalogSection(jSection);
            VKList<VKApiVideo> videoList = new VKList<>();
            List<Album> albumList = new ArrayList<>();
            JSONArray jItems = jSection.optJSONArray("items");
            if (catalogSection.getId().equals("series")) {
                for (int j = 0; j < jItems.length(); j++) {
                    Album album = new Album(jItems.optJSONObject(j));
                    albumList.add(album);
                }
                catalogSection.setAlbumList(albumList);
            }
            else {
                for (int j = 0; j < jItems.length(); j++) {
                    try {
                        VKApiVideo vkApiVideo = new VKApiVideo(jItems.optJSONObject(j));
                        videoList.add(vkApiVideo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                catalogSection.setVideoList(videoList);
            }
            catalogSectionList.add(catalogSection);
        }

        for (int i = 0; i < catalogSectionList.size(); i++) {
            String sectionId = catalogSectionList.get(i).getId();
            if (!sectionId.equals("series")) {
                for (int j = 0; j < communityList.size(); j++) {
                    if (sectionId.equals("ugc") || sectionId.equals("top")
                            || Integer.valueOf(sectionId) == -1 * communityList.get(j).id) {
                        catalogSectionList.get(i).setIcon(communityList.get(j).photo_100);
                    }
                }
            }
        }
        return catalogSectionList;
    }

    public static VKList<VKApiVideo> parseCatalogSection(VKResponse response) {
        VKList<VKApiVideo> videoList = new VKList<>();

        JSONObject jResponse = response.json.optJSONObject("response");
        JSONArray jItems = jResponse.optJSONArray("items");
        for (int i = 0; i < jItems.length(); i++) {
            VKApiVideo vkApiVideo = null;
            JSONObject jItem = jItems.optJSONObject(i);
            if (jItem.optString("type").equals("video")) {
                try {
                    vkApiVideo = new VKApiVideo(jItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            videoList.add(vkApiVideo);
        }
        return videoList;
    }
}
