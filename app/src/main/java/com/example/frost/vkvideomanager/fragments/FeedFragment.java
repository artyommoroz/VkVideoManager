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

import com.example.frost.vkvideomanager.EndlessScrollListener;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.FeedAdapter;
import com.example.frost.vkvideomanager.network.Parser;
import com.example.frost.vkvideomanager.pojo.FeedVideo;
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

public class FeedFragment extends Fragment implements FeedAdapter.ItemClickListener {

    @Bind(R.id.recyclerViewVideo)
    RecyclerView recyclerView;
    private OnFeedVideoSelectedListener feedVideoSelectedListener;
    private List<FeedVideo> feedVideoList;
    private FeedAdapter feedAdapter;
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
        return inflater.inflate(R.layout.fragment_video, container, false);
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
                        VKApiConst.FILTERS, "post",
                        VKApiConst.COUNT, 90,
                        "start_from", startFrom
                ));
                feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            startFrom = response.json.optJSONObject("response").getString("next_from");
                            Log.d("FeedStart", startFrom);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!startFrom.equals(startFromFirst)) {
                            feedVideoList.addAll(Parser.parseNewsFeed(response));
                            Log.d("FeedVideoListSize", String.valueOf(feedVideoList.size()));
                            int curSize = feedAdapter.getItemCount();
                            feedAdapter.notifyItemRangeInserted(curSize, feedVideoList.size() - 1);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKRequest feedRequest = new VKRequest("newsfeed.get", VKParameters.from(
                VKApiConst.FILTERS, "post",
                VKApiConst.COUNT, 90
        ));
        feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    startFromFirst = response.json.optJSONObject("response").getString("next_from");
                    startFrom = response.json.optJSONObject("response").getString("next_from");
                    Log.d("FeedStartFirst", startFromFirst);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                feedVideoList = Parser.parseNewsFeed(response);
                Log.d("FeedVideoListSize", String.valueOf(feedVideoList.size()));
                feedAdapter = new FeedAdapter(getActivity(), feedVideoList, FeedFragment.this);
                recyclerView.setAdapter(feedAdapter);
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
