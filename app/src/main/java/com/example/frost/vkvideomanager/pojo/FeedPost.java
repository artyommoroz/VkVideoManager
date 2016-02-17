package com.example.frost.vkvideomanager.pojo;

import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FeedPost {

    private String date;
    private int sourceId;
    private VKAttachments attachments;

    public FeedPost() {}

    public FeedPost(JSONObject from) {
        parse(from);
    }

    public FeedPost parse(JSONObject from) {
        date = from.optString("date");
        sourceId = from.optInt("source_id");
        attachments = new VKAttachments(from.optJSONArray("attachments"));
        return this;
    }

    public String getDate() {
        return date;
    }

    public VKAttachments getAttachments() {
        return attachments;
    }

    public int getSourceId() {
        return sourceId;
    }

    @Override
    public String toString() {
        return "Id: " + getSourceId();
    }
}
