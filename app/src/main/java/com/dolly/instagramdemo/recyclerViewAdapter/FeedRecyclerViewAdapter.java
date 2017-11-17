package com.dolly.instagramdemo.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dolly.instagramdemo.R;
import com.dolly.instagramdemo.activity.LikedByActivity;
import com.dolly.instagramdemo.model.Data;
import com.dolly.instagramdemo.model.FeedInstagramResponse;
import com.dolly.instagramdemo.rest.RestClient;
import com.dolly.instagramdemo.utils.ErrorHandlingUtil;
import com.dolly.instagramdemo.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.RecyclerViewHolderFeed> {
    private Context context;
    private ArrayList<Data> data;

    // Constructor
    public FeedRecyclerViewAdapter(Context context, ArrayList<Data> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerViewHolderFeed onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for this view holder
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_user_feed, parent, false);
        // Call the view holder's constructor, and pass the view to it;
        // return that new view holder
        return new RecyclerViewHolderFeed(layoutView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerViewHolderFeed holder, final int position) {
        // Find out the data, based on this view holder's position
        // set the user fullname in textview
        holder.textview_user_fullname.setText(data.get(position).getUser().getFull_name());
        // set the state of like/unlike button
        setStateofLikeStatusInUI(holder, data.get(position).getUser_has_liked());
        // show like count in UI
        addUpdateLikeCount(holder, position);

        // resize profile pic with the help of Picasso lib
        Picasso.with(context)
                .load(data.get(position).getUser().getProfile_picture())
                .resize(100, 100)
                .centerInside()
                .transform(new RoundedCornersTransformation(50, 0))
                .into(holder.imageview_profile);

        // resize user feed pic with the help of Picasso lib
        Picasso.with(context)
                .load(data.get(position).getImages().getStandard_resolution().getUrl())
                .transform(new RoundedCornersTransformation(10, 10))
                .into(holder.imageviewv_photo);

        // set on click listener on like/unlike button
        holder.floating_action_button_like_status.setOnClickListener(view -> {
            // passing mediaId, previous like status before clicking the button
            likeToggle(data.get(position).getId(), data.get(position).getUser_has_liked(), position, holder);
        });

        // start new activity on click listener to show the users who liked the feed pic
        holder.button_like_count.setOnClickListener(v -> {
            Intent myIntent = new Intent(context.getApplicationContext(), LikedByActivity.class);
            myIntent.putExtra("mediaId", data.get(position).getId());
            context.getApplicationContext().startActivity(myIntent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    private void likeToggle(String mediaId, final boolean hasLiked, final int position, final RecyclerViewHolderFeed holder) {

        // we are using Observable not considering about backpressure,
        // if we suspect that backpressure is occurring in our app
        // then we can use Flowables instead of Observable
        Observable<FeedInstagramResponse> call;

        if (!hasLiked) {
            call = RestClient.getRetrofitService().postLikes(mediaId, SharedPreferencesUtils.getSharedPreferencesToken(context));
        } else {
            call = RestClient.getRetrofitService().deleteLike(mediaId, SharedPreferencesUtils.getSharedPreferencesToken(context));
        }

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
                if (!ErrorHandlingUtil.isCorrectInstagramResponse(context, responseData)) {
                    return;
                }

                // changing the like/unlike state in UI if request is successful
                setStateofLikeStatusInUI(holder, !hasLiked);
                setUserLikeStatusinPOJO(hasLiked, position);
                addUpdateLikeCount(holder, position);
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

    private void setStateofLikeStatusInUI(RecyclerViewHolderFeed holder, boolean hasLiked) {
        if (hasLiked) {
            holder.floating_action_button_like_status.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
        } else {
            holder.floating_action_button_like_status.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));

        }
    }

    private void setUserLikeStatusinPOJO(boolean hasLiked, int position) {
        String likeCount = data.get(position).getLikes().getCount();
        if (hasLiked) {
            // liked/unliked by user
            data.get(position).setUser_has_liked(false);
            // update like count
            int newLikeCount = Integer.valueOf(likeCount) - 1;
            data.get(position).getLikes().setCount(Integer.toString(newLikeCount));
        } else {
            data.get(position).setUser_has_liked(true);
            // update like count
            int newLikeCount = Integer.valueOf(likeCount) + 1;
            data.get(position).getLikes().setCount(Integer.toString(newLikeCount));
        }
    }

    private void addUpdateLikeCount(RecyclerViewHolderFeed holder, int position) {
        // like count on a feed pic
        String likeCount = data.get(position).getLikes().getCount();
        if (Integer.valueOf(likeCount) > 0) {
            // Todo: do not use concatenation string
            // Todo: for 1:like and more then one:likes
            holder.button_like_count.setText(likeCount + " like");
            holder.button_like_count.setClickable(true);

        } else {
            holder.button_like_count.setText("");
            holder.button_like_count.setClickable(false);

        }
    }

    class RecyclerViewHolderFeed extends RecyclerView.ViewHolder {
        TextView textview_user_fullname;
        ImageView imageviewv_photo;
        ImageView imageview_profile;
        FloatingActionButton floating_action_button_like_status;
        Button button_like_count;

        private RecyclerViewHolderFeed(View itemView) {
            super(itemView);
            textview_user_fullname = itemView.findViewById(R.id.textview_user_fullname);
            imageviewv_photo = itemView.findViewById(R.id.iimageview_user_feed_pic);
            imageview_profile = itemView.findViewById(R.id.imageview_profile_pic);
            floating_action_button_like_status = itemView.findViewById(R.id.like_status);
            button_like_count = itemView.findViewById(R.id.button_like_count);
        }
    }

}
