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
import com.example.frost.vkvideomanager.activity.AlbumActivity;
import com.example.frost.vkvideomanager.activity.CatalogSectionActivity;
import com.example.frost.vkvideomanager.model.Album;
import com.example.frost.vkvideomanager.model.CatalogSection;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.example.frost.vkvideomanager.utils.TimeConverter;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
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

    CatalogSection catalogSection;
    Context context;
    VKList<VKApiVideo> videoList;
    List<Album> albumList;
    boolean expanded;
    SectionedRecyclerViewAdapter sectionAdapter;

    public CatalogSectionAdapter(Context context, CatalogSection catalogSection, SectionedRecyclerViewAdapter sectionAdapter) {
        super(R.layout.catalog_section_header, R.layout.catalog_section_footer, R.layout.item_video);
        this.catalogSection = catalogSection;
        this.context = context;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {
//        return catalogBlock.getVideoList().size();
        return expanded ? 10 : 3;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        if (catalogSection.getId().equals("series")) {
            albumList = catalogSection.getAlbumList();
            itemHolder.title.setText(albumList.get(position).getTitle());
            itemHolder.duration.setText(String.valueOf(albumList.get(position).getCount()));
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
            Picasso.with(context).load(videoList.get(position).photo_320).fit().centerCrop().into(itemHolder.imageVideo);
            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VKRequest videoRequest = VKApi.video().get(VKParameters.from("videos",
                            videoList.get(position).owner_id + "_"
                                    + videoList.get(position).id + "_"
                    ));
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
                    sectionIntent.putExtra("sectionId", catalogSection.getId());
                    sectionIntent.putExtra("from", catalogSection.getNext());
                    sectionIntent.putExtra("sectionTitle", catalogSection.getName());
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
