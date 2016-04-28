package com.frost.vkvideomanager.wall;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.BaseFragment;
import com.frost.vkvideomanager.MainActivity;
import com.frost.vkvideomanager.network.AdditionRequests;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WallFragment extends BaseFragment implements WallAdapter.ItemClickListener {

    @Bind(R.id.rootView)
    RelativeLayout rootView;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.noConnectionView)
    RelativeLayout noConnectionView;
    @Bind(R.id.retryButton)
    Button retryButton;
    @Bind(R.id.noVideosView)
    TextView noVideosView;

    private static final String OWNER_ID = "ownerId";

    private List<WallVideo> wallVideoList = new ArrayList<>();
    private int offset = 100;
    private WallAdapter wallAdapter;
    private int ownerId;
    private boolean isCreated;
    private boolean noConnection;
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
        if(!(getActivity() instanceof MainActivity)) {
            setRetainInstance(true);
        }

        updateWallVideoList();
        isCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        wallAdapter = new WallAdapter(getActivity(), wallVideoList, WallFragment.this);
        recyclerView.setAdapter(wallAdapter);

        if (isCreated) {
            progressBar.setVisibility(View.VISIBLE);
            isCreated = false;
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (noConnection  && wallVideoList.isEmpty()) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else if (!noConnection  && wallVideoList.size() > 0) {
            noConnectionView.setVisibility(View.GONE);
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
                        int curSize = wallAdapter.getItemCount();
                        wallAdapter.notifyItemRangeInserted(curSize, wallVideoList.size() - 1);
                    }
                });
                offset += 100;
            }
        });

        swipeRefresh.setOnRefreshListener(() -> updateWallVideoList());

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
                noConnection = true;
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
                wallAdapter = new WallAdapter(getActivity(), wallVideoList, WallFragment.this);
                recyclerView.setAdapter(wallAdapter);
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
                    String videoUri = vkApiVideo.player;
                    UrlHelper.playVideo(getActivity(), videoUri);
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

    public String getName() {
//        return getString(R.string.wall);
        return "СТЕНА";
    }
}