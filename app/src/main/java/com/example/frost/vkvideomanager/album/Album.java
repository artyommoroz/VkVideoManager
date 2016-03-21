package com.example.frost.vkvideomanager.album;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Album implements Parcelable {

    private int id;
    private int count;
    private int ownerId;
    private String title;
    private String photo;
    private String privacy;
    private boolean isSelected;

    public Album(JSONObject from) {
        id = from.optInt("id");
        count = from.optInt("count");
        ownerId = from.optInt("owner_id");
        title = from.optString("title");
        photo = from.optString("photo_160");
    }

    public Album(JSONObject from, String v) {
        id = from.optInt("id");
        title = from.optString("title");
    }

    private Album(Parcel source) {
        setId(source.readInt());
        setCount(source.readInt());
        setOwnerId(source.readInt());
        setTitle(source.readString());
        setPhoto(source.readString());
        setPrivacy(source.readString());
        setSelected(source.readByte() != 0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeInt(getCount());
        dest.writeInt(getOwnerId());
        dest.writeString(getTitle());
        dest.writeString(getPhoto());
        dest.writeString(getPrivacy());
        dest.writeByte((byte) (isSelected() ? 1 : 0));
    }

    public static final Parcelable.Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
