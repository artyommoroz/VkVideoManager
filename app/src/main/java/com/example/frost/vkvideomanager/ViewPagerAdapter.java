package com.example.frost.vkvideomanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.frost.vkvideomanager.friend.FriendsFragment;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentManager fragmentManager;
//    private List<BaseFragment> fragments = new ArrayList<>();

//    public ViewPagerAdapter(List<BaseFragment> fragments, FragmentManager manager) {
    public ViewPagerAdapter(List<Fragment> fragments, FragmentManager manager) {
        super(manager);
        fragmentManager = manager;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
//        switch (position) {
//            case 0:
//                if (fragmentManager.findFragmentByTag("VIDEOS") == null) {
//                    fragmentManager.beginTransaction().add(FriendsFragment.newInstance(), "VIDEOS").commit();
//                }
//                break;
//        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return fragments.get(position).getName();
        String title = "FR";
        switch (position) {
            case 0: title = "ДОБАВЛЕННЫЕ"; break;
            case 1: title = "АЛЬБОМЫ"; break;
            case 2: title = "СТЕНА"; break;
            case 3: title = "ЗАКЛАДКИ"; break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
