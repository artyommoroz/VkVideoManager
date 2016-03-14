package com.example.frost.vkvideomanager.model;

import org.json.JSONObject;

public class Album {

    private int id;
    private int count;
    private int ownerId;
    private String title;
    private String photo;
//    private String privacy;

    public Album(JSONObject from) {
        parse(from);
    }

    public Album parse(JSONObject from) {
        id = from.optInt("id");
        count = from.optInt("count");
        ownerId = from.optInt("owner_id");
        title = from.optString("title");
        photo = from.optString("photo_160");
//        privacy = from.optJSONObject("privacy").optString("type");
        return this;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPhoto() {
        return photo;
    }

    public int getOwnerId() {
        return ownerId;
    }

//    public String getPrivacy() {
//        return privacy;
//    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + ", Title: " + getTitle();
    }

}
