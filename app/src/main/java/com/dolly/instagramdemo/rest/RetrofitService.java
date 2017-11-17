package com.dolly.instagramdemo.rest;


import com.dolly.instagramdemo.model.FeedInstagramResponse;
import com.dolly.instagramdemo.model.LikedByInstagramResponse;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("/v1/users/self/media/recent")
    Observable<FeedInstagramResponse> getUserImages(@Query("access_token") String access_token);

    // use complete
    @POST("/v1/media/{media-id}/likes")
    Observable<FeedInstagramResponse> postLikes(@Path("media-id") String mediaId, @Query("access_token") String access_token);

    // use complete
    @DELETE("/v1/media/{media-id}/likes")
    Observable<FeedInstagramResponse> deleteLike(@Path("media-id") String mediaId, @Query("access_token") String access_token);

    @GET("/v1/media/{media-id}/likes")
    Observable<LikedByInstagramResponse> getLikes(@Path("media-id") String mediaId, @Query("access_token") String access_token);

}

