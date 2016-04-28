package com.frost.vkvideomanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.frost.vkvideomanager.catalog.CatalogFragment;
import com.frost.vkvideomanager.community.CommunitiesFragment;
import com.frost.vkvideomanager.feed.FeedFragment;
import com.frost.vkvideomanager.friend.FriendsFragment;
import com.frost.vkvideomanager.search.SearchFragment;
import com.frost.vkvideomanager.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    private static final String TAG_CONTAINER = "container";
    private static final String TAG_FEED = "feed";
    private static final String TAG_CATALOG = "catalog";
    private static final String TAG_FRIENDS = "friends";
    private static final String TAG_COMMUNITIES = "communities";
    private static final String TAG_SEARCH = "search";

    private VKApiUser vkApiUser;
    private Bundle state;
    private String currentTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        View header = navigationView.getHeaderView(0);
        final ImageView headerImage = (ImageView) header.findViewById(R.id.icon);
        final TextView headerName = (TextView) header.findViewById(R.id.name);

        state = savedInstanceState;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_videos, 0);
        }

        VKRequest userRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_100"));
        userRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                vkApiUser = ((VKList<VKApiUser>) response.parsedModel).get(0);
                headerName.setText(String.format("%s %s", vkApiUser.first_name, vkApiUser.last_name));
                Picasso.with(MainActivity.this).load(vkApiUser.photo_100).fit().centerCrop()
                        .transform(new CircleTransform()).into(headerImage);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_videos) {
            FragmentManager fragmentManager = getSupportFragmentManager();
//            if (fragmentManager.findFragmentByTag(TAG_CONTAINER) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        ContainerFragment.newInstance(), TAG_CONTAINER).commit();
//            }
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_friends) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_FRIENDS) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        FriendsFragment.newInstance(), TAG_FRIENDS).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_catalog) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_CATALOG) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        CatalogFragment.newInstance(), TAG_CATALOG).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_news) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_FEED) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        FeedFragment.newInstance(), TAG_FEED).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_communities) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_COMMUNITIES) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        CommunitiesFragment.newInstance(), TAG_COMMUNITIES).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_search) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_SEARCH) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        SearchFragment.newInstance(), TAG_SEARCH).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.logout_message)
                    .setPositiveButton(R.string.video_dialog_delete_positive_button, (dialog, id1) -> {
                        VKSdk.logout();
                        finish();
                        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(logoutIntent);
                    })
                    .setNegativeButton(R.string.video_dialog_delete_negative_button, (dialog, id1) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
