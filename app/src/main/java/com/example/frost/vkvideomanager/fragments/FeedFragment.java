package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
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
import com.example.frost.vkvideomanager.adapters.FeedAdapter;
import com.example.frost.vkvideomanager.adapters.FeedSectionAdapter;
import com.example.frost.vkvideomanager.model.FeedSection;
import com.example.frost.vkvideomanager.model.FeedVideo;
import com.example.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class FeedFragment extends Fragment implements FeedAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private OnFeedVideoSelectedListener feedVideoSelectedListener;
    private List<FeedVideo> feedVideoList;
    private List<FeedSection> feedSectionList;
    private FeedAdapter feedAdapter;
    SectionedRecyclerViewAdapter sectionAdapter;
    private String startFrom;
    private String startFromFirst;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
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
//        recyclerView.addItemDecoration(new FeedItemDecoration(8));
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
//                VKRequest feedRequest = new VKRequest("newsfeed.get", VKParameters.from(
//                        VKApiConst.FILTERS, "post",
//                        VKApiConst.COUNT, 90,
//                        "start_from", startFrom
//                ));
//                feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
//                    @Override
//                    public void onComplete(VKResponse response) {
//                        super.onComplete(response);
//                        try {
//                            startFrom = response.json.optJSONObject("response").getString("next_from");
//                            Log.d("FeedStart", startFrom);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        if (!startFrom.equals(startFromFirst)) {
//                            feedVideoList.addAll(Parser.parseNewsFeed(response));
//                            Log.d("FeedVideoListSize", String.valueOf(feedVideoList.size()));
//                            int curSize = feedAdapter.getItemCount();
//                            feedAdapter.notifyItemRangeInserted(curSize, feedVideoList.size() - 1);
//                        }
//                    }
//                });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKRequest feedRequest = new VKRequest("newsfeed.get", VKParameters.from(
                VKApiConst.FILTERS, "video"
//                VKApiConst.COUNT, 90
        ));
        feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                try {
                    startFromFirst = response.json.optJSONObject("response").getString("next_from");
                    startFrom = response.json.optJSONObject("response").getString("next_from");
                    Log.d("FeedStartFirst", startFromFirst);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                feedSectionList = Parser.parseNewsFeed(response);
                sectionAdapter = new SectionedRecyclerViewAdapter();
                for (int i = 0; i < feedSectionList.size(); i++) {
                    FeedSectionAdapter feedSectionAdapter = new FeedSectionAdapter(getActivity(), feedSectionList.get(i), sectionAdapter);
                    sectionAdapter.addSection(feedSectionAdapter);
                }
                recyclerView.setAdapter(sectionAdapter);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFeedVideoSelectedListener) {
            feedVideoSelectedListener = (OnFeedVideoSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        feedVideoSelectedListener = null;
    }

    @Override
    public void itemClicked(View v, int position) {
        if (feedVideoSelectedListener != null) {
            VKRequest videoRequest =  VKApi.video().get(VKParameters.from("videos",
                            feedVideoList.get(position).getVkApiVideo().owner_id + "_"
                                    + feedVideoList.get(position).getVkApiVideo().id + "_"
                                    + feedVideoList.get(position).getVkApiVideo().access_key)
            );
            videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                    Uri videoUri = Uri.parse(vkApiVideo.player);
                    feedVideoSelectedListener.onFeedVideoSelected(videoUri);
                }
            });
        }
    }

    public interface OnFeedVideoSelectedListener {
        void onFeedVideoSelected(Uri videoUri);
    }
}
