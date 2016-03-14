package com.example.frost.vkvideomanager.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.EndlessScrollListener;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.CatalogSectionAdapter;
import com.example.frost.vkvideomanager.model.CatalogSection;
import com.example.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class CatalogFragment extends Fragment {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    SectionedRecyclerViewAdapter sectionAdapter;

    private List<CatalogSection> catalogSectionList = new ArrayList<>();
    private String next;

    public CatalogFragment() {}

    public static CatalogFragment newInstance() {
        CatalogFragment fragment = new CatalogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new FeedItemDecoration(8));
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                VKRequest catalogRequest = new VKRequest("video.getCatalog", VKParameters.from(
//                        VKApiConst.COUNT, 5,
                        "items_count", 10,
                        "from", next,
                        VKApiConst.EXTENDED, 1,
                        VKApiConst.FILTERS, "other"
                ));
                catalogRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        List<CatalogSection> loadedCatalogSectionList = Parser.parseCatalog(response);
                        for (int i = 0; i < loadedCatalogSectionList.size(); i++) {
                            CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(getActivity(), loadedCatalogSectionList.get(i), sectionAdapter);
                            sectionAdapter.addSection(catalogSectionAdapter);
                        }
                        try {
                            next = response.json.optJSONObject("response").getString("next");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("next", next);
                        catalogSectionList.addAll(loadedCatalogSectionList);
                        if (catalogSectionList.size() <= 135) {
                            int curSize = sectionAdapter.getItemCount();
                            sectionAdapter.notifyItemRangeInserted(curSize, catalogSectionList.size() - 1);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKRequest catalogRequest = new VKRequest("video.getCatalog", VKParameters.from(
                "items_count", 10,
                VKApiConst.EXTENDED, 1,
                VKApiConst.FILTERS, "other, series, ugc, top"
        ));
        catalogRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                try {
                    next = response.json.optJSONObject("response").getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catalogSectionList = Parser.parseCatalog(response);
                Log.d("nextFirst", next);
                sectionAdapter = new SectionedRecyclerViewAdapter();
                for (int i = 0; i < catalogSectionList.size(); i++) {
                    CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(getActivity(), catalogSectionList.get(i), sectionAdapter);
                    sectionAdapter.addSection(catalogSectionAdapter);
                }
                recyclerView.setAdapter(sectionAdapter);
            }
        });
    }
}
