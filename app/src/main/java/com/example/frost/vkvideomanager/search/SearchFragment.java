package com.example.frost.vkvideomanager.search;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.frost.vkvideomanager.EndlessScrollListener;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.network.AdditionRequests;
import com.example.frost.vkvideomanager.network.Parser;
import com.example.frost.vkvideomanager.video.VideoAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment implements VideoAdapter.ItemClickListener,
        SearchView.OnQueryTextListener, SearchParametersDialogFragment.SearchParametersListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private int offset;
    private String query;
    private int hd = 0;
    private int adult = 0;
    private int sort = 2;
    private String duration = "";

    public SearchFragment() {}

    public static SearchFragment newInstance() {;
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        progressBar.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                offset += offset;
                VKRequest searchRequest = VKApi.video().search(VKParameters.from(
                        VKApiConst.Q, query,
                        VKApiConst.HD, hd,
                        VKApiConst.ADULT, adult,
                        VKApiConst.SORT, sort,
                        VKApiConst.FILTERS, duration,
                        VKApiConst.COUNT, 50,
                        VKApiConst.OFFSET, offset
                ));
                searchRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        videoList.addAll(Parser.parseVideos(response));
                        int curSize = videoAdapter.getItemCount();
                        videoAdapter.notifyItemRangeInserted(curSize, videoList.size() - 1);
                    }
                });
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSearchResults(query, hd, adult, sort, duration);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.onActionViewExpanded();
        searchView.setMaxWidth(800);
        searchView.setOnQueryTextListener(this);

        MenuItem parametersItem = menu.findItem(R.id.action_parameters);
        parametersItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                SearchParametersDialogFragment fragment = SearchParametersDialogFragment
                        .newInstance(hd, adult, sort, duration);
                fragment.setListener(SearchFragment.this);
                fragment.show(ft, "ss");
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void itemClicked(View v, final int position) {
        if (v instanceof RelativeLayout) {
            Uri videoUri = Uri.parse(videoList.get(position).player);
            startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
        } else if (v instanceof ImageButton){
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_video);
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
                        default:
                            return false;
                    }
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
        query = newText;
        getSearchResults(query, hd, adult, sort, duration);
        return false;
    }

    @Override
    public void onDialogPositiveClick(int hd, int adult, int sort, String duration) {
        if (this.hd != hd || this.adult != adult || this.sort != sort || this.duration != duration) {
            this.hd = hd;
            this.adult = adult;
            this.sort = sort;
            this.duration = duration;
            getSearchResults(query, this.hd, this.adult, this.sort, this.duration);
        }
    }

    private void getSearchResults(String query, int hd, int adult, int sort, String duration) {
        VKRequest searchRequest = VKApi.video().search(VKParameters.from(
                VKApiConst.Q, query,
                VKApiConst.HD, hd,
                VKApiConst.ADULT, adult,
                VKApiConst.SORT, sort,
                VKApiConst.FILTERS, duration,
                VKApiConst.COUNT, 50
        ));
        searchRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefresh.setRefreshing(false);
                videoList.clear();
                videoList = Parser.parseVideos(response);
                offset = videoList.size();
                videoAdapter = new VideoAdapter(getActivity(), videoList, SearchFragment.this);
                recyclerView.setAdapter(videoAdapter);
            }
        });
    }
}