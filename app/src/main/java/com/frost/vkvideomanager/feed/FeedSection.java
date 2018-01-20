package com.frost.vkvideomanager.feed;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.friend.FriendActivity;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.CircleTransform;
import com.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class FeedSection extends StatelessSection {

    private FeedSectionModel feedSectionModel;
    private Context context;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private FragmentManager fragmentManager;

    public FeedSection(Context context, FeedSectionModel feedSectionModel, FragmentManager fragmentManager) {
        super(R.layout.feed_section_header, R.layout.feed_section_footer, R.layout.item_video_big);
        this.context = context;
        this.feedSectionModel = feedSectionModel;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getContentItemsTotal() {
        return feedSectionModel.getVideoList().size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        videoList = feedSectionModel.getVideoList();
        itemHolder.title.setText(videoList.get(position).title);
        itemHolder.duration.setText(TimeConverter.secondsToHHmmss(videoList.get(position).duration));
        Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(itemHolder.imageVideo);

        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest videoRequest = VKApi.video().get(VKParameters.from(VKApiConst.VIDEOS,
                        videoList.get(position).owner_id + "_" + videoList.get(position).id));
                videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                        String videoUri = vkApiVideo.player;
                        UrlHelper.playVideo(context, videoUri);
                    }
                });
            }
        });

        itemHolder.moreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(context, videoList.get(position));
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(fragmentManager, videoList.get(position));
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        Picasso.with(context).load(feedSectionModel.getIcon()).fit().centerCrop()
                .transform(new CircleTransform()).into(headerHolder.icon);
        headerHolder.name.setText(feedSectionModel.getName());
        headerHolder.date.setText(TimeConverter.getFormattedDate(feedSectionModel.getDate()));

        headerHolder.icon.setOnClickListener(v -> {
            Intent friendIntent = new Intent(context, FriendActivity.class);
            friendIntent.putExtra(FriendActivity.FRIEND_ID, feedSectionModel.getSourceId());
            friendIntent.putExtra(FriendActivity.FRIEND_FULL_NAME, feedSectionModel.getName());
            context.startActivity(friendIntent);
        });
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
        @Bind(R.id.moreButton)
        ImageButton moreButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.date)
        TextView date;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
