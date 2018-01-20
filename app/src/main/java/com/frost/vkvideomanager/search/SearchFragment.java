package com.frost.vkvideomanager.search;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


public class SearchFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, VKList<VKApiVideo>, SearchViewMosby, SearchPresenter>
        implements SearchViewMosby, SwipeRefreshLayout.OnRefreshListener, VideoAdapter.ItemClickListener,
        SearchView.OnQueryTextListener, SearchParametersDialogFragment.SearchParametersListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private VideoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;

    private String query = "";
    private int hd = 0;
    private int adult = 0;
    private int sort = 2;
    private String duration = "";

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public SearchPresenter createPresenter() {
        return new SearchPresenter();
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
        setHasOptionsMenu(true);
        loadingView.setVisibility(View.GONE);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.onActionViewExpanded();
        searchView.setQuery(query, false);
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            searchView.setMaxWidth(700);
        } else {
            searchView.setMaxWidth(1600);
        }
        searchView.setOnQueryTextListener(this);

        MenuItem parametersItem = menu.findItem(R.id.action_parameters);
        parametersItem.setOnMenuItemClickListener(item -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            SearchParametersDialogFragment fragment = SearchParametersDialogFragment
                    .newInstance(hd, adult, sort, duration);
            fragment.setListener(SearchFragment.this);
            fragment.show(ft, "ss");
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;

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
        if (!TextUtils.isEmpty(query)) {
            presenter.loadVideos(pullToRefresh, query, hd, adult, sort, duration);
        }
    }

    @Override
    public void moreVideosLoaded(int newSize) {
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, newSize - 1);
    }

    @Override
    public LceViewState<VKList<VKApiVideo>, SearchViewMosby> createViewState() {
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
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
                    presenter.loadMoreVideos(query, hd, adult, sort, duration);
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.addOnScrollListener(new EndlessScrollListener((GridLayoutManager) layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    presenter.loadMoreVideos(query, hd, adult, sort, duration);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals("")) {
            adapter = new VideoAdapter(getActivity(), this);
            recyclerView.setAdapter(adapter);
        } else {
            query = newText;
            loadData(false);
        }
        return true;
    }

    @Override
    public void onDialogPositiveClick(int hd, int adult, int sort, String duration) {
        if (this.hd != hd || this.adult != adult || this.sort != sort || !this.duration.equals(duration)) {
            this.hd = hd;
            this.adult = adult;
            this.sort = sort;
            this.duration = duration;
            presenter.loadVideos(true, query, this.hd, this.adult, this.sort, this.duration);
        }
    }
}
