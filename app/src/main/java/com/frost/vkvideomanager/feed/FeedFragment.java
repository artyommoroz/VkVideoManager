package com.frost.vkvideomanager.feed;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class FeedFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, List<FeedSectionModel>, FeedView, FeedPresenter>
        implements FeedView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private SectionedRecyclerViewAdapter adapter;

    public static FeedFragment newInstance() {
        return new FeedFragment();
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                presenter.loadMoreFeedSections();
            }
        });

        contentView.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }

    @Override
    public FeedPresenter createPresenter() {
        return new FeedPresenter();
    }

    @Override
    public void setData(List<FeedSectionModel> data) {
        adapter = new SectionedRecyclerViewAdapter();
        for (int i = 0; i < data.size(); i++) {
            FeedSection feedSection = new FeedSection(getActivity(), data.get(i), getFragmentManager());
            adapter.addSection(feedSection);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public List<FeedSectionModel> getData() {
        return presenter.getFeedSections();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadFeedSections(pullToRefresh);
    }

    @Override
    public void moreVideosLoaded(List<FeedSectionModel> feedSectionModels, int oldSize) {
        for (int i = 0; i < feedSectionModels.size(); i++) {
            FeedSection feedSection = new FeedSection(getActivity(), feedSectionModels.get(i), getFragmentManager());
            adapter.addSection(feedSection);
        }
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, oldSize - 1);
    }

    @Override
    public LceViewState<List<FeedSectionModel>, FeedView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
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
}
