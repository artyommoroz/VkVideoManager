package com.example.frost.vkvideomanager.community;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.frost.vkvideomanager.BaseFragment;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.ViewPagerAdapter;
import com.example.frost.vkvideomanager.video.VideosFragment;
import com.example.frost.vkvideomanager.wall.WallFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommunityActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    public static final String COMMUNITY_NAME = "community name";
    public static final String COMMUNITY_ID = "community id";

    private List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String name = getIntent().getStringExtra(COMMUNITY_NAME);
        getSupportActionBar().setTitle(name);

        int communityId = getIntent().getIntExtra(COMMUNITY_ID, 13) * -1;
        Log.d("CommunityID", String.valueOf(communityId));
        fragments.add(VideosFragment.newInstance(communityId, 0, false));
        fragments.add(WallFragment.newInstance(communityId));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
