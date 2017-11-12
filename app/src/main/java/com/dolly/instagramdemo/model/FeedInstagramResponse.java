package com.dolly.instagramdemo.model;

// Instagram response for the feed call. Here, we show recent media posted by the user in the feed.
// API details: https://www.instagram.com/developer/endpoints/users/
public class FeedInstagramResponse extends BaseInstagramResponse {

    private Data[] data;

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

}
