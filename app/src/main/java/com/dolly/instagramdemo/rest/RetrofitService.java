package com.dolly.instagramdemo.rest;


import com.dolly.instagramdemo.model.FeedInstagramResponse;
import com.dolly.instagramdemo.model.LikedByInstagramResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("/v1/users/self/media/recent")
    Call<FeedInstagramResponse> getUserImages(@Query("access_token") String access_token);

    @POST("/v1/media/{media-id}/likes")
    Call<FeedInstagramResponse> postLikes(@Path("media-id") String mediaId, @Query("access_token") String access_token);

    @DELETE("/v1/media/{media-id}/likes")
    Call<FeedInstagramResponse> deleteLike(@Path("media-id") String mediaId, @Query("access_token") String access_token);

    @GET("/v1/media/{media-id}/likes")
    Call<LikedByInstagramResponse> getLikes(@Path("media-id") String mediaId, @Query("access_token") String access_token);

}

