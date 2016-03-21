package com.example.frost.vkvideomanager.wall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.BaseFragment;
import com.example.frost.vkvideomanager.EndlessScrollListener;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WallFragment extends BaseFragment implements WallAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private List<WallVideo> wallVideoList = new ArrayList<>();
    private int offset = 100;
    private WallAdapter wallAdapter;
    private int ownerId;

    public WallFragment() {}

    public static WallFragment newInstance(int ownerId) {
        WallFragment fragment = new WallFragment();
        Bundle args = new Bundle();
        args.putInt("ownerId", ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                VKRequest wallRequest = VKApi.wall().get(VKParameters.from(
                        VKApiConst.OWNER_ID, ownerId,
                        VKApiConst.OFFSET, offset,
                        VKApiConst.COUNT, 50,
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
                offset += 50;
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWallVideoList();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ownerId = getArguments().getInt("ownerId");
        }
        updateWallVideoList();
    }

    private void updateWallVideoList() {
        VKRequest wallRequest = VKApi.wall().get(VKParameters.from(
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.COUNT, 100,
                VKApiConst.EXTENDED, 1));
        wallRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                wallVideoList.clear();
                wallVideoList = Parser.parseWall(response);
                Log.d("CommunityIDwaLL", String.valueOf(wallVideoList.size()));
                wallAdapter = new WallAdapter(getActivity(), wallVideoList, WallFragment.this);
                recyclerView.setAdapter(wallAdapter);
            }
        });
    }

    @Override
    public void itemClicked(View v, final int position) {
        if (v instanceof CardView) {
            VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                    "videos", wallVideoList.get(position).getVideo().owner_id
                            + "_" + wallVideoList.get(position).getVideo().id
                            + "_" + wallVideoList.get(position).getVideo().access_key));
            videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                    Uri videoUri = Uri.parse(vkApiVideo.player);
                    startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
                }
            });
        } else if (v instanceof ImageButton) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
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
                }
            });
            popupMenu.show();
        }
    }

    public String getName() {
        return "CТЕНА";
    }
}