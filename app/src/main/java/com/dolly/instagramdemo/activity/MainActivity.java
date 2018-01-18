package com.dolly.instagramdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Main activity.
public class MainActivity extends AppCompatActivity implements AuthenticationListener {

    private AuthenticationDialog auth_dialog;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Data> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // action bar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler_view_feed);
        recyclerView.setLayoutManager(layoutManager);

        // Check if an auth token exists. If not, ask user to authenticate.
        String auth_token = SharedPreferencesUtils.getSharedPreferencesToken(this);
        System.out.println("auth_token = " + auth_token);
        if (auth_token == null || auth_token.isEmpty()) {
            authenticate();
        } else {
            onCodeReceived();
        }
    }

    @Override
    protected void onResume() {
        // TODO: Login logic here because onResume is always called
        super.onResume();
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

        String auth_token = SharedPreferencesUtils.getSharedPreferencesToken(this);
        System.out.println("auth_token = " + auth_token);

        // specify an adapter
        FeedRecyclerViewAdapter rcAdapter = new FeedRecyclerViewAdapter(this, data);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setHasFixedSize(true);
        fetchData(rcAdapter);
    }

    // fetch data from Instagram API by using Retrofit library
    // from API getting the recent media published by the owner with its like count, like status, etc
    public void fetchData(final FeedRecyclerViewAdapter rcAdapter) {
        //
        Call<FeedInstagramResponse> call = RestClient.getRetrofitService().
                getUserImages(SharedPreferencesUtils.getSharedPreferencesToken(getApplicationContext()));

        call.enqueue(new Callback<FeedInstagramResponse>() {
            @Override
            public void onResponse(Call<FeedInstagramResponse> call, Response<FeedInstagramResponse> response) {
                if (!ErrorHandlingUtil.isCorrectInstagramResponse(getApplicationContext(), response.body())) {
                    return;
                }



                // The good case. Everything is alright, and we got the data.
                if (response.body().getData() != null) {
                    for (int i = 0; i < response.body().getData().length; i++) {
                        data.add(response.body().getData()[i]);
                    }

                    data.sort((i1, i2) -> Integer.compare(Integer.valueOf(i2.getLikes().getCount()), Integer.valueOf(i1.getLikes().getCount())));

                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            //notify the recycler view about the new data
                            rcAdapter.notifyItemInserted(data.size());
                        });
                    }

                } else {
                    ErrorHandlingUtil.showErrorToUser(getApplicationContext(), "Something went wrong. Data returned is null");
                }
            }

            @Override
            public void onFailure(Call<FeedInstagramResponse> call, Throwable t) {
                ErrorHandlingUtil.showErrorToUser(getApplicationContext(), t.toString());
            }
        });

    }

    // Logout from the app.
    public void logOut() {
        SharedPreferencesUtils.clear(this);
        ClearCookiesUtil.clearCookies(this);
        finish();
        startActivity(getIntent());
    }
}
