package com.frost.vkvideomanager.catalog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.MainActivity;
import com.frost.vkvideomanager.community.CommunityActivity;
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


import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class CatalogSectionAdapter extends StatelessSection {

    private CatalogSection catalogSection;
    private Context context;
//    private VKList<VKApiVideo> videoList;
    private VKApiVideo video;
    private boolean expanded;

    public CatalogSectionAdapter(Context context, CatalogSection catalogSection) {
        super(R.layout.catalog_section_header, R.layout.catalog_section_footer, R.layout.item_video_catalog);
        this.catalogSection = catalogSection;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return expanded ? 10 : 3;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
//        videoList = catalogSection.getVideoList();
        if (catalogSection.getVideoList().size() > 1) {
            video = catalogSection.getVideoList().get(position);
        }
        if (video != null) {
            itemHolder.title.setText(video.title);
            itemHolder.duration.setText(TimeConverter.secondsToHHmmss(video.duration));
            if (Locale.getDefault().getLanguage().equals("ru")
                    || Locale.getDefault().getLanguage().equals("ua")
                    || Locale.getDefault().getLanguage().equals("by")
                    || Locale.getDefault().getLanguage().equals("kz")) {
                itemHolder.views.setText(TimeConverter.getViewsWithRightEnding(video.views));
            } else {
                itemHolder.views.setText(String.format(context.getString(R.string.video_views), video.views));
            }
            itemHolder.views.setText(TimeConverter.getViewsWithRightEnding(video.views));
            Picasso.with(context).load(video.photo_320).fit().centerCrop().into(itemHolder.imageVideo);
            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                            VKApiConst.VIDEOS, video.owner_id + "_" + video.id));
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
        }

        itemHolder.moreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(context, video);
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(((MainActivity) context).getSupportFragmentManager(),
                                video);
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
        if (catalogSection.getId().equals("ugc")) {
            headerHolder.icon.setImageResource(R.drawable.ic_whatshot_white_24dp);
        } else {
            Picasso.with(context).load(catalogSection.getIcon()).fit().centerCrop()
                    .transform(new CircleTransform()).into(headerHolder.icon);
        }
        headerHolder.name.setText(catalogSection.getName());
        headerHolder.icon.setOnClickListener(v -> {
            if (catalogSection.getType().equals("channel")) {
                Intent communityIntent = new Intent (context, CommunityActivity.class);
                communityIntent.putExtra(CommunityActivity.COMMUNITY_ID, Integer.valueOf(catalogSection.getId()));
                communityIntent.putExtra(CommunityActivity.COMMUNITY_NAME, catalogSection.getName());
                context.startActivity(communityIntent);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
        final FooterViewHolder footerHolder = (FooterViewHolder) holder;
        footerHolder.rootView.setOnClickListener(v -> {
            footerHolder.expandButton.setVisibility(View.GONE);
            if (expanded) {
                Intent sectionIntent = new Intent(context, CatalogSectionActivity.class);
                sectionIntent.putExtra(CatalogSectionActivity.SECTION_ID, catalogSection.getId());
                sectionIntent.putExtra(CatalogSectionActivity.SECTION_FROM, catalogSection.getNext());
                sectionIntent.putExtra(CatalogSectionActivity.SECTION_TITLE, catalogSection.getName());
                context.startActivity(sectionIntent);
            }
            if (!catalogSection.getId().equals("top")) {
                footerHolder.footerText.setVisibility(View.VISIBLE);
            }
            expanded = true;
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.rootView)
        RelativeLayout rootView;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.views)
        TextView views;
        @Bind(R.id.duration)
        TextView duration;
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
        ImageButton icon;
        @Bind(R.id.name)
        TextView name;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.footerText)
        TextView footerText;
        @Bind(R.id.expandButton)
        ImageView expandButton;
        @Bind(R.id.rootView)
        LinearLayout rootView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
