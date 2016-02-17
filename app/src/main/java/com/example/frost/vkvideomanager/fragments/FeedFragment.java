package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.PostAdapter;
import com.example.frost.vkvideomanager.network.Parser;
import com.example.frost.vkvideomanager.pojo.NewsFeed;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedFragment extends Fragment implements PostAdapter.ItemClickListener {

    @Bind(R.id.recyclerViewVideo)
    RecyclerView recyclerView;
    NewsFeed newsFeed;
    private OnFeedVideoSelectedListener feedVideoSelectedListener;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VKRequest feedRequest = new VKRequest("newsfeed.get", VKParameters.from(
                VKApiConst.FILTERS, "post",
//                VKApiConst.COUNT, "20"
                "start_time", "1455643800",
                "end_time", "1455650100"
        ));
        feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                NewsFeed newsFeed = Parser.parseNewsFeed(response);
                PostAdapter recyclerAdapter = new PostAdapter(getActivity(), newsFeed, FeedFragment.this);
                recyclerView.setAdapter(recyclerAdapter);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFeedVideoSelectedListener) {
            feedVideoSelectedListener = (OnFeedVideoSelectedListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
                            newsFeed.getVideoList().get(position).owner_id + "_"
                                    + newsFeed.getVideoList().get(position).id + "_"
                                    + newsFeed.getVideoList().get(position).access_key)
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
