package com.frost.vkvideomanager.friend;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.ViewPagerAdapter;
import com.frost.vkvideomanager.video.VideosFragment;
import com.frost.vkvideomanager.wall.WallVideosFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    public static final String FRIEND_ID = "friendId";
    public static final String FRIEND_FULL_NAME = "friendFullName";

    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String friendFullName = getIntent().getStringExtra(FRIEND_FULL_NAME);
        getSupportActionBar().setTitle(friendFullName);

        int friendId = getIntent().getIntExtra(FRIEND_ID, 13);

        fragments.add(VideosFragment.newInstance(friendId, 0, false));
        fragments.add(WallVideosFragment.newInstance(friendId));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getSupportFragmentManager(), this);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
