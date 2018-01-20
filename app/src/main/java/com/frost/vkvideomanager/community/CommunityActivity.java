package com.frost.vkvideomanager.community;

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

public class CommunityActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    public static final String COMMUNITY_NAME = "communityName";
    public static final String COMMUNITY_ID = "communityId";

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

        String name = getIntent().getStringExtra(COMMUNITY_NAME);
        getSupportActionBar().setTitle(name);

        int communityId = getIntent().getIntExtra(COMMUNITY_ID, 1);
        fragments.add(VideosFragment.newInstance(communityId, 0, false));
        fragments.add(WallVideosFragment.newInstance(communityId));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getSupportFragmentManager(), this);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
