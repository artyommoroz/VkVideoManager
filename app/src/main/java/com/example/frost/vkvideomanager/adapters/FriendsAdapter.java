package com.example.frost.vkvideomanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private VKList<VKApiUser> friends;
    private Context context;
    private ItemClickListener itemClickListener;

    public FriendsAdapter(Context context, VKList<VKApiUser> friends, ItemClickListener itemClickListener) {
        this.context = context;
        this.friends = friends;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsViewHolder holder, int position) {
        holder.name.setText(friends.get(position).first_name + " " + friends.get(position).last_name);
        Picasso.with(context).load(friends.get(position).photo_100).fit().centerCrop()
                .transform(new CircleTransform()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.icon)
        ImageView avatar;

        public FriendsViewHolder(View itemView) {
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
