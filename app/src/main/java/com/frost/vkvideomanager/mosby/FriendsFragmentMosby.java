package com.frost.vkvideomanager.mosby;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.friend.FriendActivity;
import com.frost.vkvideomanager.friend.FriendAdapter;
import com.frost.vkvideomanager.mosby.presenter.FriendsPresenter;
import com.frost.vkvideomanager.mosby.view.FriendsView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendsFragmentMosby extends MvpLceViewStateFragment<SwipeRefreshLayout, VKList<VKApiUser>, FriendsView, FriendsPresenter>
        implements FriendsView, SwipeRefreshLayout.OnRefreshListener, FriendAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.noneItemsView)
    TextView noneItemsView;

    private FriendAdapter adapter;

    public static FriendsFragmentMosby newInstance() {
        return new FriendsFragmentMosby();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        errorView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.ic_cloud_off_black_96dp), null, null);
        adapter = new FriendAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = orientation == Configuration.ORIENTATION_PORTRAIT ?
                new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        contentView.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        ButterKnife.unbind(this);
    }

    @Override
    public FriendsPresenter createPresenter() {
        return new FriendsPresenter();
    }

    @Override
    public void setData(VKList<VKApiUser> data) {
        adapter.setFriends(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public VKList<VKApiUser> getData() {
        return adapter == null ? null : adapter.getFriends();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadFriends(pullToRefresh);
    }

    @Override
    public LceViewState<VKList<VKApiUser>, FriendsView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        if (adapter.getFriends().isEmpty()) {
            noneItemsView.setVisibility(View.VISIBLE);
            noneItemsView.setText(getString(R.string.no_friends));
        }
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        contentView.setRefreshing(pullToRefresh);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.no_connection_message);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void friendClicked(View v, int position) {
        Intent friendIntent = new Intent(getActivity(), FriendActivity.class);
        friendIntent.putExtra(FriendActivity.FRIEND_ID, presenter.getSelectedFriend(position).id);
        friendIntent.putExtra(FriendActivity.FRIEND_FULL_NAME, String.format("%s %s",
                presenter.getSelectedFriend(position).first_name,
                presenter.getSelectedFriend(position).last_name));
        startActivity(friendIntent);
    }
}
