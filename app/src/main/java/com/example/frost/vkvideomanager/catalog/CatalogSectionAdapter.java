package com.example.frost.vkvideomanager.catalog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.frost.vkvideomanager.MainActivity;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.album.Album;
import com.example.frost.vkvideomanager.album.AlbumActivity;
import com.example.frost.vkvideomanager.community.CommunityActivity;
import com.example.frost.vkvideomanager.network.AdditionRequests;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.example.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class CatalogSectionAdapter extends StatelessSection {

    private CatalogSection catalogSection;
    private Context context;
    private VKList<VKApiVideo> videoList;
    private List<Album> albumList;
    private boolean expanded;
    private SectionedRecyclerViewAdapter sectionAdapter;

    public CatalogSectionAdapter(Context context, CatalogSection catalogSection, SectionedRecyclerViewAdapter sectionAdapter) {
        super(R.layout.catalog_section_header, R.layout.catalog_section_footer, R.layout.item_video);
        this.catalogSection = catalogSection;
        this.context = context;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {
        return expanded ? catalogSection.getVideoList().size() : 3;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        if (catalogSection.getId().equals("series")) {
            albumList = catalogSection.getAlbumList();
            itemHolder.title.setText(albumList.get(position).getTitle());
            itemHolder.duration.setText(String.valueOf(albumList.get(position).getCount()));
            itemHolder.moreButton.setVisibility(View.GONE);
            Picasso.with(context).load(albumList.get(position).getPhoto()).fit().centerCrop().into(itemHolder.imageVideo);
            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent albumIntent = new Intent(context, AlbumActivity.class);
                    albumIntent.putExtra("ownerId", albumList.get(position).getOwnerId());
                    albumIntent.putExtra("albumId", albumList.get(position).getId());
                    albumIntent.putExtra("albumTitle", albumList.get(position).getTitle());
                    context.startActivity(albumIntent);
                }
            });
        } else {
            videoList = catalogSection.getVideoList();
            itemHolder.title.setText(videoList.get(position).title);
            itemHolder.duration.setText(TimeConverter.secondsToHHmmss(videoList.get(position).duration));
            itemHolder.views.setText(TimeConverter.getViewsWithRightEnding(videoList.get(position).views));
            Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(itemHolder.imageVideo);
            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                            VKApiConst.VIDEOS, videoList.get(position).owner_id + "_" + videoList.get(position).id));
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
        itemHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.popup_menu_video);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add:
                                AdditionRequests.addVideo(context, videoList.get(position));
                                return true;
                            case R.id.add_to_album:
                                AdditionRequests.addVideoToAlbum(((MainActivity) context).getSupportFragmentManager(),
                                        videoList.get(position));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
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
        if (catalogSection.getType().equals("channel") || catalogSection.getType().equals("category")) {
            Picasso.with(context).load(catalogSection.getIcon()).fit().centerCrop()
                    .transform(new CircleTransform()).into(headerHolder.icon);
        }
        headerHolder.name.setText(catalogSection.getName());
        headerHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (catalogSection.getType().equals("channel")) {
                    Intent communityIntent = new Intent(context, CommunityActivity.class);
                    communityIntent.putExtra(CommunityActivity.COMMUNITY_ID, catalogSection.getId());
                    communityIntent.putExtra(CommunityActivity.COMMUNITY_ID, catalogSection.getName());
                    context.startActivity(communityIntent);
                }
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
        footerHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                footerHolder.expandView.setVisibility(View.GONE);
                if (expanded) {
                    Intent sectionIntent = new Intent(context, CatalogSectionActivity.class);
                    sectionIntent.putExtra(CatalogSectionActivity.SECTION_ID, catalogSection.getId());
                    sectionIntent.putExtra(CatalogSectionActivity.SECTION_FROM, catalogSection.getNext());
                    sectionIntent.putExtra(CatalogSectionActivity.SECTION_TITLE, catalogSection.getName());
                    context.startActivity(sectionIntent);


                }
                expanded = true;
                sectionAdapter.notifyDataSetChanged();
                if (!catalogSection.getId().equals("series") && !catalogSection.getId().equals("top")) {
                    footerHolder.footerText.setVisibility(View.VISIBLE);
                }
            }
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
        ImageView icon;
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
        ImageView expandView;
        @Bind(R.id.rootView)
        LinearLayout rootView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
