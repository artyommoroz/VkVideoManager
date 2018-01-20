package com.frost.vkvideomanager.video.favorites;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.frost.vkvideomanager.video.VideoAdapter;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoritesFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, VKList<VKApiVideo>, FavoritesView, FavoritesPresenter>
        implements FavoritesView, SwipeRefreshLayout.OnRefreshListener, VideoAdapter.ItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private VideoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
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
    }

    @Override
    public FavoritesPresenter createPresenter() {
        return new FavoritesPresenter();
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
        presenter.loadVideos(pullToRefresh);
    }

    @Override
    public void moreVideosLoaded(int newSize) {
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, newSize - 1);
    }

    @Override
    public LceViewState<VKList<VKApiVideo>, FavoritesView> createViewState() {
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
        loadData(true);
        setScrollListener(layoutManager);
    }

    private void setScrollListener(RecyclerView.LayoutManager layoutManager) {
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    presenter.loadMoreVideos();
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.addOnScrollListener(new EndlessScrollListener((GridLayoutManager) layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    presenter.loadMoreVideos();
                }
            });
        }
    }

    @Override
    public void itemClicked(View v, int position) {
        if (v instanceof RelativeLayout) {
            String videoUri = presenter.getSelectedVideo(position).player;
            UrlHelper.playVideo(getActivity(), videoUri);
        } else if (v instanceof ImageButton){
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(getActivity(), presenter.getSelectedVideo(position));
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(getFragmentManager(), presenter.getSelectedVideo(position));
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }
}
