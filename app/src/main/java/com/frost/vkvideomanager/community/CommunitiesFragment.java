package com.frost.vkvideomanager.community;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.frost.vkvideomanager.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommunitiesFragment extends Fragment implements CommunityAdapter.ItemClickListener {

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

    private CommunityAdapter communityAdapter;
    private VKList<VKApiCommunity> communityList = new VKList<>();
    private boolean noConnection;

    public CommunitiesFragment() {}

    public static CommunitiesFragment newInstance() {
        return new CommunitiesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateCommunityList();
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
            communityAdapter = new CommunityAdapter(getActivity(), communityList, CommunitiesFragment.this);
            recyclerView.setAdapter(communityAdapter);
        }

        if (noConnection  && communityList.isEmpty()) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else if (!noConnection  && communityList.size() > 0) {
            noConnectionView.setVisibility(View.GONE);
        }

        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = null;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(getActivity());
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        recyclerView.setLayoutManager(layoutManager);

        swipeRefresh.setOnRefreshListener(() -> updateCommunityList());

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateCommunityList();
        });
    }

    private void updateCommunityList() {
        VKRequest videoRequest = VKApi.groups().get(VKParameters.from(
                VKApiConst.COUNT, 1000,
                VKApiConst.EXTENDED, 1));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                noConnection = true;
                if (communityList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (communityList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(R.string.no_connection_snack_button, view -> {
                                updateCommunityList();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                communityList.clear();
                int communitiesCount = ((VKList<VKApiCommunity>) response.parsedModel).getCount();
                for (int i = 0; i < communitiesCount; i++) {
                    VKApiCommunity community = ((VKList<VKApiCommunity>) response.parsedModel).get(i);
                    communityList.add(community);
                }
                communityAdapter = new CommunityAdapter(getActivity(), communityList, CommunitiesFragment.this);
                recyclerView.setAdapter(communityAdapter);
            }
        });
    }

    @Override
    public void itemClicked(View v, int position) {
        Intent communityIntent = new Intent(getActivity(), CommunityActivity.class);
        communityIntent.putExtra(CommunityActivity.COMMUNITY_ID, communityList.get(position).id * -1);
        communityIntent.putExtra(CommunityActivity.COMMUNITY_NAME, communityList.get(position).name);
        startActivity(communityIntent);
    }

}
