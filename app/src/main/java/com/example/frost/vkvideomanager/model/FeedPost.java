package com.example.frost.vkvideomanager.model;

import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONObject;


public class FeedPost {

    private long date;
    private int sourceId;
    private VKAttachments attachments;

    public FeedPost(JSONObject from) {
        parse(from);
    }

    public FeedPost parse(JSONObject from) {
        date = from.optLong("date");
        sourceId = from.optInt("source_id");
        attachments = new VKAttachments(from.optJSONArray("attachments"));
        return this;
    }

    public long getDate() {
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
