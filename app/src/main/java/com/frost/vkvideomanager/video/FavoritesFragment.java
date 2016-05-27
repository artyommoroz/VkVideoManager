package com.frost.vkvideomanager.video;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.frost.vkvideomanager.BaseFragment;
import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.network.Parser;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.ButterKnife;


public class FavoritesFragment extends BaseFragment implements VideoAdapter.ItemClickListener {

    private static final String FAVORITES_REQUEST = "fave.getVideos";

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private int offset;
    private boolean noConnection;
    private boolean noVideos;

    public FavoritesFragment() {}

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateVideoList();
        isCreated = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoAdapter = new VideoAdapter(getActivity(), videoList, FavoritesFragment.this);
        recyclerView.setAdapter(videoAdapter);

        if (noConnection  && videoList.isEmpty()) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else if (!noConnection  && videoList.size() > 0) {
            noConnectionView.setVisibility(View.GONE);
        }

        if (noVideos) {
            noVideosView.setText(R.string.no_fav_videos);
            noVideosView.setVisibility(View.VISIBLE);
        } else {
            noVideosView.setVisibility(View.GONE);
        }

        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    loadMore();
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.addOnScrollListener(new EndlessScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    loadMore();
                }
            });
        }

        swipeRefresh.setOnRefreshListener(() -> updateVideoList());

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateVideoList();
        });
    }

    private void loadMore() {
        offset += offset;
        VKRequest videoRequest = new VKRequest(FAVORITES_REQUEST, VKParameters.from(
                VKApiConst.OFFSET, offset,
                VKApiConst.EXTENDED, 1));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                noConnectionView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                noConnection = true;
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                videoList.addAll(Parser.parseVideos(response));
                int curSize = videoAdapter.getItemCount();
                videoAdapter.notifyItemRangeInserted(curSize, videoList.size() - 1);
            }
        });
    }


    private void updateVideoList() {
        VKRequest videoRequest;
        videoRequest = new VKRequest(FAVORITES_REQUEST, VKParameters.from(VKApiConst.EXTENDED, 1));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                noConnection = true;
                if (videoList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (videoList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.no_connection_snack_button), view -> {
                                updateVideoList();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                videoList.clear();
                videoList = Parser.parseVideos(response);
                offset = videoList.size();
                videoAdapter = new VideoAdapter(getActivity(), videoList, FavoritesFragment.this);
                recyclerView.setAdapter(videoAdapter);
                if (videoList.isEmpty()) {
                    noVideosView.setText(R.string.no_fav_videos);
                    noVideosView.setVisibility(View.VISIBLE);
                    noVideos = true;
                }
            }
        });
    }

    @Override
    public void itemClicked(View v, final int position) {
        if (v instanceof RelativeLayout) {
            String videoUri = videoList.get(position).player;
            UrlHelper.playVideo(getActivity(), videoUri);
        } else if (v instanceof ImageButton){
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(getActivity(), videoList.get(position));
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(getFragmentManager(), videoList.get(position));
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

}
