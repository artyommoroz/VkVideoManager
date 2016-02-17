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

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.PostAdapter;
import com.example.frost.vkvideomanager.network.Parser;
import com.example.frost.vkvideomanager.pojo.Wall;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WallFragment extends Fragment implements PostAdapter.ItemClickListener {

    @Bind(R.id.recyclerViewVideo)
    RecyclerView recyclerView;
    Wall wall;
    private OnWallVideoSelectedListener wallVideoSelectedListener;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//        }

        VKRequest wallRequest = VKApi.wall().get(VKParameters.from(VKApiConst.OWNER_ID, "4171317" , VKApiConst.COUNT, "99",
//        VKRequest wallRequest = VKApi.wall().get(VKParameters.from(VKApiConst.OWNER_ID, "114658987" ,VKApiConst.COUNT, "2",
                VKApiConst.EXTENDED, 1));
        wallRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                wall = Parser.parseWall(response);
                PostAdapter wallAdapter = new PostAdapter(getActivity(), wall, WallFragment.this);
                recyclerView.setAdapter(wallAdapter);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnWallVideoSelectedListener) {
            wallVideoSelectedListener = (OnWallVideoSelectedListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
            VKRequest videoRequest =  VKApi.video().get(VKParameters.from("videos",
                            wall.getVideoList().get(position).owner_id + "_"
                                    + wall.getVideoList().get(position).id + "_"
                                    + wall.getVideoList().get(position).access_key)
                    );

            videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    Log.d("WallyResponse", response.responseString);
                    Log.d("WallyRequest", response.request.toString());
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
