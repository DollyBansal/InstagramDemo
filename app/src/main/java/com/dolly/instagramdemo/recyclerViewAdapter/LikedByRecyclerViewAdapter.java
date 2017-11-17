package com.dolly.instagramdemo.recyclerViewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dolly.instagramdemo.R;
import com.dolly.instagramdemo.model.LikedByUserData;

import java.util.ArrayList;

public class LikedByRecyclerViewAdapter extends RecyclerView.Adapter<LikedByRecyclerViewAdapter.RecyclerViewHolderUserLiked> {
    private Context context;
    private ArrayList<LikedByUserData> data;

    public LikedByRecyclerViewAdapter(Context context, ArrayList<LikedByUserData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerViewHolderUserLiked onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_user_liked, parent, false);
        return new RecyclerViewHolderUserLiked(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderUserLiked holder, int position) {
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        holder.textview_like_user_name.setText(data.get(position).getUserName());
        holder.textview_ike_user_first_name.setText(data.get(position).getFirstName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerViewHolderUserLiked extends RecyclerView.ViewHolder {
        TextView textview_like_user_name;
        TextView textview_ike_user_first_name;

        private RecyclerViewHolderUserLiked(View itemView) {
            super(itemView);
            textview_like_user_name = itemView.findViewById(R.id.textview_liked_user_name);
            textview_ike_user_first_name = itemView.findViewById(R.id.textview_liked_user_first_name);
        }
    }
}
