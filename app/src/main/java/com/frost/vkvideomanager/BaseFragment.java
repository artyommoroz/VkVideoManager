package com.frost.vkvideomanager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.frost.vkvideomanager.network.NetworkChecker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    @BindView(R.id.rootView)
    protected RelativeLayout rootView;
    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @BindView(R.id.loadingView)
    protected ProgressBar progressBar;
    @BindView(R.id.contentView)
    protected SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.noConnectionView)
    protected RelativeLayout noConnectionView;
    @BindView(R.id.retryButton)
    protected Button retryButton;
    @BindView(R.id.noneItemsView)
    protected TextView noVideosView;

    public BaseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (NetworkChecker.isOnline(getActivity())) {
            noConnectionView.setVisibility(View.GONE);
        } else {
            noConnectionView.setVisibility(View.VISIBLE);
        }
    }

}
