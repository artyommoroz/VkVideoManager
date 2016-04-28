package com.frost.vkvideomanager.catalog;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.frost.vkvideomanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CatalogSectionActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static final String SECTION_TITLE = "sectionTitle";
    public static final String SECTION_ID = "sectionId";
    public static final String SECTION_FROM = "sectionFrom";

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

        String sectionTitle = getIntent().getStringExtra(SECTION_TITLE);
        getSupportActionBar().setTitle(sectionTitle);

        String sectionId = getIntent().getStringExtra(SECTION_ID);
        String from = getIntent().getStringExtra(SECTION_FROM);
        CatalogSectionFragment catalogSectionFragment = CatalogSectionFragment.newInstance(sectionId, from);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.container, catalogSectionFragment).commit();
        }
    }
}
