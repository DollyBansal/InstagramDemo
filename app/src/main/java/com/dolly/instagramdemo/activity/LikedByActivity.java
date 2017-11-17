package com.dolly.instagramdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dolly.instagramdemo.R;
import com.dolly.instagramdemo.model.LikedByInstagramResponse;
import com.dolly.instagramdemo.model.LikedByUserData;
import com.dolly.instagramdemo.recyclerViewAdapter.LikedByRecyclerViewAdapter;
import com.dolly.instagramdemo.rest.RestClient;
import com.dolly.instagramdemo.utils.ErrorHandlingUtil;
import com.dolly.instagramdemo.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        // we are using Observable not considering about backpressure,
        // if we suspect that backpressure is occurring in our app
        // then we can use Flowables instead of Observable
        Observable<LikedByInstagramResponse> call = RestClient.getRetrofitService().
                getLikes(mediaId, SharedPreferencesUtils.getSharedPreferencesToken(getApplicationContext()));

        // data is produced upstream by an Observable,
        // and is then pushed downstream to the assigned Observer
        Observer observer = new Observer<LikedByInstagramResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("onSubscribe", "onSubscribe");
                // log to know the name of the current thread, here we are using mainThread
                Log.i("onSubscribe()", Thread.currentThread().getName());
            }

            @Override
            public void onNext(LikedByInstagramResponse responseData) {
                if (!ErrorHandlingUtil.isCorrectInstagramResponse(getApplicationContext(), responseData)) {
                    return;
                }

                if (responseData.getData() != null) {
                    for (int i = 0; i < responseData.getData().length; i++) {
                        likedByUserData.add(responseData.getData()[i]);
                    }
                    rcAdapter.notifyItemInserted(likedByUserData.size());
                } else {
                    ErrorHandlingUtil.showErrorToUser(getApplicationContext(), "Something went wrong. Data returned is null");
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        // Reference: https://code.tutsplus.com/tutorials/reactive-programming-operators-in-rxjava-20--cms-28396
        // Observable emits the data on the thread where subscriber is declared.
        // with subscribeOn() operator we can define a different Scheduler
        // we use observeOn(Scheduler) to redirect our Observable’s emissions to a different
        // here we are using AndroidSchedulers.mainThread(), it redirect emissions to Android’s main UI thread
        // which also include as part of the RxAndroid library, rather than the RxJava library.
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

}
