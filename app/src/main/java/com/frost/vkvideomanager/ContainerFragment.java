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
    private FragmentManager retainedChildFragmentManager;
    private ViewPagerAdapter viewPagerAdapter;
    private Field hostField;
    private Class fragmentImplClass;
    private FragmentHostCallback currentHost;

    private List<Fragment> fragments = new ArrayList<>();

    {
        try {
            fragmentImplClass = Class.forName("android.support.v4.app.FragmentManagerImpl");
            hostField = fragmentImplClass.getDeclaredField("mHost");
            hostField.setAccessible(true);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("FragmentManagerImpl is renamed due to the " +
                    "change of Android SDK, this workaround doesn't work any more. " +
                    "See the issue at " +
                    "https://code.google.com/p/android/issues/detail?id=74222", e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("FragmentManagerImpl.mHost is found due to the " +
                    "change of Android SDK, this workaround doesn't work any more. " +
                    "See the issue at " +
                    "https://code.google.com/p/android/issues/detail?id=74222", e);
        }
    }

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

        fragments.add(VideosFragment.newInstance(0, 0, true));
        fragments.add(AlbumsFragment.newInstance());
        fragments.add(WallFragment.newInstance(0));
        fragments.add(FavoritesFragment.newInstance());
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
        viewPagerAdapter = new ViewPagerAdapter(fragments, retainedChildFragmentManager, getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (retainedChildFragmentManager != null) {
            //restore the last retained child fragment manager to the new
            //created fragment
            try {
                //Copy the mHost(Activity) to retainedChildFragmentManager
                currentHost = (FragmentHostCallback) hostField.get(getFragmentManager());
                Field childFMField = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFMField.setAccessible(true);
                childFMField.set(this, retainedChildFragmentManager);
                refreshHosts(getFragmentManager());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Refresh children fragment's hosts
        } else {
            //If the child fragment manager has not been retained yet, let it hold the internal
            //child fragment manager as early as possible. This can prevent child fragment
            //manager from missing to be set and then retained, which could happen when
            //OS kills activity and restarts it. In this case, the delegate fragment restored
            //but childFragmentManager() may not be called so mRetainedChildFragmentManager is
            //yet set. If the fragment is rotated, the state of child fragment manager will be
            //lost since mRetainedChildFragmentManager hasn't set to be retained by the OS.
            retainedChildFragmentManager = getChildFragmentManager();
        }
    }

    private void refreshHosts(FragmentManager fragmentManager) throws IllegalAccessException {
        if (fragmentManager != null) {
            replaceFragmentManagerHost(fragmentManager);
        }
        //replace host(activity) of fragments already added
        List<Fragment> frags = fragmentManager.getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null) {
                    try {
                        //Copy the mHost(Activity) to retainedChildFragmentManager
                        Field mHostField = Fragment.class.getDeclaredField("mHost");
                        mHostField.setAccessible(true);
                        mHostField.set(f, currentHost);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (f.getChildFragmentManager() != null) {
                        refreshHosts(f.getChildFragmentManager());
                    }
                }
            }
        }
    }

    private void replaceFragmentManagerHost(FragmentManager fragmentManager) throws IllegalAccessException {
        if (currentHost != null) {
            hostField.set(fragmentManager, currentHost);
        }
    }

}
