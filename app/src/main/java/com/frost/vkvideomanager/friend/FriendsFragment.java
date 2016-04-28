package com.frost.vkvideomanager.friend;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.frost.vkvideomanager.BaseFragment;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendsFragment extends BaseFragment implements FriendAdapter.ItemClickListener {

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

    private FriendAdapter friendAdapter;
    private VKList<VKApiUser> friendList = new VKList<>();
    private boolean noConnection;

    public FriendsFragment() {}

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFriendList();
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
            friendAdapter = new FriendAdapter(getActivity(), friendList, FriendsFragment.this);
            recyclerView.setAdapter(friendAdapter);
        }

        if (noConnection  && friendList.isEmpty()) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else if (!noConnection  && friendList.size() > 0) {
            noConnectionView.setVisibility(View.GONE);
        }

        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = null;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(getActivity());
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getActivity(), 3);
        }
        recyclerView.setLayoutManager(layoutManager);

        swipeRefresh.setOnRefreshListener(() -> updateFriendList());

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateFriendList();
        });
    }

    private void updateFriendList() {
        VKRequest videoRequest = VKApi.friends().get(VKParameters.from(
                VKApiConst.COUNT, 1000,
                VKApiConst.ORDER, "hints",
                VKApiConst.FIELDS, "photo_100"
        ));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                noConnection = true;
                if (friendList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (friendList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.no_connection_snack_button), view -> {
                                updateFriendList();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
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
        String friendFullName = String.format("%s %s", friendList.get(position).first_name,
                friendList.get(position).last_name);
        friendIntent.putExtra(FriendActivity.FRIEND_ID, friendId);
        friendIntent.putExtra(FriendActivity.FRIEND_FULL_NAME, friendFullName);
        startActivity(friendIntent);
    }

    public String getName() {
        return "FRIENDS";
    }

}
