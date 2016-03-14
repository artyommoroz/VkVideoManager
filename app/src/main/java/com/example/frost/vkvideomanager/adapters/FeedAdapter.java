package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.model.FeedVideo;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.example.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<FeedVideo> feedVideoList;
    private Context context;
    private ItemClickListener itemClickListener;

    public FeedAdapter(Context context, List<FeedVideo> feedVideoList, ItemClickListener itemClickListener) {
        this.context = context;
        this.feedVideoList = feedVideoList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        String convertedDate = TimeConverter.getFormattedDate(feedVideoList.get(position).getFeedPost().getDate());
        if (feedVideoList.get(position).getFlag().equals("community")) {
            VKApiCommunity vkApiCommunity = feedVideoList.get(position).getVkApiCommunity();
            holder.name.setText(vkApiCommunity.name + " · " + convertedDate);
            Picasso.with(context).load(vkApiCommunity.photo_100).fit().centerCrop()
                    .transform(new CircleTransform()).into(holder.avatar);
        } else if (feedVideoList.get(position).getFlag().equals("user")) {
            VKApiUser vkApiUser = feedVideoList.get(position).getVkApiUser();
            holder.name.setText(vkApiUser.first_name + " " + vkApiUser.last_name + " · " + convertedDate);
            Picasso.with(context).load(vkApiUser.photo_100).fit().centerCrop()
                    .transform(new CircleTransform()).into(holder.avatar);
        }
//        holder.date.setText(convertedDate);
        holder.duration.setText(TimeConverter.secondsToHHmmss(feedVideoList.get(position).getVkApiVideo().duration));
        holder.title.setText(feedVideoList.get(position).getVkApiVideo().title);
        Picasso.with(context).load(feedVideoList.get(position).getVkApiVideo().photo_320)
                .fit().centerCrop().into(holder.imageVideo);
    }


    @Override
    public int getItemCount() {
        return feedVideoList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.title)
        TextView title;
//        @Bind(R.id.date)
//        TextView date;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;
        @Bind(R.id.icon)
        ImageView avatar;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.duration)
        TextView duration;

        public FeedViewHolder(View itemView) {
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
