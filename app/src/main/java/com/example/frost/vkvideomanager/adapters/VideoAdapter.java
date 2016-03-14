package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private VKList<VKApiVideo> videos;
    private Context context;
    private ItemClickListener itemClickListener;

    public VideoAdapter(Context context, VKList<VKApiVideo> videos, ItemClickListener itemClickListener) {
        this.context = context;
        this.videos = videos;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.title.setText(videos.get(position).title);
        holder.duration.setText(TimeConverter.secondsToHHmmss(videos.get(position).duration));
        Picasso.with(context).load(videos.get(position).photo_320).fit().centerCrop().into(holder.imageVideo);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.duration)
        TextView duration;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;

        public VideoViewHolder(View itemView) {
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
