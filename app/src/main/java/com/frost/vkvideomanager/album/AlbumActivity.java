package com.frost.vkvideomanager.album;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.video.VideosFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static final String ALBUM_ID = "albumId";
    public static final String ALBUM_TITLE = "albumTitle";
    public static final String OWNER_ID = "ownerId";
    public static final String IS_MY = "isMy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String albumTitle = getIntent().getStringExtra(ALBUM_TITLE);
        getSupportActionBar().setTitle(albumTitle);

        int albumId = getIntent().getIntExtra(ALBUM_ID, 13);
        int ownerId = getIntent().getIntExtra(OWNER_ID, 13);
        boolean isMy = getIntent().getBooleanExtra(IS_MY, false);

        VideosFragment videoFragment;
        if (isMy) {
            videoFragment = VideosFragment.newInstance(ownerId, albumId, true);
        } else {
            videoFragment = VideosFragment.newInstance(ownerId, albumId, false);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.container, videoFragment).commit();
        }
    }
}
