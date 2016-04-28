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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wall_post, parent, false);
        return new NewsFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder holder, int position) {
        holder.name.setText(wallVideoList.get(position).getName());
        Picasso.with(context).load(wallVideoList.get(position).getIcon()).fit().centerCrop()
                .transform(new CircleTransform()).into(holder.icon);
        holder.date.setText(TimeConverter.getFormattedDate(wallVideoList.get(position).getDate()));
        holder.duration.setText(TimeConverter.secondsToHHmmss(wallVideoList.get(position).getVideo().duration));
        holder.title.setText(wallVideoList.get(position).getVideo().title);
        Picasso.with(context).load(wallVideoList.get(position).getVideo().photo_320)
                .fit().centerCrop().into(holder.imageVideo);
    }


    @Override
    public int getItemCount() {
        return wallVideoList.size();
    }


    public class NewsFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public NewsFeedViewHolder(View itemView) {
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
