package com.example.frost.vkvideomanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frost.vkvideomanager.catalog.CatalogFragment;
import com.example.frost.vkvideomanager.community.CommunitiesFragment;
import com.example.frost.vkvideomanager.feed.FeedFragment;
import com.example.frost.vkvideomanager.friend.FriendsFragment;
import com.example.frost.vkvideomanager.search.SearchFragment;
import com.example.frost.vkvideomanager.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

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
    private List<BaseFragment> fragments = new ArrayList<>();
    Bundle state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        state = savedInstanceState;

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        if (!VKSdk.isLoggedIn()) {
//            VKSdk.login(MainActivity.this, "friends,audio,video,wall,groups");
//        }

        if (savedInstanceState == null) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_videos, 0);
        }

        VKRequest userRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_100"));
        userRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                vkApiUser = ((VKList<VKApiUser>) response.parsedModel).get(0);
                ImageView headerImage = (ImageView) navigationView.findViewById(R.id.icon);
                Picasso.with(MainActivity.this).load(vkApiUser.photo_100).fit().centerCrop()
                        .transform(new CircleTransform()).into(headerImage);
                TextView headerName = (TextView) navigationView.findViewById(R.id.name);
                headerName.setText(vkApiUser.first_name + " " + vkApiUser.last_name);
            }
        });

//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        Log.d("Main FINGERPRINT", fingerprints[0]);
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
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_videos) {
            FragmentManager fragmentManager = getSupportFragmentManager();
//            if (fragmentManager.findFragmentByTag(TAG_CONTAINER) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        ContainerFragment.newInstance(), TAG_CONTAINER).commit();
//            }
            tabLayout.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Мои видеозаписи");
        } else if (id == R.id.nav_friends) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_FRIENDS) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        FriendsFragment.newInstance(), TAG_FRIENDS).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Мои друзья");
        } else if (id == R.id.nav_catalog) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_CATALOG) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        CatalogFragment.newInstance(), TAG_CATALOG).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Каталог");
        } else if (id == R.id.nav_news) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_FEED) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        FeedFragment.newInstance(), TAG_FEED).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Новости");
        } else if (id == R.id.nav_communities) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_COMMUNITIES) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        CommunitiesFragment.newInstance(), TAG_COMMUNITIES).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Мои сообщества");
        } else if (id == R.id.nav_search) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(TAG_SEARCH) == null || state == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        SearchFragment.newInstance(), TAG_SEARCH).commit();
            }
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Поиск видеозаписей");
        } else if (id == R.id.nav_logout) {
            VKSdk.logout();
            finish();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
