package com.dolly.instagramdemo.model;

// All Instagram responses contain the meta field. The code value of 200 indicates the success.
// Anything else indicates some sort of error. The two classes: FeedInstagramResponse and
// LikedByInstagramResponse extend this class.
// The benefit of keeping meta field in a base class is that the error handling of the response
// can be done in a clean manner, irrespective of the type of response. See ErrorHandlingUtil class
// for implementation of error handling.
public class BaseInstagramResponse {
    protected Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
