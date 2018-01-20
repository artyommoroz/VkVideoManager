package com.frost.vkvideomanager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHostCallback;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frost.vkvideomanager.album.AlbumsFragment;
import com.frost.vkvideomanager.mosby.AlbumsFragmentMosby;
import com.frost.vkvideomanager.mosby.VideosFragmentMosby;
import com.frost.vkvideomanager.mosby.WallVideosFragmentMosby;
import com.frost.vkvideomanager.video.FavoritesFragment;
import com.frost.vkvideomanager.video.VideosFragment;
import com.frost.vkvideomanager.wall.WallFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ContainerFragment extends Fragment {

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragments = new ArrayList<>();

    public ContainerFragment() {}

    public static ContainerFragment newInstance() {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        fragments.add(VideosFragmentMosby.newInstance(0, 0, true));
//        fragments.add(VideosFragment.newInstance(0, 0, true));
        fragments.add(AlbumsFragmentMosby.newInstance());
//        fragments.add(AlbumsFragment.newInstance());
        fragments.add(WallVideosFragmentMosby.newInstance(0));
//        fragments.add(WallFragment.newInstance(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        viewPagerAdapter = new ViewPagerAdapter(fragments, getChildFragmentManager(), getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
