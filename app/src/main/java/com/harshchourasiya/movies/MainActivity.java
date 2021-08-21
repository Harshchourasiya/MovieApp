package com.harshchourasiya.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.harshchourasiya.movies.Category.Category;
import com.harshchourasiya.movies.Home.Home;
import com.harshchourasiya.movies.Like.Like;
import com.harshchourasiya.movies.Login.Login;
import com.harshchourasiya.movies.Search.Search;
import com.harshchourasiya.movies.Setting.Setting;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static com.harshchourasiya.movies.Data.Data.ISFIRSTTIMEKEY;
import static com.harshchourasiya.movies.Data.Data.ISPAID;
import static com.harshchourasiya.movies.Data.Data.REMOVE_AD_ID;
import static com.harshchourasiya.movies.Data.Data.SHARE_BODY;
import static com.harshchourasiya.movies.Data.Data.SHARE_TITLE;
import static com.harshchourasiya.movies.Data.Data.TOPIC;
import static com.harshchourasiya.movies.Data.Data.getEmail;
import static com.harshchourasiya.movies.Data.Data.isFirstTime;
import static com.harshchourasiya.movies.Data.Data.isPaid;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    // Initializing Bottom Navigation
    private BottomNavigationView bottomNavigation;

    // Initializing Variables for Billing
    boolean isReady = false;
    private SkuDetails details;
    private BillingClient billingClient;
    private List skuList = new ArrayList();

    // Initializing Firebase Analytics for google Analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Make a Firebase Analytics Instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // This method Subscribe topic to firebase messaging service which help to check messages
        toSubscribeToTopic();
        // This method initializing Billing system
        toRemoveAds();
        // This method load Ads
        toLoadAd();
        // Open Home fragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, new Home()).commit();
        // Initializing Bottom Navigation
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        // This method check internet Connection
        toCheckInternet(this);
        // this method Check weather User Login or not
        toCheckForLogin();
        // This method Handle Bottom Navigation Click listener
        forHandlingBottomNavigation();
        // This method Handle drawer Navigation Click listener
        forHandlingDrawerNavigation();
        // This method handle Search button Click listener
        forHandlingSearchButton();
    }


    // for Search button

    private void forHandlingSearchButton() {
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Search Activity
                Intent intent = new Intent(getApplicationContext(), Search.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }


    /*

    Drawer Navigation Code start from here

     */

    // For Drawer navigation

    public void forHandlingDrawerNavigation() {
        // Get Toolbar from layout and attach with action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawerNavigation);
        View navigationHeaderView = (View) navigationView.inflateHeaderView(R.layout.navigation_header);
        // Set up Navigation Header
        toSetupHeaderNavigation(navigationHeaderView);
        // Action bar Drawer toggle with Drawer navigation
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        // Start Search Intent
                        toStartActivity(new Intent(getApplicationContext(), Search.class));
                        break;
                    case R.id.setting:
                        // Start Setting Intent
                        toStartActivity(new Intent(getApplicationContext(), Setting.class));
                        break;
                    case R.id.rate:
                        // Start Rate Url
                        toStartActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                        break;
                    case R.id.share:
                        // Share app url in whatsapp, facebook and message
                        toShare();
                        break;
                    case R.id.remove:
                        // Remove Ads
                        toMakePurchase();
                        break;
                    default:
                        toStartFragmentFromDrawerNavigation(new Home(), getString(R.string.home), R.id.home);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    // Change Fragment form Drawer Navigation
    private void toStartFragmentFromDrawerNavigation(Fragment fragment, String title, int id) {
        toStartFragment(fragment);
        toChangeTitle(title);
        bottomNavigation.setSelectedItemId(id);
    }

    // Setup Navigation Header Content
    private void toSetupHeaderNavigation(View view) {
        String email = getEmail(this);
        String first = String.valueOf(email.charAt(0)).toUpperCase();
        String name = email.split("@")[0];
        ((MaterialTextView) view.findViewById(R.id.name)).setText(first);
        ((MaterialTextView) view.findViewById(R.id.email)).setText(name);
    }

    /*
    END HERE
     */

    // Share App url
    private void toShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/html");
        String body = SHARE_BODY + getPackageName();
        share.putExtra(Intent.EXTRA_SUBJECT, SHARE_TITLE);
        share.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(share, SHARE_TITLE));
    }

    // Start Activity
    private void toStartActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    // Start Fragment
    private void toStartFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    /*

    Purchase App Code Start from Here

     */


    // To make a Purchase

    private void toMakePurchase() {
        // Write code here to make a purchase
        if (isReady && !isPaid(this)) {
            BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(details).build();
            billingClient.launchBillingFlow(this, params);
        } else {
        }
    }

    // Here Code to Make Purchase Ready


    public void toRemoveAds() {
        skuList.add(REMOVE_AD_ID);
        // Here Work to Remove Ads
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    toLoadSku();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                billingClient.startConnection(this);
            }
        });
    }

    private void toLoadSku() {
        if (billingClient.isReady()) {
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Object skuDetailsObject : list) {
                            final SkuDetails skuDetails = (SkuDetails) skuDetailsObject;
                            if (skuDetails.getSku().equals(REMOVE_AD_ID)) {
                                details = skuDetails;
                                isReady = true;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK && list != null) {
            // Buy
            for (Purchase i : list) {
                if (i.getSku().equals(REMOVE_AD_ID)) {
                    // App Purchase is finish Here
                    getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean(ISPAID, true).apply();
                    Snackbar.make(findViewById(R.id.recycler), R.string.purchase, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        } else {
            // App Purchase is failed
            Snackbar.make(findViewById(R.id.recycler), R.string.purchase_fail, BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }

    /*
    END HERE
     */


    /*

    Bottom Navigation Code start from here

     */

    // For Bottom Navigation
    private void forHandlingBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        toStartFragment(new Home());
                        break;
                    case R.id.category:
                        toStartFragment(new Category());
                        break;
                    default:
                        toStartFragment(new Like());
                }
                toChangeTitle(item.getTitle().toString());
                return true;
            }
        });
    }

    // to Change Toolbar Title

    private void toChangeTitle(String title) {
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    /*
    END HERE
     */

    // To make user login
    private void toCheckForLogin() {
        // This method only run the first time user open app to make user login
        if (isFirstTime(this)) {
            // Open Login Activity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            // Change that user open this app
            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean(ISFIRSTTIMEKEY, false).apply();
        }
    }


    // Subscribe topic for firebase cloud messaging
    private void toSubscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg;
                        if (!task.isSuccessful()) {
                            msg = "failed";
                            Snackbar.make(findViewById(R.id.recycler), msg, BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Load Ads

    private void toLoadAd() {
        // Check Weather Your purchase our app or not
        if (!isPaid(this)) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            AdView banner = findViewById(R.id.banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner.loadAd(adRequest);
        }
    }


    // Close App

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}