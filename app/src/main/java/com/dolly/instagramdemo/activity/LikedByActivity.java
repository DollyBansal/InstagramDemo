package com.dolly.instagramdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dolly.instagramdemo.R;
import com.dolly.instagramdemo.model.LikedByInstagramResponse;
import com.dolly.instagramdemo.model.LikedByUserData;
import com.dolly.instagramdemo.recyclerViewAdapter.LikedByRecyclerViewAdapter;
import com.dolly.instagramdemo.rest.RestClient;
import com.dolly.instagramdemo.utils.ErrorHandlingUtil;
import com.dolly.instagramdemo.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This activity will show the user who liked the specific post
// on click "like count button" this activity will visible to the user
// with the intent it will paas the mediaId
public class LikedByActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<LikedByUserData> likedByUserData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_by_user);
        Intent intent = getIntent();
        String mediaId = intent.getStringExtra("mediaId");
        recyclerView = findViewById(R.id.recycler_view_liked_by_user);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LikedByRecyclerViewAdapter rcAdapter = new LikedByRecyclerViewAdapter(LikedByActivity.this, likedByUserData);
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setHasFixedSize(true);
        fetchData(mediaId, rcAdapter);
    }

    // Fetch data from Instagram API by using Retrofit library
    // from API getting the list of user who liked the specific post
    // and the results will be shown in UI with the help of RecyclerView
    public void fetchData(String mediaId, final LikedByRecyclerViewAdapter rcAdapter) {

        Call<LikedByInstagramResponse> call = RestClient.getRetrofitService().
                getLikes(mediaId, SharedPreferencesUtils.getSharedPreferencesToken(getApplicationContext()));
        call.enqueue(new Callback<LikedByInstagramResponse>() {
            @Override
            public void onResponse(Call<LikedByInstagramResponse> call, Response<LikedByInstagramResponse> response) {
                if (!ErrorHandlingUtil.isCorrectInstagramResponse(getApplicationContext(), response.body())) {
                    return;
                }

                if (response.body().getData() != null) {
                    for (int i = 0; i < response.body().getData().length; i++) {
                        likedByUserData.add(response.body().getData()[i]);
                    }
                    rcAdapter.notifyItemInserted(likedByUserData.size());
                } else {
                    ErrorHandlingUtil.showErrorToUser(getApplicationContext(), "Something went wrong. Data returned is null");
                }
            }

            @Override
            public void onFailure(Call<LikedByInstagramResponse> call, Throwable t) {
                ErrorHandlingUtil.showErrorToUser(getApplicationContext(), t.toString());
            }
        });
    }
}
