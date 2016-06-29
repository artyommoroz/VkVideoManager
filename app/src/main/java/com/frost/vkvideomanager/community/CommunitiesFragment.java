package com.frost.vkvideomanager.community;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.BaseFragment;
import com.frost.vkvideomanager.network.NetworkChecker;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;

import butterknife.ButterKnife;

public class CommunitiesFragment extends BaseFragment implements CommunityAdapter.ItemClickListener {

    private CommunityAdapter communityAdapter;
    private VKList<VKApiCommunity> communityList = new VKList<>();

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (NetworkChecker.isOnline(getActivity())) {
            recyclerView.setAdapter(communityAdapter);
        }

        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = orientation == Configuration.ORIENTATION_PORTRAIT ?
                new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 2);
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
