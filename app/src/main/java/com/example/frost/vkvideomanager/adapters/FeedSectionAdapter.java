package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.model.FeedSection;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.example.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class FeedSectionAdapter extends StatelessSection {

    FeedSection feedSection;
    Context context;
    VKList<VKApiVideo> videoList = new VKList<>();
    SectionedRecyclerViewAdapter sectionAdapter;

    public FeedSectionAdapter(Context context, FeedSection feedSection, SectionedRecyclerViewAdapter sectionAdapter) {
        super(R.layout.catalog_section_header, R.layout.feed_section_footer, R.layout.item_video_big);
        this.feedSection = feedSection;
        this.context = context;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {
        return feedSection.getVideoList().size();
//        return expanded ? 10 : 3;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        videoList = feedSection.getVideoList();
        itemHolder.title.setText(videoList.get(position).title);
        itemHolder.duration.setText(TimeConverter.secondsToHHmmss(videoList.get(position).duration));
        Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(itemHolder.imageVideo);

        Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(itemHolder.imageVideo);
        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest videoRequest = VKApi.video().get(VKParameters.from("videos",
                        videoList.get(position).owner_id + "_" + videoList.get(position).id));
                videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                        Uri videoUri = Uri.parse(vkApiVideo.player);
                        context.startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
                    }
                });
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        Picasso.with(context).load(feedSection.getIcon()).fit().centerCrop()
                .transform(new CircleTransform()).into(headerHolder.avatar);
        headerHolder.name.setText(feedSection.getName());
    }
    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.rootView)
        LinearLayout rootView;
        @Bind(R.id.duration)
        TextView duration;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.imageVideo)
        ImageView imageVideo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon)
        ImageView avatar;
        @Bind(R.id.name)
        TextView name;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.footer)
        LinearLayout footer;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
