package com.frost.vkvideomanager.album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albums;
    private Context context;
    private ItemClickListener itemClickListener;

    public AlbumAdapter(Context context, List<Album> albums, ItemClickListener itemClickListener) {
        this.context = context;
        this.albums = albums;
        this.itemClickListener = itemClickListener;
    }

    public AlbumAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.title.setText(album.getTitle());
        holder.count.setText(context.getString(R.string.album_number_of_videos, album.getCount()));
        if (album.getCount() > 0) {
            Picasso.with(context).load(album.getPhoto()).fit().centerCrop().into(holder.image);
            if (album.getPrivacy().equals("only_me") || album.getPrivacy().equals("nobody")) {
                holder.privacy.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.count)
        TextView count;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.imageAlbum)
        ImageView image;
        @Bind(R.id.privacy)
        ImageView privacy;
        @Bind(R.id.moreButton)
        ImageButton moreButton;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            moreButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
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
