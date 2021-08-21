package com.harshchourasiya.movies.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.harshchourasiya.movies.Data.Data.DAY_TREADING_URL;
import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.POSTER;
import static com.harshchourasiya.movies.Data.Data.RATE;
import static com.harshchourasiya.movies.Data.Data.TITLE;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;


public class Home extends Fragment {

    ArrayList<Movie> dayMovieList = new ArrayList<>();
    HomeAdapter dayAdapter;

    public Home() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Check if internet if working fine
        toCheckInternet(getContext());
        // Loading recycler view
        toLoadDayRecyclerView(view);
        // Loading Data from API
        for (int i = 1; i <= 3; i++) {
            toLoadDayData(view, DAY_TREADING_URL + i);
        }
        return view;
    }


    private void toLoadDayRecyclerView(View view) {
        dayAdapter = new HomeAdapter(dayMovieList, getContext());
        RecyclerView dayRecycler = (RecyclerView) view.findViewById(R.id.recycler_day);
        dayRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        dayRecycler.setAdapter(dayAdapter);
    }

    private void toLoadDayData(View view, String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        ((ProgressBar) view.findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);

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
                                    // Adding Data to List
                                    JSONObject object = array.getJSONObject(i);
                                    Movie movie = new Movie(object.getString(TITLE), object.getString(ID), object.getString(POSTER), object.getString(RATE));
                                    dayMovieList.add(movie);
                                } catch (JSONException e) {
                                }
                            }
                            dayAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}