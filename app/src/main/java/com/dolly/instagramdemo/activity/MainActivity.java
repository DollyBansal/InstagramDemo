package com.dolly.instagramdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dolly.instagramdemo.R;
import com.dolly.instagramdemo.dialog.AuthenticationDialog;
import com.dolly.instagramdemo.interfaces.AuthenticationListener;
import com.dolly.instagramdemo.model.Data;
import com.dolly.instagramdemo.model.FeedInstagramResponse;
import com.dolly.instagramdemo.recyclerViewAdapter.FeedRecyclerViewAdapter;
import com.dolly.instagramdemo.rest.RestClient;
import com.dolly.instagramdemo.utils.ClearCookiesUtil;
import com.dolly.instagramdemo.utils.ErrorHandlingUtil;
import com.dolly.instagramdemo.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

// Main activity.
public class MainActivity extends AppCompatActivity implements AuthenticationListener {

    private AuthenticationDialog auth_dialog;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Data> data = new ArrayList<>();
    private CompositeDisposable disposables = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler_view_feed);
        recyclerView.setLayoutManager(layoutManager);

        // Check if an auth token exists. If not, ask user to authenticate.
        String auth_token = SharedPreferencesUtils.getSharedPreferencesToken(this);
        if (auth_token == null || auth_token.isEmpty()) {
            authenticate();
        } else {
            onCodeReceived();
        }
    }

    // Authenticate user by opening a dialog box.
    void authenticate() {
        // open authentication/login dialog box
        auth_dialog = new AuthenticationDialog(MainActivity.this, MainActivity.this);
        auth_dialog.setCancelable(true);
        auth_dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    // Add logout option at top right.
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // after authentication is done, we can fetch data with the API
    // and show it in the UI with the help of RecycleView
    @Override
    public void onCodeReceived() {
        // specify an adapter
        FeedRecyclerViewAdapter rcAdapter = new FeedRecyclerViewAdapter(this, data);
        recyclerView.setAdapter(rcAdapter);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        fetchData(rcAdapter);
    }

    // fetch data from Instagram API by using Retrofit library
    // from API getting the recent media published by the owner with its like count, like status, etc
    public void fetchData(final FeedRecyclerViewAdapter rcAdapter) {

        // The requests are built via Retrofit. The calls are made asynchronously through RxJava.
        // we are using Observable not considering about backpressure,
        // if we suspect that backpressure is occurring in our app
        // then we can use Flowables instead of Observable
        Observable<FeedInstagramResponse> call = RestClient.getRetrofitService()
                .getUserImages(SharedPreferencesUtils.getSharedPreferencesToken(getApplicationContext()));

        // data is produced upstream by an Observable,
        // and is then pushed downstream to the assigned Observer
        Observer observer = new Observer<FeedInstagramResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("onSubscribe", "onSubscribe");
                // log to know the name of the current thread, here we are using mainThread
                Log.i("onSubscribe()", Thread.currentThread().getName());
            }

            @Override
            public void onNext(FeedInstagramResponse responseData) {
                if (!ErrorHandlingUtil.isCorrectInstagramResponse(getApplicationContext(), responseData)) {
                    return;
                }

                // The good case. Everything is alright, and we got the data.
                if (responseData.getData() != null) {
                    for (int i = 0; i < responseData.getData().length; i++) {
                        data.add(responseData.getData()[i]);
                    }
                    //notify the recycler view about the new data
                    rcAdapter.notifyItemInserted(data.size());
                } else {
                    ErrorHandlingUtil.showErrorToUser(getApplicationContext(), "Something went wrong. Data returned is null");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("onError", e.toString());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.i("onComplete", "onComplete");
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

    // Logout from the app.
    public void logOut() {
        SharedPreferencesUtils.clear(this);
        ClearCookiesUtil.clearCookies(this);
        finish();
        startActivity(getIntent());
    }
}
