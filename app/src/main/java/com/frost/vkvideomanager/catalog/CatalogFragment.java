package com.frost.vkvideomanager.catalog;

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


public class CatalogFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, List<CatalogSection>, CatalogView, CatalogPresenter>
        implements CatalogView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private SectionedRecyclerViewAdapter adapter;

    public static CatalogFragment newInstance() {
        return new CatalogFragment();
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
                presenter.loadMoreCatalogSections();
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
    public CatalogPresenter createPresenter() {
        return new CatalogPresenter();
    }

    @Override
    public void setData(List<CatalogSection> data) {
        adapter = new SectionedRecyclerViewAdapter();
        for (int i = 0; i < data.size(); i++) {
            CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(getActivity(), data.get(i));
            adapter.addSection(catalogSectionAdapter);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public List<CatalogSection> getData() {
        return presenter.getCatalogSections();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadFeedSections(pullToRefresh);
    }


    @Override
    public void moreCatalogSectionsLoaded(List<CatalogSection> catalogSections, int oldSize) {
        for (int i = 0; i < catalogSections.size(); i++) {
            CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(getActivity(), catalogSections.get(i));
            adapter.addSection(catalogSectionAdapter);
        }
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, oldSize - 1);
    }

    @Override
    public LceViewState<List<CatalogSection>, CatalogView> createViewState() {
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


