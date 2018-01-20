package com.frost.vkvideomanager.community;

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
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private VKList<VKApiCommunity> communities;
    private Context context;
    private ItemClickListener itemClickListener;

    public CommunityAdapter(Context context, VKList<VKApiCommunity> communities, ItemClickListener itemClickListener) {
        this.context = context;
        this.communities = communities;
        this.itemClickListener = itemClickListener;
    }

    public CommunityAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public VKList<VKApiCommunity> getCommunities() {
        return communities;
    }

    public void setCommunities(VKList<VKApiCommunity> communities) {
        this.communities = communities;
    }

    @Override
    public CommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommunityViewHolder holder, int position) {
        holder.name.setText(communities.get(position).name);
        Picasso.with(context).load(communities.get(position).photo_100).fit().centerCrop()
                .transform(new CircleTransform()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.icon)
        ImageView icon;

        public CommunityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
