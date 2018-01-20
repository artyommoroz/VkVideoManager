package com.frost.vkvideomanager.wall;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageButton;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.network.NetworkChecker;
import com.frost.vkvideomanager.network.Parser;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.frost.vkvideomanager.BaseFragment;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;


public class WallFragment extends BaseFragment implements WallVideoAdapter.ItemClickListener {

    private static final String OWNER_ID = "ownerId";

    private List<WallVideo> wallVideoList = new ArrayList<>();
    private WallVideoAdapter wallVideoAdapter;
    private int offset = 100;
    private int ownerId;
    private boolean noVideos;

    public WallFragment() {}

    public static WallFragment newInstance(int ownerId) {
        WallFragment fragment = new WallFragment();
        Bundle args = new Bundle();
        args.putInt(OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ownerId = getArguments().getInt(OWNER_ID);
        }

        updateWallVideoList();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (NetworkChecker.isOnline(getActivity())) {
            recyclerView.setAdapter(wallVideoAdapter);
        }

        if (noVideos) {
            noVideosView.setText(R.string.no_wall_videos);
            noVideosView.setVisibility(View.VISIBLE);
        } else {
            noVideosView.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                VKRequest wallRequest = VKApi.wall().get(VKParameters.from(
                        VKApiConst.OWNER_ID, ownerId,
                        VKApiConst.OFFSET, offset,
                        VKApiConst.COUNT, 100,
                        VKApiConst.EXTENDED, 1));
                wallRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        List<WallVideo> loadedVideo = Parser.parseWall(response);
                        wallVideoList.addAll(loadedVideo);
                        int curSize = wallVideoAdapter.getItemCount();
                        wallVideoAdapter.notifyItemRangeInserted(curSize, wallVideoList.size() - 1);
                    }
                });
                offset += 100;
            }
        });

        swipeRefresh.setOnRefreshListener(this::updateWallVideoList);

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateWallVideoList();
        });
    }

    private void updateWallVideoList() {
        VKRequest wallRequest = VKApi.wall().get(VKParameters.from(
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.COUNT, 100,
                VKApiConst.EXTENDED, 1));
        wallRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if (wallVideoList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (wallVideoList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.no_connection_snack_button), view -> {
                                updateWallVideoList();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                wallVideoList.clear();
                wallVideoList = Parser.parseWall(response);
                wallVideoAdapter = new WallVideoAdapter(getActivity(), wallVideoList, WallFragment.this);
                recyclerView.setAdapter(wallVideoAdapter);
                if (wallVideoList.isEmpty()) {
                    noVideosView.setText(R.string.no_wall_videos);
                    noVideosView.setVisibility(View.VISIBLE);
                    noVideos = true;
                }
            }
        });
    }

    @Override
    public void itemClicked(View v, final int position) {
        if (v instanceof CardView) {
            VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                    VKApiConst.VIDEOS, wallVideoList.get(position).getVideo().owner_id
                            + "_" + wallVideoList.get(position).getVideo().id
                            + "_" + wallVideoList.get(position).getVideo().access_key));
            videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                    UrlHelper.playVideo(getActivity(), vkApiVideo.player);
                }
            });
        } else if (v instanceof ImageButton) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(getActivity(), wallVideoList.get(position).getVideo());
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(getFragmentManager(), wallVideoList.get(position).getVideo());
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

}