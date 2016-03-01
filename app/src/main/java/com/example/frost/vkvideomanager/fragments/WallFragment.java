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
import com.example.frost.vkvideomanager.adapters.WallAdapter;
import com.example.frost.vkvideomanager.network.Parser;
import com.example.frost.vkvideomanager.pojo.WallVideo;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WallFragment extends Fragment implements WallAdapter.ItemClickListener {

    @Bind(R.id.recyclerViewVideo)
    RecyclerView recyclerView;

    private List<WallVideo> wallVideoList;
    private int offset = 100;
    private OnWallVideoSelectedListener wallVideoSelectedListener;
    private WallAdapter wallAdapter;

    public WallFragment() {
        // Required empty public constructor
    }

    public static WallFragment newInstance() {
        WallFragment fragment = new WallFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
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
                Log.d("WallVideoListSizeOffset", String.valueOf(offset));
                VKRequest wallRequest = VKApi.wall().get(VKParameters.from(
//                        VKApiConst.OWNER_ID, 4171317,
                        VKApiConst.OWNER_ID, 37052706,
                        VKApiConst.OFFSET, offset,
                        VKApiConst.COUNT, 50,
                        VKApiConst.EXTENDED, 1));
                wallRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        List<WallVideo> loadedVideo = Parser.parseWall(response);
                        Log.d("WallVideoListSizeLoaded", String.valueOf(loadedVideo.size()));
                        wallVideoList.addAll(loadedVideo);
                        int curSize = wallAdapter.getItemCount();
                        wallAdapter.notifyItemRangeInserted(curSize, wallVideoList.size() - 1);
                        Log.d("WallVideoListSize", String.valueOf(wallVideoList.size()));
                    }
                });
                offset += 50;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//        }

        VKRequest wallRequest = VKApi.wall().get(VKParameters.from(
//                VKApiConst.OWNER_ID, 4171317,37052706
                VKApiConst.OWNER_ID, 37052706,
                VKApiConst.COUNT, 100,
                "filter", "all",
                VKApiConst.EXTENDED, 1));
        wallRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                wallVideoList = Parser.parseWall(response);
                wallAdapter = new WallAdapter(getActivity(), wallVideoList, WallFragment.this);
                recyclerView.setAdapter(wallAdapter);
                Log.d("WallVideoListSize", String.valueOf(wallVideoList.size()));
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnWallVideoSelectedListener) {
            wallVideoSelectedListener = (OnWallVideoSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        wallVideoSelectedListener = null;
    }

    @Override
    public void itemClicked(View v, int position) {
        if (wallVideoSelectedListener != null) {
            VKRequest videoRequest =  VKApi.video().get(VKParameters.from(
                            "videos", wallVideoList.get(position).getVkApiVideo().owner_id
                                    + "_" + wallVideoList.get(position).getVkApiVideo().id
                                    + "_" + wallVideoList.get(position).getVkApiVideo().access_key));
            videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                    Uri videoUri = Uri.parse(vkApiVideo.player);
                    wallVideoSelectedListener.onWallVideoSelected(videoUri);
                }
            });
        }
    }

    public interface OnWallVideoSelectedListener {
        void onWallVideoSelected(Uri videoUri);
    }
}