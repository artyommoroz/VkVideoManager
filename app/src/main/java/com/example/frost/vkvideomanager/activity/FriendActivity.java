package com.example.frost.vkvideomanager.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.ViewPagerAdapter;
import com.example.frost.vkvideomanager.fragments.BaseFragment;
import com.example.frost.vkvideomanager.fragments.VideosFragment;
import com.example.frost.vkvideomanager.fragments.WallFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendActivity extends AppCompatActivity implements VideosFragment.OnVideoSelectedListener {

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
        setContentView(R.layout.activity_friends);
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
        Log.d("friendFullNameACTIVITY", friendFullName);

        int friendId = getIntent().getIntExtra("friendId", 13);
        fragments.add(VideosFragment.newInstance(friendId, 0, false));
        fragments.add(WallFragment.newInstance(friendId));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onVideoSelected(Uri videoUri) {
        startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
