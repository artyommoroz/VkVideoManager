package com.frost.vkvideomanager.wall;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.utils.CircleTransform;
import com.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WallVideoAdapter extends RecyclerView.Adapter<WallVideoAdapter.WallVideoViewHolder> {

    private List<WallVideo> wallVideos = new ArrayList<>();
    private Context context;
    private ItemClickListener itemClickListener;

    public WallVideoAdapter(Context context, List<WallVideo> wallVideos, ItemClickListener itemClickListener) {
        this.context = context;
        this.wallVideos = wallVideos;
        this.itemClickListener = itemClickListener;
    }

    public WallVideoAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public List<WallVideo> getWallVideos() {
        return wallVideos;
    }

    public void setWallVideos(List<WallVideo> wallVideos) {
        this.wallVideos = wallVideos;
    }

    @Override
    public WallVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wall_post, parent, false);
        return new WallVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallVideoViewHolder holder, int position) {
        WallVideo wallVideo = wallVideos.get(position);
        holder.name.setText(wallVideo.getName());
        Picasso.with(context).load(wallVideo.getIcon()).fit().centerCrop()
                .transform(new CircleTransform()).into(holder.icon);
        holder.date.setText(TimeConverter.getFormattedDate(wallVideo.getDate()));
        holder.duration.setText(TimeConverter.secondsToHHmmss(wallVideo.getVideo().duration));
        holder.title.setText(wallVideo.getVideo().title);
        Picasso.with(context).load(wallVideo.getVideo().photo_320).fit().centerCrop()
                .into(holder.imageVideo);
    }

    @Override
    public int getItemCount() {
        return wallVideos.size();
    }


    public class WallVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.duration)
        TextView duration;
        @Bind(R.id.moreButton)
        ImageButton moreButton;
        @Bind(R.id.rootView)
        CardView rootView;

        public WallVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rootView.setOnClickListener(this);
            moreButton.setOnClickListener(this);
            icon.setOnClickListener(this);
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
