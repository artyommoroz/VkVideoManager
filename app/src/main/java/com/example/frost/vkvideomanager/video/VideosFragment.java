package com.example.frost.vkvideomanager.video;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.frost.vkvideomanager.player.UrlHelper;
import com.example.frost.vkvideomanager.utils.EndlessScrollListener;
import com.example.frost.vkvideomanager.MainActivity;
import com.example.frost.vkvideomanager.player.PlayerActivity;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.network.AdditionRequests;
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


public class VideosFragment extends Fragment implements VideoAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private static final String TAG = "VideosFragment";

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private boolean isCreated;
    private int albumId;
    private int ownerId;
    private boolean isMy;
    private int offset;

    public VideosFragment() {}

    public static VideosFragment newInstance(int ownerId, int albumId, boolean isMy) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putInt("albumId", albumId);
        args.putInt("ownerId", ownerId);
        args.putBoolean("isMy", isMy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getInt("albumId");
            ownerId = getArguments().getInt("ownerId");
            isMy = getArguments().getBoolean("isMy");
        }

        if(!(getActivity() instanceof MainActivity)) {
            setRetainInstance(true);
        }

        updateVideoList();
        isCreated = true;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        orientation = getActivity().getResources().getConfiguration().orientation;
//        Log.d(TAG, "onAttach: orientation " + String.valueOf(orientation));
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        videoAdapter = new VideoAdapter(getActivity(), videoList, VideosFragment.this);
        recyclerView.setAdapter(videoAdapter);

        if (isCreated) {
            progressBar.setVisibility(View.VISIBLE);
            isCreated = false;
        } else {
            progressBar.setVisibility(View.GONE);
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

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateVideoList();
            }
        });
    }

    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }

    @Override
    public void itemClicked(View v, final int position) {
        if (v instanceof RelativeLayout) {
            String videoUri = videoList.get(position).player;
            UrlHelper.playVideo(getActivity(), videoUri);
        } else if (v instanceof ImageButton) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            if (isMy) {
                popupMenu.inflate(R.menu.popup_menu_my_video);
            } else {
                popupMenu.inflate(R.menu.popup_menu_video);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.add:
                            AdditionRequests.addVideo(getActivity(), videoList.get(position));
                            return true;
                        case R.id.add_to_album:
                            AdditionRequests.addVideoToAlbum(getFragmentManager(), videoList.get(position));
                            return true;
                        case R.id.delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Вы уверены, что хотите удалить эту видеозапись?")
                                    .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            VKRequest deleteRequest = VKApi.video().removeFromAlbum(VKParameters.from(
                                                    VKApiConst.VIDEO_ID, videoList.get(position).id,
                                                    VKApiConst.OWNER_ID, videoList.get(position).owner_id,
                                                    VKApiConst.ALBUM_IDS, "-1, -2"));
                                            deleteRequest.executeWithListener(new VKRequest.VKRequestListener() {
                                                @Override
                                                public void onComplete(VKResponse response) {
                                                    super.onComplete(response);
//                                                    if (response.json.optInt("response") == 1) {
                                                        Toast.makeText(getActivity(), "Видеозапись " + videoList.get(position).title
                                                                + " была успешно удалена", Toast.LENGTH_SHORT).show();
                                                        videoList.remove(position);
                                                        videoAdapter.notifyItemRemoved(position);
                                                        videoAdapter.notifyItemRangeChanged(position, videoAdapter.getItemCount());
//                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            return true;
                        default:
                            return false;
                    }
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
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                videoList.clear();
                videoList = Parser.parseVideos(response);
                offset = videoList.size();
                videoAdapter = new VideoAdapter(getActivity(), videoList, VideosFragment.this);
                recyclerView.setAdapter(videoAdapter);
            }
        });
    }

    public String getName() {
        return "ДОБАВЛЕННЫЕ";
    }

}
