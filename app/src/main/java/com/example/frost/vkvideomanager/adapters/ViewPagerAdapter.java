package com.example.frost.vkvideomanager.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.example.frost.vkvideomanager.fragments.BaseFragment;

import java.util.List;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;

    public ViewPagerAdapter(List<BaseFragment> fragments, FragmentManager manager) {
        super(manager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getName();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
