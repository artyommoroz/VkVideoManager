package com.frost.vkvideomanager.video;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.frost.vkvideomanager.BaseFragment;
import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.MainActivity;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.network.NetworkChecker;
import com.frost.vkvideomanager.network.Parser;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.ButterKnife;


public class VideosFragment extends BaseFragment implements VideoAdapter.ItemClickListener {

    private static final String ALBUM_ID = "albumId";
    private static final String OWNER_ID = "ownerId";
    private static final String IS_MY = "isMy";

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private boolean noVideos;
    private boolean isMy;
    private int albumId;
    private int ownerId;
    private int offset;
    private int clickedPosition;

    public VideosFragment() {}

    public static VideosFragment newInstance(int ownerId, int albumId, boolean isMy) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putInt(ALBUM_ID, albumId);
        args.putInt(OWNER_ID, ownerId);
        args.putBoolean(IS_MY, isMy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            albumId = getArguments().getInt(ALBUM_ID);
            ownerId = getArguments().getInt(OWNER_ID);
            isMy = getArguments().getBoolean(IS_MY);
        }

        updateVideoList();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (noVideos) {
            noVideosView.setText(R.string.no_added_videos);
            noVideosView.setVisibility(View.VISIBLE);
        } else {
            noVideosView.setVisibility(View.GONE);
        }

        if (NetworkChecker.isOnline(getActivity())) {
            recyclerView.setAdapter(videoAdapter);
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
        } else {
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

    @Override
    public void itemClicked(View v, final int position) {
        clickedPosition = position;
        if (v instanceof RelativeLayout) {
            String videoUrl = videoList.get(position).player;
            UrlHelper.playVideo(getActivity(), videoUrl);
        } else if (v instanceof ImageButton) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            if (isMy) {
                popupMenu.inflate(R.menu.popup_menu_my_video);
            } else {
                popupMenu.inflate(R.menu.popup_menu_video);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(getActivity(), videoList.get(position));
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(getFragmentManager(), videoList.get(position));
                        return true;
                    case R.id.delete:
                        showDeleteDialog();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

    private void loadMore() {
        offset += offset;
        VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.ALBUM_ID, albumId,
                VKApiConst.OFFSET, offset));
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

    private void updateVideoList() {
        VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.ALBUM_ID, albumId));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                if (error.errorCode == -105) {
                    swipeRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    if (videoList.isEmpty()) {
                        noConnectionView.setVisibility(View.VISIBLE);
                    } else if (videoList.size() > 0) {
                        Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                                .setAction(R.string.no_connection_snack_button, view -> {
                                    updateVideoList();
                                }).show();
                    }
                } else if (error.errorCode == -101) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    noVideosView.setText(R.string.no_added_videos);
                    noVideosView.setVisibility(View.VISIBLE);
                    noVideos = true;
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
                videoAdapter = new VideoAdapter(getActivity(), videoList, VideosFragment.this);
                recyclerView.setAdapter(videoAdapter);
                if (videoList.isEmpty()) {
                    noVideosView.setText(R.string.no_added_videos);
                    noVideosView.setVisibility(View.VISIBLE);
                    noVideos = true;
                }
            }
        });
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.video_dialog_delete_title)
                .setPositiveButton(R.string.video_dialog_delete_positive_button,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String ids = albumId == 0 ? "-1, -2" : String.valueOf(albumId);
                        VKRequest deleteRequest = VKApi.video().removeFromAlbum(VKParameters.from(
                                VKApiConst.VIDEO_ID, videoList.get(clickedPosition).id,
                                VKApiConst.OWNER_ID, videoList.get(clickedPosition).owner_id,
                                VKApiConst.ALBUM_IDS, ids));
                        deleteRequest.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Toast.makeText(getActivity(), getString(R.string.video_dialog_delete_success_toast,
                                        videoList.get(clickedPosition).title), Toast.LENGTH_SHORT).show();
                                videoList.remove(clickedPosition);
                                videoAdapter.notifyItemRemoved(clickedPosition);
                                videoAdapter.notifyItemRangeChanged(clickedPosition, videoAdapter.getItemCount());
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.video_dialog_delete_negative_button, (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).
                setTextColor(getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).
                setTextColor(getResources().getColor(R.color.colorPrimary));
    }

}
