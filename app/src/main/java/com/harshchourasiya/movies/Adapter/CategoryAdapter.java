package com.harshchourasiya.movies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.harshchourasiya.movies.Model.Genres;
import com.harshchourasiya.movies.Model.Movie;
import com.harshchourasiya.movies.R;
import com.harshchourasiya.movies.Show.Show;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

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


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ParentViewHolder> {

    Context context;
    ArrayList<Genres> list;

    public CategoryAdapter(ArrayList<Genres> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        // Setting Titles and More Button Click Handler
        holder.title.setText(list.get(position).getTitle());
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Show Activity
                Intent intent = new Intent(context, Show.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(TITLE, list.get(position).getTitle());
                intent.putExtra(ID, list.get(position).getId());
                context.startActivity(intent);
            }
        });
        // Add Home Adapter for Recycler View
        HomeAdapter adapter;
        ArrayList<Movie> movies = new ArrayList<>();
        adapter = new HomeAdapter(movies, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(adapter);
        // Loading Data from Url
        toLoadData(SPECIFIC_GENRE_URL + list.get(position).getId(), adapter, movies);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void toLoadData(String url, HomeAdapter adapter, ArrayList<Movie> movies) {
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
                            // taking only 10 Movies at a time
                            for (int i = 0; i < 10; i++) {
                                //getting the json object of the particular index inside the array
                                // Catching error if happened
                                try {
                                    JSONObject object = array.getJSONObject(i);
                                    Movie movie = new Movie(object.getString(TITLE), object.getString(ID), object.getString(POSTER), object.getString(RATE));
                                    movies.add(movie);
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title;
        MaterialButton more;
        RecyclerView recyclerView;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (MaterialTextView) itemView.findViewById(R.id.title);
            more = (MaterialButton) itemView.findViewById(R.id.more);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler);
        }
    }
}
