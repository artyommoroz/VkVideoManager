package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.pojo.FeedPost;
import com.example.frost.vkvideomanager.pojo.NewsFeed;
import com.example.frost.vkvideomanager.pojo.Wall;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.NewsFeedViewHolder> {

    VKList<VKApiPost> wallPostList;
    List<FeedPost> feedPostList;
    VKList<VKApiVideo> videoList;
    Map<Integer, Object> owners;
    Context context;
    ItemClickListener itemClickListener;

    public PostAdapter(Context context, NewsFeed newsFeed, ItemClickListener itemClickListener) {
        this.context = context;
        feedPostList = newsFeed.getFeedPostList();
        videoList = newsFeed.getVideoList();
        owners = newsFeed.getOwners();
        this.itemClickListener = itemClickListener;
    }

    public PostAdapter(Context context, Wall wall, ItemClickListener itemClickListener) {
        this.context = context;
        wallPostList = wall.getWallPostList();
        videoList = wall.getVideoList();
        owners = wall.getOwners();
        this.itemClickListener = itemClickListener;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new NewsFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, int position) {
        if (owners.get(position) instanceof VKApiCommunity) {
            VKApiCommunity vkApiCommunity = (VKApiCommunity) owners.get(position);
            holder.name.setText(vkApiCommunity.name);
            Picasso.with(context).load(vkApiCommunity.photo_100).fit().centerCrop()
                    .transform(new CircleTransform()).into(holder.avatar);
        } else if (owners.get(position) instanceof VKApiUser) {
            VKApiUser vkApiUser = (VKApiUser) owners.get(position);
            holder.name.setText(vkApiUser.first_name + " " + vkApiUser.last_name);
            Picasso.with(context).load(vkApiUser.photo_100).fit().centerCrop()
                    .transform(new CircleTransform()).into(holder.avatar);
        }
        holder.title.setText(videoList.get(position).title);
        Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(holder.imageVideo);
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.titleAlbum)
        TextView title;
//        @Bind(R.id.date)
//        TextView date;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;
        @Bind(R.id.avatar)
        ImageView avatar;
        @Bind(R.id.name)
        TextView name;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.itemClicked(v, this.getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void itemClicked(View v, int position);
    }
}
