package com.harshchourasiya.movies.Category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.harshchourasiya.movies.Adapter.CategoryAdapter;
import com.harshchourasiya.movies.Model.Genres;
import com.harshchourasiya.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.harshchourasiya.movies.Data.Data.GENRE_URL;
import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.KEY;
import static com.harshchourasiya.movies.Data.Data.NAME;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;

public class Category extends Fragment {

    CategoryAdapter adapter;
    ArrayList<Genres> list = new ArrayList<>();

    public Category() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        // Checking Internet Connections
        toCheckInternet(getContext());
        // Connecting Recycler View and Category Adapter
        toLoadRecyclerView(view);
        // Loading Data for recycler view
        toLoadData(view, GENRE_URL);
        return view;
    }

    private void toLoadRecyclerView(View view) {
        adapter = new CategoryAdapter(list, getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void toLoadData(View view, String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        ((ProgressBar) view.findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray array = obj.getJSONArray("genres");
                            //now looping through all the elements of the json array
                            for (int i = 0; i < array.length(); i++) {
                                //getting the json object of the particular index inside the array
                                // Catching error if happened
                                try {
                                    JSONObject object = array.getJSONObject(i);
                                    Genres genre = new Genres(object.getString(NAME), object.getString(ID));
                                    list.add(genre);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}