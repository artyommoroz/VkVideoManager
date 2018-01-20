package com.frost.vkvideomanager.wall;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.network.AdditionRequests;
import com.frost.vkvideomanager.player.UrlHelper;
import com.frost.vkvideomanager.utils.EndlessScrollListener;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WallVideosFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, List<WallVideo>, WallVideosView, WallVideosPresenter>
        implements WallVideosView, SwipeRefreshLayout.OnRefreshListener, WallVideoAdapter.ItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private static final String OWNER_ID = "ownerId";

    private WallVideoAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int ownerId;

    public static WallVideosFragment newInstance(int ownerId) {
        WallVideosFragment fragment = new WallVideosFragment();
        Bundle args = new Bundle();
        args.putInt(OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            ownerId = getArguments().getInt(OWNER_ID);
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
        adapter = new WallVideoAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getActivity());
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
    public WallVideosPresenter createPresenter() {
        return new WallVideosPresenter();
    }

    @Override
    public void setData(List<WallVideo> data) {
        adapter.setWallVideos(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<WallVideo> getData() {
        return adapter == null ? null : adapter.getWallVideos();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadWallVideos(pullToRefresh, ownerId);
    }

    @Override
    public void moreWallVideosLoaded(int newSize) {
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, newSize - 1);
    }


    @Override
    public LceViewState<List<WallVideo>, WallVideosView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        if (adapter.getWallVideos().isEmpty()) {
            noneItemsView.setVisibility(View.VISIBLE);
            noneItemsView.setText(getString(R.string.no_wall_videos));
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

    private void setScrollListener(LinearLayoutManager layoutManager) {
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                presenter.loadMoreWallVideos(ownerId);
            }
        });
    }

    @Override
    public void itemClicked(View v, int position) {
        if (v instanceof CardView) {
            presenter.getWallVideoUrl(position);
        } else if (v instanceof ImageButton) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_video);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add:
                        AdditionRequests.addVideo(getActivity(), presenter.getSelectedVideo(position).getVideo());
                        return true;
                    case R.id.add_to_album:
                        AdditionRequests.addVideoToAlbum(getFragmentManager(), presenter.getSelectedVideo(position).getVideo());
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

    @Override
    public void playWallVideo(String wallVideoUrl) {
        UrlHelper.playVideo(getActivity(), wallVideoUrl);
    }
}
