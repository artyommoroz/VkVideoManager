package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
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
import com.example.frost.vkvideomanager.adapters.VideoAdapter;
import com.example.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class VideosFragment extends BaseFragment implements VideoAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private OnVideoSelectedListener videoSelectedListener;
    private int albumId;
    private int ownerId;
    private boolean isFav;
    private int offset;

    public VideosFragment() {}

    public static VideosFragment newInstance(int ownerId, int albumId, boolean isFav) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putInt("albumId", albumId);
        args.putInt("ownerId", ownerId);
        args.putBoolean("isFav", isFav);
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
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                offset += offset;
                VKRequest videoRequest;
                if (isFav) {
                    videoRequest = new VKRequest("fave.getVideos", VKParameters.from(VKApiConst.OFFSET, offset,
                            VKApiConst.EXTENDED, 1));
                } else {
                    videoRequest = VKApi.video().get(VKParameters.from(
                            VKApiConst.OWNER_ID, ownerId,
                            VKApiConst.ALBUM_ID, albumId,
                            VKApiConst.OFFSET, offset
                    ));
                }
                videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        videoList.addAll(Parser.parseVideos(response));
                        int curSize = videoAdapter.getItemCount();
                        videoAdapter.notifyItemRangeInserted(curSize, videoList.size() - 1);
                    }
                });
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            albumId = getArguments().getInt("albumId");
            ownerId = getArguments().getInt("ownerId");
            isFav = getArguments().getBoolean("isFav");
        }

        VKRequest videoRequest;
        if (isFav) {
            videoRequest = new VKRequest("fave.getVideos", VKParameters.from(VKApiConst.EXTENDED, 1));
        } else {
            videoRequest = VKApi.video().get(VKParameters.from(
                    VKApiConst.OWNER_ID, ownerId,
                    VKApiConst.ALBUM_ID, albumId
            ));
        }
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("VideoResponse", response.responseString);
                videoList = Parser.parseVideos(response);
                offset = videoList.size();
                videoAdapter = new VideoAdapter(getActivity(), videoList, VideosFragment.this);
                recyclerView.setAdapter(videoAdapter);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnVideoSelectedListener) {
            videoSelectedListener = (OnVideoSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        videoSelectedListener = null;
    }

    @Override
    public void itemClicked(View v, int position) {
        if (videoSelectedListener != null) {
            Uri videoUri = Uri.parse(videoList.get(position).player);
            videoSelectedListener.onVideoSelected(videoUri);
        }
    }

    public String getName() {
        return isFav ? "FAVORITES" : "ADDED";
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(Uri videoUri);
    }
}
