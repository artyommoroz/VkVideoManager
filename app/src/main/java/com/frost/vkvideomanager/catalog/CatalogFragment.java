package com.frost.vkvideomanager.catalog;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.Parser;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.frost.vkvideomanager.BaseFragment;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class CatalogFragment extends BaseFragment {

//    @Bind(R.id.rootView)
//    RelativeLayout rootView;
//    @Bind(R.id.recyclerView)
//    RecyclerView recyclerView;
//    @Bind(R.id.progressBar)
//    ProgressBar progressBar;
//    @Bind(R.id.swipeRefresh)
//    SwipeRefreshLayout swipeRefresh;
//    @Bind(R.id.noConnectionView)
//    RelativeLayout noConnectionView;
//    @Bind(R.id.retryButton)
//    Button retryButton;

    private static final String CATALOG_REQUEST = "video.getCatalog";

    private SectionedRecyclerViewAdapter sectionAdapter;
    private List<CatalogSection> catalogSectionList = new ArrayList<>();
    private String next;
    private boolean noConnection;

    public CatalogFragment() {}

    public static CatalogFragment newInstance() {
        return new CatalogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateCatalog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(sectionAdapter);
        }

        if (noConnection  && catalogSectionList.isEmpty()) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else if (!noConnection  && catalogSectionList.size() > 0) {
            noConnectionView.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                VKRequest catalogRequest = new VKRequest(CATALOG_REQUEST, VKParameters.from(
                        VKApiConst.COUNT, 16,
                        VKApiConst.CATALOG_ITEMS_COUNT, 10,
                        VKApiConst.CATALOG_FROM, next,
                        VKApiConst.EXTENDED, 1,
                        VKApiConst.FILTERS, "other"));
                catalogRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        List<CatalogSection> loadedCatalogSectionList = Parser.parseCatalog(response);
                        for (int i = 0; i < loadedCatalogSectionList.size(); i++) {
                            CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(getActivity(),
                                    loadedCatalogSectionList.get(i));
                            sectionAdapter.addSection(catalogSectionAdapter);
                        }
                        try {
                            next = response.json.optJSONObject("response").getString("next");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catalogSectionList.addAll(loadedCatalogSectionList);
                        if (catalogSectionList.size() <= 135) {
                            int curSize = sectionAdapter.getItemCount();
                            sectionAdapter.notifyItemRangeInserted(curSize, catalogSectionList.size() - 1);
                        }
                    }
                });
            }
        });

        swipeRefresh.setOnRefreshListener(() -> updateCatalog());

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateCatalog();
        });
    }

    private void updateCatalog() {
        final VKRequest catalogRequest = new VKRequest(CATALOG_REQUEST, VKParameters.from(
                VKApiConst.COUNT, 16,
                VKApiConst.CATALOG_ITEMS_COUNT, 10,
                VKApiConst.EXTENDED, 1,
                VKApiConst.FILTERS, "other, ugc, top"));
        catalogRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                noConnection = true;
                if (catalogSectionList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (catalogSectionList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(R.string.no_connection_snack_button, view -> {
                                updateCatalog();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefresh.setRefreshing(false);
                catalogSectionList.clear();
                try {
                    next = response.json.optJSONObject("response").getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catalogSectionList = Parser.parseCatalog(response);
                sectionAdapter = new SectionedRecyclerViewAdapter();
                for (int i = 0; i < catalogSectionList.size(); i++) {
                    CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(getActivity(),
                            catalogSectionList.get(i));
                    sectionAdapter.addSection(catalogSectionAdapter);
                }
                recyclerView.setAdapter(sectionAdapter);
            }
        });
    }
}
