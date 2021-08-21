package com.harshchourasiya.movies.Detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.harshchourasiya.movies.Adapter.HomeAdapter;
import com.harshchourasiya.movies.Login.Login;
import com.harshchourasiya.movies.Model.Like;
import com.harshchourasiya.movies.Model.Movie;
import com.harshchourasiya.movies.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static com.harshchourasiya.movies.Data.Data.BACKGROUND_IMAGE;
import static com.harshchourasiya.movies.Data.Data.EMAIL;
import static com.harshchourasiya.movies.Data.Data.GET_API;
import static com.harshchourasiya.movies.Data.Data.GET_DETAILS;
import static com.harshchourasiya.movies.Data.Data.GET_SIMILAR;
import static com.harshchourasiya.movies.Data.Data.GET_SIMILAR_LAST;
import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.IMAGE_W200;
import static com.harshchourasiya.movies.Data.Data.IMAGE_W500;
import static com.harshchourasiya.movies.Data.Data.KEY;
import static com.harshchourasiya.movies.Data.Data.LIKE;
import static com.harshchourasiya.movies.Data.Data.OVERVIEW;
import static com.harshchourasiya.movies.Data.Data.POSTER;
import static com.harshchourasiya.movies.Data.Data.RATE;
import static com.harshchourasiya.movies.Data.Data.TAGLINE;
import static com.harshchourasiya.movies.Data.Data.TIME;
import static com.harshchourasiya.movies.Data.Data.TITLE;
import static com.harshchourasiya.movies.Data.Data.USERKEY;
import static com.harshchourasiya.movies.Data.Data.USERNAME;
import static com.harshchourasiya.movies.Data.Data.USERS;
import static com.harshchourasiya.movies.Data.Data.getEmail;
import static com.harshchourasiya.movies.Data.Data.getUserKey;
import static com.harshchourasiya.movies.Data.Data.isPaid;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;

public class Detail extends AppCompatActivity {

    // Initializing Variables for Detailed
    ImageView backgroundImage, poster, like, back;
    MaterialTextView name, tagline, overview;
    RecyclerView recyclerView;
    ProgressBar progress_rate, loading;
    TextView rate, time;
    ArrayList<Movie> list = new ArrayList<>();
    LinearLayout parent;
    HomeAdapter adapter;
    boolean isLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // For Back
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Check Internet
        toCheckInternet(this);
        // get Specific Id for your Api Like we get Id from Movie
        String id = getIntent().getStringExtra(ID);
        // initializing Variables
        toInit();
        // To Get if user like this specific id or not
        isLiked();
        // Load Content
        toLoadContent(GET_DETAILS + id + GET_API);
        // load recycler view
        toLoadRecyclerView();
        // load recycler view data
        toLoadRecyclerViewData(GET_SIMILAR + id + GET_SIMILAR_LAST);
        // like Click handler
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if your login or not
                if (!getEmail(Detail.this).equals(USERNAME)) {
                    if (isLike) {
                        // if your like this id previously then unlike this
                        toUnlike();
                    } else {
                        // or like this
                        toLike();
                    }
                } else {
                    // If not Login the say to login
                    Snackbar.make(findViewById(R.id.overview), getString(R.string.login_to_like), BaseTransientBottomBar.LENGTH_LONG)
                            .setAction(R.string.login_text, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // open to Login Activity
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });
        // Load Ads
        toLoadAd();
    }

    // Initializing variables
    private void toInit() {
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        poster = (ImageView) findViewById(R.id.poster);
        like = (ImageView) findViewById(R.id.like);
        name = (MaterialTextView) findViewById(R.id.name);
        tagline = (MaterialTextView) findViewById(R.id.tagLine);
        overview = (MaterialTextView) findViewById(R.id.overview);
        progress_rate = (ProgressBar) findViewById(R.id.rate_progress);
        rate = (TextView) findViewById(R.id.rate);
        time = (TextView) findViewById(R.id.time);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        loading = (ProgressBar) findViewById(R.id.progress);
        parent = (LinearLayout) findViewById(R.id.linearLayout);
    }


    /*

    for Like unlike and check like code start from here

     */

    private void isLiked() {
        // Check if user login or not
        if (!getEmail(this).equals(USERNAME)) {
            // get user like list to get if your like this one or not
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String ref = USERS + "/" + getUserKey(this) + "/" + LIKE;
            DatabaseReference myRef = database.getReference(ref);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Like lik = child.getValue(Like.class);
                        if (lik.getId().equals(getIntent().getStringExtra(ID))) {
                            // got that user like this
                            like.setImageResource(R.drawable.like_filled);
                            isLike = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // When Something gone wrong
                }
            });

        }
    }

    private void toUnlike() {
        // Set half filled heart image
        like.setImageResource(R.drawable.ic_like_unfilled);
        isLike = false;
        // delete this if from user likes list
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String ref = USERS + "/" + getUserKey(this) + "/" + LIKE;
        DatabaseReference myRef = database.getReference(ref);
        myRef.orderByChild(ID).equalTo(getIntent().getStringExtra(ID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    child.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void toLike() {
        // Set filled heart image
        like.setImageResource(R.drawable.like_filled);
        isLike = true;
        // Save User like data in firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.orderByChild(EMAIL).equalTo(getEmail(this)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Key = "";
                for (DataSnapshot child : snapshot.getChildren()) {
                    Key = child.getKey();
                }
                Like like = new Like(getIntent().getStringExtra(ID));
                DatabaseReference ref = database.getReference(USERS + "/" + Key);
                ref.child(LIKE).push().setValue(like);
                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(USERKEY, Key).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If something gone wrong
            }
        });

    }

    /*

    Loading Recycler view and Recycler Content from here
     */

    private void toLoadRecyclerView() {
        adapter = new HomeAdapter(list, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void toLoadRecyclerViewData(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray array = obj.getJSONArray("results");
                            //now looping through all the elements of the json array
                            for (int i = 0; i < array.length(); i++) {
                                //getting the json object of the particular index inside the array
                                // Catching error if happened
                                try {
                                    // storing list for similar recommandation
                                    JSONObject object = array.getJSONObject(i);
                                    Movie movie = new Movie(object.getString(TITLE), object.getString(ID), object.getString(POSTER), object.getString(RATE));
                                    list.add(movie);
                                } catch (JSONException e) {
                                    Log.i(KEY, "Error " + e.getMessage() + " Cause " + e.getCause());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(KEY, "Error " + e.getMessage() + " Cause " + e.getCause());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }


    private void toLoadContent(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        loading.setAlpha(0);
                        toHideAndShow();
                        try {
                            //getting the whole json object from the response
                            // Setting whole content
                            JSONObject obj = new JSONObject(response);
                            Glide.with(Detail.this).load(IMAGE_W200 + obj.getString(POSTER)).into(poster);
                            Glide.with(Detail.this).load(IMAGE_W500 + obj.getString(BACKGROUND_IMAGE)).into(backgroundImage);
                            tagline.setText(obj.getString(TAGLINE));
                            name.setText(obj.getString(TITLE));
                            overview.setText(obj.getString(OVERVIEW));
                            time.setText(obj.getString(TIME) + "M");
                            rate.setText(obj.getString(RATE));
                            int progress = (int) Integer.parseInt(obj.getString(RATE).substring(0, 1));
                            progress_rate.setProgress(progress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(KEY, "Error " + e.getMessage() + " Cause " + e.getCause());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    // Hide Content for progress
    private void toHideAndShow() {
        poster.setAlpha(1f);
        parent.setAlpha(1);
        back.setAlpha(1f);
        recyclerView.setAlpha(1);
    }

    /*

    Ads Code Start of here

     */

    // Loading Ads
    private void toLoadAd() {
        if (!isPaid(this)) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            // Select randomly to Show ads or not
            boolean random = new Random().nextBoolean();
            if (random) {

                toShowAds();

            }
        }
    }


    // Showing ads
    private void toShowAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                interstitialAd.show(Detail.this);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
            }
        });
    }

}