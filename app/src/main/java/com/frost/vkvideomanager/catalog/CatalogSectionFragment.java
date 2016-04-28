package com.frost.vkvideomanager.catalog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.network.Parser;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.frost.vkvideomanager.video.VideoAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CatalogSectionFragment extends Fragment implements VideoAdapter.ItemClickListener {

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

    private static final String CATALOG_SECTION_REQUEST = "video.getCatalogSection";
    private static final String SECTION_ID = "sectionId";
    private static final String FROM = "from";

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private String sectionId;
    private String from;
    private String fromFirst;
    private boolean isCreated;
    private boolean noConnection;

    public CatalogSectionFragment() {}

    public static CatalogSectionFragment newInstance(String sectionId, String from) {
        CatalogSectionFragment fragment = new CatalogSectionFragment();
        Bundle args = new Bundle();
        args.putString(SECTION_ID, sectionId);
        args.putString(FROM, from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionId = getArguments().getString(SECTION_ID);
            fromFirst = getArguments().getString(FROM);
        }
        updateVideoList();
        isCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(videoAdapter);
        }

        if (isCreated) {
            progressBar.setVisibility(View.VISIBLE);
            isCreated = false;
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (noConnection) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else {
            noConnectionView.setVisibility(View.GONE);
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
        VKRequest videoRequest = new VKRequest(CATALOG_SECTION_REQUEST, VKParameters.from(
                VKApiConst.CATALOG_SECTION_ID, sectionId,
                VKApiConst.CATALOG_FROM, from,
                VKApiConst.COUNT, 16));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    from = response.json.optJSONObject("response").getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                videoList.addAll(Parser.parseCatalogSection(response));
                int curSize = videoAdapter.getItemCount();
                videoAdapter.notifyItemRangeInserted(curSize, videoList.size() - 1);
            }
        });
    }

    private void updateVideoList () {
        VKRequest videoRequest = new VKRequest(CATALOG_SECTION_REQUEST, VKParameters.from(
                VKApiConst.CATALOG_SECTION_ID, sectionId,
                VKApiConst.CATALOG_FROM, fromFirst,
                VKApiConst.COUNT, 16));
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
                            .setAction(R.string.no_connection_snack_button, view -> {
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
                try {
                    from = response.json.optJSONObject("response").getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                videoList = Parser.parseCatalogSection(response);
                videoAdapter = new VideoAdapter(getActivity(), videoList, CatalogSectionFragment.this);
                recyclerView.setAdapter(videoAdapter);
            }
        });
    }

    @Override
    public void itemClicked(View v, final int position) {
        if (v instanceof RelativeLayout) {
            VKRequest videoRequest = VKApi.video().get(VKParameters.from(VKApiConst.VIDEOS,
                    videoList.get(position).owner_id + "_" + videoList.get(position).id));
            videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                    String videoUri = vkApiVideo.player;
                    UrlHelper.playVideo(getActivity(), videoUri);
                }
            });
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
