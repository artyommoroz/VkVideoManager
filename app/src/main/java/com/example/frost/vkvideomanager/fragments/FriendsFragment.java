package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.FriendsAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendsFragment extends BaseFragment implements FriendsAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private FriendsAdapter friendsAdapter;
    private VKList<VKApiUser> friendList = new VKList<>();
    private OnFriendSelectedListener friendSelectedListener;

    public FriendsFragment() {}

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VKRequest videoRequest = VKApi.friends().get(VKParameters.from(
                "order", "hints",
                VKApiConst.FIELDS, "photo_100"));

        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                int friendsCount = ((VKList<VKApiUser>) response.parsedModel).getCount();
                for (int i = 0; i < friendsCount; i++) {
                    VKApiUser friend = ((VKList<VKApiUser>) response.parsedModel).get(i);
                    friendList.add(friend);
                }
                friendsAdapter = new FriendsAdapter(getActivity(), friendList, FriendsFragment.this);
                recyclerView.setAdapter(friendsAdapter);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity instanceof OnFriendSelectedListener) {
            friendSelectedListener = (OnFriendSelectedListener) activity;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFriendSelectedListener) {
            friendSelectedListener = (OnFriendSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        friendSelectedListener = null;
    }

    @Override
    public void itemClicked(View v, int position) {
        if (friendSelectedListener != null) {
            int friendId = friendList.get(position).id;
            String friendFullName = friendList.get(position).first_name + " " + friendList.get(position).last_name;
            friendSelectedListener.onFriendSelected(friendId, friendFullName);
        }
    }

    public interface OnFriendSelectedListener {
        void onFriendSelected(int friendId, String friendFullName);
    }

    public String getName() {
        return "FRIENDS";
    }
}
