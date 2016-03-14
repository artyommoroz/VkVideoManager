package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.model.Album;
import com.squareup.picasso.Picasso;

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

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
//        if (!albums.get(position).getPrivacy().equals("all")) {
//            holder.privacy.setVisibility(View.VISIBLE);
//        }
        holder.title.setText(albums.get(position).getTitle());
        holder.count.setText(albums.get(position).getCount() + " видео");
        Picasso.with(context).load(albums.get(position).getPhoto()).fit().centerCrop().into(holder.image);
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

        public AlbumViewHolder(View itemView) {
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
