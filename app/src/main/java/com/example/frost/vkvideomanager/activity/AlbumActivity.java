package com.example.frost.vkvideomanager.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.fragments.VideoListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumActivity extends AppCompatActivity implements VideoListFragment.OnVideoSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String albumTitle = getIntent().getStringExtra("albumTitle");
        toolbar.setTitle(albumTitle);

        int albumId = getIntent().getIntExtra("albumId", 13);
        Fragment videoFragment = VideoListFragment.newInstance(albumId);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, videoFragment).commit();
    }

    @Override
    public void onVideoSelected(Uri videoUri) {
        startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
    }
}
