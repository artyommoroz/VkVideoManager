package com.example.frost.vkvideomanager;

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

import com.example.frost.vkvideomanager.catalog.CatalogFragment;
import com.example.frost.vkvideomanager.community.CommunitiesFragment;
import com.example.frost.vkvideomanager.feed.FeedFragment;
import com.example.frost.vkvideomanager.friend.FriendsFragment;
import com.example.frost.vkvideomanager.search.SearchFragment;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiUser;

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

    private VKApiUser vkApiUser;
    private List<BaseFragment> fragments = new ArrayList<>();
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Мои видеозаписи");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setCheckedItem(R.id.nav_videos);

        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(MainActivity.this, "friends,audio,video,wall,groups");
        }

        ContainerFragment containerFragment = ContainerFragment.newInstance(0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content, containerFragment).commit();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Explode explode = new Explode();
//            explode.setDuration(1000);
//            getWindow().setExitTransition(explode);
//        getWindow().setWindowAnimations(android.R.anim.slide_in_left);
//        overridePendingTransition();
//     }

//        fragments.add(VideosFragment.newInstance(0, 0));
//        fragments.add(AlbumsFragment.newInstance());
//        fragments.add(WallFragment.newInstance(0));
////        fragments.add(FriendsFragment.newInstance());
//
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragments, getFragmentManager());
//        viewPager.setOffscreenPageLimit(3);
//        viewPager.setAdapter(viewPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);

//        VKRequest userRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_100"));
//        userRequest.executeWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//
//                vkApiUser = ((VKList<VKApiUser>) response.parsedModel).get(0);
//                ImageView headerImage = (ImageView) navigationView.findViewById(R.id.imageView);
//                Picasso.with(MainActivity.this).load(vkApiUser.photo_100).fit().centerCrop()
//                        .transform(new CircleTransform()).into(headerImage);
//                TextView headerName = (TextView) navigationView.findViewById(R.id.nameView);
//                headerName.setText(vkApiUser.first_name + " " + vkApiUser.last_name);
//                TextView headerId = (TextView) navigationView.findViewById(R.id.idTextView);
//                headerId.setText(String.valueOf(vkApiUser.id));
//            }
//        });

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

        if (id == R.id.nav_videos) {
            ContainerFragment containerFragment = ContainerFragment.newInstance(0);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, containerFragment).commit();
            tabLayout.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Мои видеозаписи");
        } else if (id == R.id.nav_friends) {
            FriendsFragment friendsFragment = FriendsFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, friendsFragment).commit();
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Мои друзья");
        } else if (id == R.id.nav_catalog) {
            CatalogFragment catalogFragment = CatalogFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, catalogFragment).commit();
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Каталог");
        } else if (id == R.id.nav_news) {
            FeedFragment feedFragment = FeedFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, feedFragment).commit();
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Новости");
        } else if (id == R.id.nav_communities) {
            CommunitiesFragment communitiesFragment = CommunitiesFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, communitiesFragment).commit();
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Мои сообщества");
        } else if (id == R.id.nav_search) {
            SearchFragment searchFragment = SearchFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, searchFragment).commit();
            tabLayout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Новости");
        } else if (id == R.id.nav_send) {
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
