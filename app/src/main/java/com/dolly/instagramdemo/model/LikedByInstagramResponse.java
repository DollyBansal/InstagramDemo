package com.dolly.instagramdemo.model;

public class LikedByInstagramResponse extends BaseInstagramResponse {

    private LikedByUserData[] data;

    public LikedByUserData[] getData() {
        return data;
    }

    public void setData(LikedByUserData[] data) {
        this.data = data;
    }

}
