package com.example.frost.vkvideomanager.catalog;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.frost.vkvideomanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CatalogSectionActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static final String SECTION_TITLE = "section title";
    public static final String SECTION_ID = "section id";
    public static final String SECTION_FROM = "section from";


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

        String sectionTitle = getIntent().getStringExtra(SECTION_TITLE);
        getSupportActionBar().setTitle(sectionTitle);

        String sectionId = getIntent().getStringExtra(SECTION_ID);
        String from = getIntent().getStringExtra(SECTION_FROM);
        CatalogSectionFragment catalogSectionFragment = CatalogSectionFragment.newInstance(sectionId, from);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, catalogSectionFragment).commit();
    }
}
