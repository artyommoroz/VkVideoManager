package com.example.frost.vkvideomanager.friend;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class FriendActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
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

        String friendFullName = getIntent().getStringExtra("friendFullName");
        getSupportActionBar().setTitle(friendFullName);

        int friendId = getIntent().getIntExtra("friendId", 13);
        fragments.add(VideosFragment.newInstance(friendId, 0, false));
        fragments.add(WallFragment.newInstance(friendId));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
