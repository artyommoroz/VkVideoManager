package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.pojo.WallVideo;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WallAdapter extends RecyclerView.Adapter<WallAdapter.NewsFeedViewHolder> {

    private List<WallVideo> wallVideoList = new ArrayList<>();
    private Context context;
    private ItemClickListener itemClickListener;

    public WallAdapter(Context context, List<WallVideo> wallVideoList, ItemClickListener itemClickListener) {
        this.context = context;
        this.wallVideoList = wallVideoList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new NewsFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, int position) {
        Log.d("ParserOwnersAdapter", String.valueOf(wallVideoList.size()));
        if (wallVideoList.get(position).getFlag().equals("community")) {
            VKApiCommunity vkApiCommunity = wallVideoList.get(position).getVkApiCommunity();
            holder.name.setText(vkApiCommunity.name);
            Picasso.with(context).load(vkApiCommunity.photo_100).fit().centerCrop()
                    .transform(new CircleTransform()).into(holder.avatar);
        } else if (wallVideoList.get(position).getFlag().equals("user")) {
            VKApiUser vkApiUser = wallVideoList.get(position).getVkApiUser();
            holder.name.setText(vkApiUser.first_name + " " + vkApiUser.last_name);
            Picasso.with(context).load(vkApiUser.photo_100).fit().centerCrop()
                    .transform(new CircleTransform()).into(holder.avatar);
        }
        holder.title.setText(wallVideoList.get(position).getVkApiVideo().title);
        Picasso.with(context).load(wallVideoList.get(position).getVkApiVideo().photo_320)
                .fit().centerCrop().into(holder.imageVideo);
    }


    @Override
    public int getItemCount() {
        return wallVideoList.size();
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
