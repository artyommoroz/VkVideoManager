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

import com.frost.vkvideomanager.mosby.CatalogFragmentMosby;
import com.frost.vkvideomanager.mosby.CommunitiesFragmentMosby;
import com.frost.vkvideomanager.mosby.FavoritesFragmentMosby;
import com.frost.vkvideomanager.mosby.FeedFragmentMosby;
import com.frost.vkvideomanager.mosby.FriendsFragmentMosby;
import com.frost.vkvideomanager.mosby.SearchFragmentMosby;
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

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
//        , BillingProcessor.IBillingHandler {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
//    @Bind(R.id.adView)
//    AdView adView;

    private static final String TAG_CONTAINER = "container";
    private static final String TAG_FEED = "feed";
    private static final String TAG_CATALOG = "catalog";
    private static final String TAG_FRIENDS = "friends";
    private static final String TAG_COMMUNITIES = "communities";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_SEARCH = "search";
    private static final String TITLE = "title";
    private static final String GOOGLE_PLAY_URL = "market://details?id=com.frost.vkvideomanager";
    private static final String GOOGLE_API_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA21g4MlBZOOCGvUtrekmXfjpHpMb00CzXKCqbz6RX5P9xarjTtUXUrrHWa3oOEW/JN05AimYCynxCVSUTXtvHVfvJ/V4GR88SNj0v2sqHgmpo5NE9LsUbZhl01Bq9BnqBtv/7IlVaKBcTUrgvUKvSjzwpWNHkg4njWQUdCSyLmh2TnqOJIKY3MeVrgfRQ4O2mUoYARay941zaiNP0obJYq0yp/3GEZnL9Kl7B6FPCGr9DFq3vqBVOd89UICgaVkDE3/l452nK/9CjsjZbRnv9nXPpLzOmCKoaE+GjwdWZkBD0jb8puj4yckyaKULWh2PBY2zuyLQO2nggMk/CrMc1kwIDAQAB";
    private static final String GOOGLE_PRODUCT_ID = "remove_ads";
    private static final String GOOGLE_MERCHANT_ID = "3689-0974-6364";

    private VKApiUser vkApiUser;
    private Bundle state;
//    private BillingProcessor billingProcessor;
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

//        billingProcessor = new BillingProcessor(this, GOOGLE_API_KEY, GOOGLE_MERCHANT_ID, this);

//        FirebaseAnalytics.getInstance(this);
//        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7469946279231621~5606710797");
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

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
                fragment = FeedFragmentMosby.newInstance();
//                fragment = FeedFragment.newInstance();
                break;
            case R.id.nav_catalog:
                title = item.getTitle();
                tag = TAG_CATALOG;
//                fragment = CatalogFragment.newInstance();
                fragment = CatalogFragmentMosby.newInstance();
                break;
            case R.id.nav_friends:
                title = item.getTitle();
                tag = TAG_FRIENDS;
//                fragment = FriendsFragment.newInstance();
                fragment = FriendsFragmentMosby.newInstance();
                break;
            case R.id.nav_communities:
                title = item.getTitle();
                tag = TAG_COMMUNITIES;
//                fragment = CommunitiesFragment.newInstance();
                fragment = CommunitiesFragmentMosby.newInstance();
                break;
            case R.id.nav_favorites:
                title = item.getTitle();
                tag = TAG_FAVORITES;
//                fragment = FavoritesFragment.newInstance();
                fragment = FavoritesFragmentMosby.newInstance();
                break;
            case R.id.nav_search:
                title = item.getTitle();
                tag = TAG_SEARCH;
//                fragment = SearchFragment.newInstance();
                fragment = SearchFragmentMosby.newInstance();
                break;
//            case R.id.nav_remove_ads:
//                billingProcessor.purchase(this, GOOGLE_PRODUCT_ID);
//                removeAds();
//                break;
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

//    private void removeAds() {
//        adView.setVisibility(View.GONE);
//        navigationView.getMenu().findItem(R.id.nav_remove_ads).setVisible(false);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//       if (billingProcessor != null) {
//           billingProcessor.release();
//       }
//        super.onDestroy();
//    }

//    @Override
//    public void onProductPurchased(String productId, TransactionDetails details) {
//        Toast.makeText(this, "SUCCESS " + productId, Toast.LENGTH_SHORT).show();
//    }z
//
//    @Override
//    public void onPurchaseHistoryRestored() {
//
//    }
//
//    @Override
//    public void onBillingError(int errorCode, Throwable error) {
//        Toast.makeText(this, "CANCELED " + errorCode, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onBillingInitialized() {
//        Toast.makeText(this, "INITIALIZED", Toast.LENGTH_SHORT).show();
//    }
}
