package com.dolly.instagramdemo.rest;

import com.dolly.instagramdemo.utils.InstagramConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static RetrofitServiceInterface getRetrofitService() {
        return new Retrofit.Builder()
                .baseUrl(InstagramConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitServiceInterface.class);
    }
}