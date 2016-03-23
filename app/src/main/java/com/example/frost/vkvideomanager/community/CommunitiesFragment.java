package com.example.frost.vkvideomanager.community;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommunitiesFragment extends Fragment implements CommunityAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private CommunityAdapter friendAdapter;
    private VKList<VKApiCommunity> communityList = new VKList<>();

    public CommunitiesFragment() {}

    public static CommunitiesFragment newInstance() {
        return new CommunitiesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateCommunityList();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateCommunityList();
    }

    private void updateCommunityList() {
        VKRequest videoRequest = VKApi.groups().get(VKParameters.from(VKApiConst.EXTENDED, 1));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                communityList.clear();
                int communitiesCount = ((VKList<VKApiCommunity>) response.parsedModel).getCount();
                for (int i = 0; i < communitiesCount; i++) {
                    VKApiCommunity community = ((VKList<VKApiCommunity>) response.parsedModel).get(i);
                    communityList.add(community);
                }
                friendAdapter = new CommunityAdapter(getActivity(), communityList, CommunitiesFragment.this);
                recyclerView.setAdapter(friendAdapter);
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
