package com.example.frost.vkvideomanager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frost.vkvideomanager.album.AlbumsFragment;
import com.example.frost.vkvideomanager.video.FavoritesFragment;
import com.example.frost.vkvideomanager.video.VideosFragment;
import com.example.frost.vkvideomanager.wall.WallFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ContainerFragment extends Fragment {

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    int ownerId;

    private List<BaseFragment> fragments = new ArrayList<>();

    public ContainerFragment() {}

    public static ContainerFragment newInstance(int ownerId) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        args.putInt("ownerId", ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            ownerId = getArguments().getInt("ownerId");
        }

        fragments.add(VideosFragment.newInstance(ownerId, 0, true));
        fragments.add(AlbumsFragment.newInstance());
        fragments.add(WallFragment.newInstance(ownerId));
        fragments.add(FavoritesFragment.newInstance());

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

}
