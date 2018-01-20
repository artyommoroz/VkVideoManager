package com.frost.vkvideomanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.frost.vkvideomanager.friend.FriendsFragment;
import com.frost.vkvideomanager.video.favorites.FavoritesFragment;
import com.frost.vkvideomanager.feed.FeedFragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private static final String TAG_CONTAINER = "container";
    private static final String TAG_FEED = "feed";
    private static final String TAG_CATALOG = "catalog";
    private static final String TAG_FRIENDS = "friends";
    private static final String TAG_COMMUNITIES = "communities";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_SEARCH = "search";
    private static final String TITLE = "title";
    private static final String GOOGLE_PLAY_URL = "market://details?id=com.frost.vkvideomanager";

    private VKApiUser vkApiUser;
    private Bundle state;
    private CharSequence title;

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(TITLE, title);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getCharSequence(TITLE);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        tabLayout.setVisibility(View.GONE);
        String tag = null;
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_videos:
                title = item.getTitle();
                tag = TAG_CONTAINER;
                fragment = ContainerFragment.newInstance();
                break;
            case R.id.nav_news:
                title = item.getTitle();
                tag = TAG_FEED;
                fragment = FeedFragment.newInstance();
                break;
            case R.id.nav_catalog:
                title = item.getTitle();
                tag = TAG_CATALOG;
                fragment = CatalogFragment.newInstance();
                break;
            case R.id.nav_friends:
                title = item.getTitle();
                tag = TAG_FRIENDS;
                fragment = FriendsFragment.newInstance();
                break;
            case R.id.nav_communities:
                title = item.getTitle();
                tag = TAG_COMMUNITIES;
                fragment = CommunitiesFragment.newInstance();
                break;
            case R.id.nav_favorites:
                title = item.getTitle();
                tag = TAG_FAVORITES;
                fragment = FavoritesFragment.newInstance();
                break;
            case R.id.nav_search:
                title = item.getTitle();
                tag = TAG_SEARCH;
                fragment = SearchFragment.newInstance();
                break;
            case R.id.nav_rate_app:
                item.setCheckable(false);
                fragment = null;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL)));
                break;
            case R.id.nav_logout:
                item.setCheckable(false);
                fragment = null;
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
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources()
                        .getColor(R.color.colorPrimary));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources()
                        .getColor(R.color.colorPrimary));
                break;
        }

        getSupportActionBar().setTitle(title);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((fragmentManager.findFragmentByTag(tag) == null || state == null) && fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content, fragment, tag).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
