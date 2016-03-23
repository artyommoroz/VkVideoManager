package com.example.frost.vkvideomanager.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.BaseFragment;
import com.example.frost.vkvideomanager.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendsFragment extends BaseFragment implements FriendAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private FriendAdapter friendAdapter;
    private VKList<VKApiUser> friendList = new VKList<>();
    private static final String TAG = "FriendsFragment";
    

    public FriendsFragment() {}

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
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
                updateFriendList();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFriendList();
    }

    private void updateFriendList() {
        VKRequest videoRequest = VKApi.friends().get(VKParameters.from(
                VKApiConst.ORDER, "hints",
                VKApiConst.FIELDS, "photo_100"
        ));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefresh.setRefreshing(false);
                friendList.clear();
                int friendsCount = ((VKList<VKApiUser>) response.parsedModel).getCount();
                for (int i = 0; i < friendsCount; i++) {
                    VKApiUser friend = ((VKList<VKApiUser>) response.parsedModel).get(i);
                    friendList.add(friend);
                }
                friendAdapter = new FriendAdapter(getActivity(), friendList, FriendsFragment.this);
                recyclerView.setAdapter(friendAdapter);
            }
        });
    }

    @Override
    public void friendClicked(View v, int position) {
        Intent friendIntent = new Intent(getActivity(), FriendActivity.class);
        int friendId = friendList.get(position).id;
        String friendFullName = friendList.get(position).first_name + " "
                + friendList.get(position).last_name;
        friendIntent.putExtra("friendId", friendId);
        friendIntent.putExtra("friendFullName", friendFullName);
        startActivity(friendIntent);
    }

    public String getName() {
        return "FRIENDS";
    }

}
