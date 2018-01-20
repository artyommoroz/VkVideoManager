package com.frost.vkvideomanager.community;

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
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunitiesFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, VKList<VKApiCommunity>, CommunitiesView, CommunitiesPresenter>
        implements CommunitiesView, SwipeRefreshLayout.OnRefreshListener, CommunityAdapter.ItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private CommunityAdapter adapter;

    public static CommunitiesFragment newInstance() {
        return new CommunitiesFragment();
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
        adapter = new CommunityAdapter(getActivity(), this);
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

    }

    @Override
    public CommunitiesPresenter createPresenter() {
        return new CommunitiesPresenter();
    }

    @Override
    public void setData(VKList<VKApiCommunity> data) {
        adapter.setCommunities(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public VKList<VKApiCommunity> getData() {
        return adapter == null ? null : adapter.getCommunities();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadCommunities(pullToRefresh);
    }

    @Override
    public LceViewState<VKList<VKApiCommunity>, CommunitiesView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        if (adapter.getCommunities().isEmpty()) {
            noneItemsView.setVisibility(View.VISIBLE);
            noneItemsView.setText(getString(R.string.no_communities));
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
    public void itemClicked(View v, int position) {
        Intent communityIntent = new Intent(getActivity(), CommunityActivity.class);
        communityIntent.putExtra(CommunityActivity.COMMUNITY_ID,
                presenter.getSelectedFriend(position).id * -1);
        communityIntent.putExtra(CommunityActivity.COMMUNITY_NAME,
                presenter.getSelectedFriend(position).name);
        startActivity(communityIntent);
    }
}
