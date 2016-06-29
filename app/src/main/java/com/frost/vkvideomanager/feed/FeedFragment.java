package com.frost.vkvideomanager.feed;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.NetworkChecker;
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

public class FeedFragment extends BaseFragment {

    private static final String NEWSFEED_REQUEST = "newsfeed.get";

    private List<FeedSection> feedSectionList = new ArrayList<>();
    private SectionedRecyclerViewAdapter sectionAdapter;
    private String startFrom;
    private String startFromFirst;

    public FeedFragment() {}

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFeed();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (NetworkChecker.isOnline(getActivity())) {
            recyclerView.setAdapter(sectionAdapter);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                VKRequest feedRequest = new VKRequest(NEWSFEED_REQUEST, VKParameters.from(
                        VKApiConst.FILTERS, "video",
                        VKApiConst.FEED_START_FROM, startFrom,
                        VKApiConst.COUNT, 100));
                feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            startFrom = response.json.optJSONObject("response").getString("next_from");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!startFrom.equals(startFromFirst)) {
                            List<FeedSection> loadedFeedSectionList = Parser.parseNewsFeed(response);
                            for (int i = 0; i < loadedFeedSectionList.size(); i++) {
                                FeedSectionAdapter feedSectionAdapter = new FeedSectionAdapter(getActivity(),
                                        loadedFeedSectionList.get(i), getFragmentManager());
                                sectionAdapter.addSection(feedSectionAdapter);
                            }
                            int curSize = sectionAdapter.getItemCount();
                            sectionAdapter.notifyItemRangeInserted(curSize, feedSectionList.size() - 1);
                        }
                    }
                });
            }
        });

        swipeRefresh.setOnRefreshListener(() -> updateFeed());

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateFeed();
        });
    }

    private void updateFeed() {
        VKRequest feedRequest = new VKRequest(NEWSFEED_REQUEST, VKParameters.from(
                VKApiConst.FILTERS, "video",
                VKApiConst.COUNT, 100));
        feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (feedSectionList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (feedSectionList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.no_connection_snack_button), view -> {
                                updateFeed();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
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
                            feedSectionList.get(i), getFragmentManager());
                    sectionAdapter.addSection(feedSectionAdapter);
                }
                recyclerView.setAdapter(sectionAdapter);
            }
        });
    }

}
