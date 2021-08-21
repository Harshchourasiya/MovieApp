package com.harshchourasiya.movies.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.harshchourasiya.movies.Adapter.HomeAdapter;
import com.harshchourasiya.movies.Model.Movie;
import com.harshchourasiya.movies.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.harshchourasiya.movies.Data.Data.DAY_TREADING_URL;
import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.KEY;
import static com.harshchourasiya.movies.Data.Data.PAGE_URL;
import static com.harshchourasiya.movies.Data.Data.POSTER;
import static com.harshchourasiya.movies.Data.Data.RATE;
import static com.harshchourasiya.movies.Data.Data.SEARCH_URL;
import static com.harshchourasiya.movies.Data.Data.TITLE;
import static com.harshchourasiya.movies.Data.Data.isPaid;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;

public class Search extends AppCompatActivity {
    private HomeAdapter adapter;
    private ArrayList<Movie> list = new ArrayList<>();
    private LinearLayout parent;
    private MaterialTextView heading;
    private FirebaseAnalytics mFirebaseAnalytics;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_search);
        // to Load Ads
        toLoadAd();
        // to Initializing Variables
        toInit();
        // to Check Internet Connection
        toCheckInternet(this);
        // Load Recyclerview
        toLoadRecyclerView();
        // Load Recycler view Content
        toLoadRecyclerViewData();
        // for Search Button
        forSearchButton();
    }


    // Initializing Variables
    private void toInit() {
        adapter = new HomeAdapter(list, this);
        parent = (LinearLayout) findViewById(R.id.result_parent);
        heading = (MaterialTextView) findViewById(R.id.top_heading);
        animationView = (LottieAnimationView)findViewById(R.id.animationView);
    }


    /*

    Loading Recyclerview and Data for recycler view code start here

     */

    private void toLoadRecyclerViewData() {
        toLoadData(DAY_TREADING_URL + 2);
    }

    private void toLoadRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }

    /*

    END HERE

     */


    /*

    Search Button and Search quire Code start from here

     */


    private void forSearchButton() {
        TextInputEditText forSearch = (TextInputEditText) findViewById(R.id.searchText);
        forSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        // When your enter ENTER in keyboard
                        String quire = forSearch.getText().toString();
                        if (quire.length() > 0) {
                            toSearch(quire);
                            return true;
                        } else {
                            Snackbar.make(findViewById(R.id.searchText), R.string.empty, BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quire = forSearch.getText().toString();
                if (quire.length() > 0) {
                    toSearch(quire);
                } else {
                    Snackbar.make(findViewById(R.id.searchText), R.string.empty, BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void toSearch(String quire) {
        animationView.setVisibility(View.GONE);
        toSendDataToAnalytics(quire);
        list.clear();
        for (int i = 1; i <= 2; i++) {
            toLoadData(SEARCH_URL + quire + PAGE_URL + i);
        }
    }


    private void toLoadData(String url) {
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
                                    JSONObject object = array.getJSONObject(i);
                                    // Storing data
                                    Movie movie = new Movie(object.getString(TITLE), object.getString(ID), object.getString(POSTER), object.getString(RATE));
                                    list.add(movie);
                                } catch (JSONException e) {
                                    Log.i(KEY, "Error " + e.getMessage() + " Cause " + e.getCause());
                                }
                            }
                            if(list.size()==0){
                                // Show not found any result error
                                toShowNotFound();
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

    /*

    END HERE

     */

    /*

    To Show Result not found Code start Here

     */

    private void toShowNotFound(){
        animationView.setVisibility(View.VISIBLE);
    }



    /*

    End Here
     */

    /*

    Sending Search Quires to Google Analytics and Loading Ads

     */

    private void toSendDataToAnalytics(String data) {
        // Sending Quire data to google Analytics for Google Report
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, data);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    private void toLoadAd() {
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

}