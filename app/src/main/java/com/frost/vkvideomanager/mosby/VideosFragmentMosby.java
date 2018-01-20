package com.frost.vkvideomanager.mosby;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.mosby.presenter.VideosPresenter;
import com.frost.vkvideomanager.mosby.view.VideosView;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.frost.vkvideomanager.video.VideoAdapter;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by frost on 15.10.16.
 */

public class VideosFragmentMosby extends MvpLceViewStateFragment<SwipeRefreshLayout, VKList<VKApiVideo>, VideosView, VideosPresenter>
        implements VideosView, SwipeRefreshLayout.OnRefreshListener, VideoAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.noneItemsView)
    TextView noneItemsView;

    private static final String ALBUM_ID = "albumId";
    private static final String OWNER_ID = "ownerId";
    private static final String IS_MY = "isMy";

    private VideoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int ownerId;
    private int albumId;
    private boolean isMy;

    public static VideosFragmentMosby newInstance(int ownerId, int albumId, boolean isMy) {
        VideosFragmentMosby fragment = new VideosFragmentMosby();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        errorView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.ic_cloud_off_black_96dp), null, null);
        adapter = new VideoAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

        int orientation = getActivity().getResources().getConfiguration().orientation;
        layoutManager = orientation == Configuration.ORIENTATION_PORTRAIT ?
                new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        setScrollListener(layoutManager);

        contentView.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        ButterKnife.unbind(this);
    }

    @Override
    public VideosPresenter createPresenter() {
        return new VideosPresenter();
    }

    @Override
    public void setData(VKList<VKApiVideo> data) {
        adapter.setVideos(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public VKList<VKApiVideo> getData() {
        return adapter == null ? null : adapter.getVideos();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadVideos(pullToRefresh, ownerId, albumId);
    }

    @Override
    public void moreVideosLoaded(int newSize) {
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, newSize - 1);
    }

    @Override
    public LceViewState<VKList<VKApiVideo>, VideosView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        if (adapter.getVideos().isEmpty()) {
            noneItemsView.setVisibility(View.VISIBLE);
            noneItemsView.setText(getString(R.string.no_fav_videos));
        }
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        contentView.setRefreshing(pullToRefresh);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.no_connection_message);
    }

    @Override
    public void onRefresh() {
        setScrollListener(layoutManager);
        loadData(true);
    }

    private void setScrollListener(RecyclerView.LayoutManager layoutManager) {
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    presenter.loadMoreVideos(ownerId, albumId);
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.addOnScrollListener(new EndlessScrollListener((GridLayoutManager) layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    presenter.loadMoreVideos(ownerId, albumId);
                }
            });
        }
    }

    @Override
    public void itemClicked(View v, int position) {
        if (v instanceof RelativeLayout) {
            String videoUrl = presenter.getSelectedVideo(position).player;
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
                        AdditionRequests.addVideo(getActivity(), presenter.getSelectedVideo(position));
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(getFragmentManager(), presenter.getSelectedVideo(position));
                        return true;
                    case R.id.delete:
                        showDeleteDialog(position);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.video_dialog_delete_title)
                .setPositiveButton(R.string.video_dialog_delete_positive_button,
                        (dialog, id) -> presenter.deleteVideo(position, albumId))
                .setNegativeButton(R.string.video_dialog_delete_negative_button,
                        (dialog, id) -> { dialog.cancel(); });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void videoDeleted(int position, String title) {
        Toast.makeText(getActivity(), getString(R.string.video_dialog_delete_success_toast, title),
                Toast.LENGTH_SHORT).show();
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
    }
}
