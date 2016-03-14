package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ListAdapter extends BaseAdapter {

    VKList<VKApiVideo> videoList = new VKList<>();
    LayoutInflater inflater;
    Context context;

    public ListAdapter(Context context, VKList<VKApiVideo> videoList) {
        this.videoList = videoList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public VKApiVideo getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_video, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final VKApiVideo currentVideo = getItem(position);

        holder.title.setText(currentVideo.title);
//        holder.duration.setText(currentVideo.duration);
        Picasso.with(context).load(currentVideo.photo_320).fit().centerCrop().into(holder.imageVideo);
        holder.imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentVideo.player)));
            }
        });

        return convertView;
    }


    static class ViewHolder {

        @Bind(R.id.duration)
        TextView duration;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;

        public ViewHolder(View item) {
            ButterKnife.bind(this, item);
        }
    }

}
