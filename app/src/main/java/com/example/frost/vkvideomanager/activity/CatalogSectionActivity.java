package com.example.frost.vkvideomanager.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.fragments.CatalogSectionFragment;
import com.example.frost.vkvideomanager.fragments.VideosFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CatalogSectionActivity extends AppCompatActivity implements VideosFragment.OnVideoSelectedListener {

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

        String sectionTitle = getIntent().getStringExtra("sectionTitle");
        getSupportActionBar().setTitle(sectionTitle);

        String sectionId = getIntent().getStringExtra("sectionId");
        Log.d("SectionActivityID", sectionId);
        String from = getIntent().getStringExtra("from");
        Log.d("SectionActivityFROM", from);
        CatalogSectionFragment catalogSectionFragment = CatalogSectionFragment.newInstance(sectionId, from);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, catalogSectionFragment).commit();
    }

    @Override
    public void onVideoSelected(Uri videoUri) {
        startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
    }
}
