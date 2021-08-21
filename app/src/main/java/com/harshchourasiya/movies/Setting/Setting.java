package com.harshchourasiya.movies.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.harshchourasiya.movies.Login.Login;
import com.harshchourasiya.movies.MainActivity;
import com.harshchourasiya.movies.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import static com.harshchourasiya.movies.Data.Data.EMAILKEY;
import static com.harshchourasiya.movies.Data.Data.FACEBOOK_PAGE_URL;
import static com.harshchourasiya.movies.Data.Data.INSTAGRAM_PAGE_URL;
import static com.harshchourasiya.movies.Data.Data.ISDARK;
import static com.harshchourasiya.movies.Data.Data.ISPAID;
import static com.harshchourasiya.movies.Data.Data.KEY;
import static com.harshchourasiya.movies.Data.Data.REMOVE_AD_ID;
import static com.harshchourasiya.movies.Data.Data.SHARE_BODY;
import static com.harshchourasiya.movies.Data.Data.SHARE_TITLE;
import static com.harshchourasiya.movies.Data.Data.TWITTER_PAGE_URL;
import static com.harshchourasiya.movies.Data.Data.USERNAME;
import static com.harshchourasiya.movies.Data.Data.getEmail;
import static com.harshchourasiya.movies.Data.Data.isDark;
import static com.harshchourasiya.movies.Data.Data.isPaid;

public class Setting extends AppCompatActivity implements PurchasesUpdatedListener {

    private Switch dark;
    private boolean isLogin;
    private LinearLayout facebook, instagram, twitter;
    private MaterialTextView logout, share, rate, remove;
    private boolean isReady = false;
    private SkuDetails details;
    private BillingClient billingClient;
    private List skuList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // Initializing Variables
        toInit();
        // Setting Content
        toSetContent();
        // On Back Click
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toBack();
            }
        });
        // For Dark Mode
        toDarkMode();
        // For Login or logout
        toLog();
        // for rate
        toRate();
        // for Share
        toShare();
        // to Open facebook page
        toOpenFacebook();
        // to Open Instagram page
        toOpenInstagram();
        // to Open twitter page
        toOpenTwitter();
        // to Remove ads
        toRemoveAds();
        // to Make Purchase Possible
        toMakePurchase();
    }

    // Initializing Variables

    private void toInit() {
        dark = (Switch) findViewById(R.id.darkModeSwitch);
        facebook = (LinearLayout) findViewById(R.id.facebook);
        instagram = (LinearLayout) findViewById(R.id.instagram);
        twitter = (LinearLayout) findViewById(R.id.twitter);
        share = (MaterialTextView) findViewById(R.id.share);
        rate = (MaterialTextView) findViewById(R.id.rate);
        remove = (MaterialTextView) findViewById(R.id.remove);
        logout = (MaterialTextView) findViewById(R.id.logout);
    }


    /*

    Setting Content  like user is login or not

     */


    private void toSetContent() {
        ((TextView) findViewById(R.id.title)).setText(R.string.setting);
        dark.setChecked(isDark(this));
        if (getEmail(this).equals(USERNAME)) {
            isLogin = false;
            logout.setText(R.string.login_text);
        } else {
            isLogin = true;
            logout.setText(R.string.logout);
        }

    }

    /*

    Social Media page Code Start from Here

     */

    private void toOpenFacebook() {
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOpenWeb(FACEBOOK_PAGE_URL);
            }
        });
    }

    private void toOpenInstagram() {
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOpenWeb(INSTAGRAM_PAGE_URL);
            }
        });
    }

    private void toOpenTwitter() {
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOpenWeb(TWITTER_PAGE_URL);
            }
        });
    }

    /*

    END HERE

     */

    /*

    Make a Purchase Code Start from here

     */

    private void toMakePurchase() {
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReady && !isPaid(Setting.this)) {
                    BillingFlowParams params = BillingFlowParams.newBuilder().setSkuDetails(details).build();
                    billingClient.launchBillingFlow(Setting.this, params);
                } else {
                    Log.i(KEY, "HERE");
                }
            }
        });
    }

    // Remove Ads Work Here


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
            for (Purchase i : list) {
                if (i.getSku().equals(REMOVE_AD_ID)) {
                    // User finally Complete Purchase Process here
                    getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean(ISPAID, true).apply();
                    Snackbar.make(findViewById(R.id.recycler), R.string.purchase, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        } else {
            Snackbar.make(findViewById(R.id.recycler), R.string.purchase_fail, BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }


    /*

    END HERE

     */



    // Share App
    private void toShare() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/html");
                String body = SHARE_BODY + getPackageName();
                share.putExtra(Intent.EXTRA_SUBJECT, SHARE_TITLE);
                share.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(share, SHARE_TITLE));
            }
        });
    }


    // Open website of google playstore to rate app

    private void toRate() {
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOpenWeb("https://play.google.com/store/apps/details?id=" + getPackageName());
            }
        });
    }

    private void toOpenWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    /*

    Login and Logout Code start from here

     */
    private void toLog() {

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    toOpenDialog();
                } else {
                    toLogin();
                }
            }
        });
    }

    private void toOpenDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle(R.string.sure)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toLogout();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    private void toLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void toLogout() {
        logout.setText(R.string.login_text);
        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(EMAILKEY, USERNAME).apply();
        isLogin = false;
    }

    /*
    END HERE
     */

    /*

    Dark mode Code start from here

     */

    private void toDarkMode() {
        dark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toMakeDarkMode(isChecked);
            }
        });
    }


    private void toMakeDarkMode(boolean isChecked) {
        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean(ISDARK, isChecked).apply();
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Intent intent = new Intent(getApplicationContext(), Setting.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /*
    End Here
     */

    private void toBack() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        toBack();
    }



}