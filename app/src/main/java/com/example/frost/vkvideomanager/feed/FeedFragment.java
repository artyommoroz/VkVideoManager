package com.example.frost.vkvideomanager.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.EndlessScrollListener;
import com.example.frost.vkvideomanager.R;
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

public class FeedFragment extends Fragment {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private List<FeedSection> feedSectionList = new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    private String startFrom;
    private String startFromFirst;

    public FeedFragment() {}

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                VKRequest feedRequest = new VKRequest("newsfeed.get", VKParameters.from(
                        VKApiConst.FILTERS, "video",
                        VKApiConst.FEED_START_FROM, startFrom,
                        VKApiConst.COUNT, 20));
                feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            startFrom = response.json.optJSONObject("response").getString("next_from");
                            Log.d("StartFrom", startFrom);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!startFrom.equals(startFromFirst)) {
                            List<FeedSection> loadedFeedSectionList = Parser.parseNewsFeed(response);
                            for (int i = 0; i < loadedFeedSectionList.size(); i++) {
                                FeedSectionAdapter feedSectionAdapter = new FeedSectionAdapter(getActivity(),
                                        loadedFeedSectionList.get(i), sectionAdapter);
                                sectionAdapter.addSection(feedSectionAdapter);
                            }
                            int curSize = sectionAdapter.getItemCount();
                            sectionAdapter.notifyItemRangeInserted(curSize, feedSectionList.size() - 1);
                        }
                    }
                });
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFeed();
    }

    private void updateFeed() {
        VKRequest feedRequest = new VKRequest("newsfeed.get", VKParameters.from(
                VKApiConst.FILTERS, "video",
                VKApiConst.COUNT, 20));
        feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                feedSectionList.clear();
                try {
                    startFromFirst = response.json.optJSONObject("response").getString("next_from");
                    startFrom = response.json.optJSONObject("response").getString("next_from");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                feedSectionList = Parser.parseNewsFeed(response);
                sectionAdapter = new SectionedRecyclerViewAdapter();
                for (int i = 0; i < feedSectionList.size(); i++) {
                    FeedSectionAdapter feedSectionAdapter = new FeedSectionAdapter(getActivity(),
                            feedSectionList.get(i), sectionAdapter);
                    sectionAdapter.addSection(feedSectionAdapter);
                }
                recyclerView.setAdapter(sectionAdapter);
            }
        });
    }
}
