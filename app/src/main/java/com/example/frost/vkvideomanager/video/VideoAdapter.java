package com.example.frost.vkvideomanager.video;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private VKList<VKApiVideo> videoList;
    private Context context;
    private ItemClickListener itemClickListener;

    public VideoAdapter(Context context, VKList<VKApiVideo> videoList, ItemClickListener itemClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.title.setText(videoList.get(position).title);
        holder.views.setText(TimeConverter.getViewsWithRightEnding(videoList.get(position).views));
//        holder.name.setText(videos.get(position).);
        holder.duration.setText(TimeConverter.secondsToHHmmss(videoList.get(position).duration));
        Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(holder.imageVideo);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.duration)
        TextView duration;
        @Bind(R.id.title)
        TextView title;
//        @Bind(R.id.name)
//        TextView name;
        @Bind(R.id.views)
        TextView views;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;
        @Bind(R.id.moreButton)
        ImageButton moreButton;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            moreButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.itemClicked(v, getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void itemClicked(View v, int position);
    }
}
