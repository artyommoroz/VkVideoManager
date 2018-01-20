package com.frost.vkvideomanager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.frost.vkvideomanager.wall.WallFragment;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private Context context;

    public ViewPagerAdapter(List<Fragment> fragments, FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.fragments = fragments;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (fragments.get(position).getClass().getSimpleName()) {
            case "VideosFragment":
                return context.getString(R.string.fragment_videos);
//            case "WallFragment":
            case "WallVideosFragmentMosby":
                return context.getString(R.string.fragment_wall);
//            case "AlbumsFragment":
            case "AlbumsFragmentMosby":
                return context.getString(R.string.fragment_albums);
            default:
                return context.getString(R.string.fragment_videos);
        }
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
