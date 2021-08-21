package com.harshchourasiya.movies.Show;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.KEY;
import static com.harshchourasiya.movies.Data.Data.POSTER;
import static com.harshchourasiya.movies.Data.Data.RATE;
import static com.harshchourasiya.movies.Data.Data.SPECIFIC_GENRE_URL;
import static com.harshchourasiya.movies.Data.Data.TITLE;
import static com.harshchourasiya.movies.Data.Data.isPaid;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;

public class Show extends AppCompatActivity {
    private HomeAdapter adapter;
    private ArrayList<Movie> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        // Loading Ads
        toLoadAd();
        // Setting Title
        ((TextView) findViewById(R.id.title)).setText(getIntent().getStringExtra(TITLE));
        // Checking Internet Connection
        toCheckInternet(this);
        // Setup Recycler view
        adapter = new HomeAdapter(list, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        // getting Key from intent
        String id = getIntent().getStringExtra(ID);
        // 2 is how many pages you want
        for (int i = 1; i <= 2; i++) {
            // loading data
            toLoadData(SPECIFIC_GENRE_URL + id);
        }
        // for back
        forBack();
    }


    private void toLoadData(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.GONE);

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
                                    // storing data
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


    private void forBack() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void toLoadAd() {
        // check if user Purchase app or not
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