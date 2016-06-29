package com.frost.vkvideomanager.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private VKList<VKApiUser> friendList;
    private Context context;
    private ItemClickListener itemClickListener;

    public FriendAdapter(Context context, VKList<VKApiUser> friendList, ItemClickListener itemClickListener) {
        this.context = context;
        this.friendList = friendList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_community, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        VKApiUser friend = friendList.get(position);
        holder.name.setText(String.format("%s %s", friend.first_name, friend.last_name));
        Picasso.with(context).load(friend.photo_100).fit().centerCrop()
                .transform(new CircleTransform()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.icon)
        ImageView avatar;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.friendClicked(v, getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void friendClicked(View v, int position);
    }
}
