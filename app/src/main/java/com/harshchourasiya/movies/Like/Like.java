package com.harshchourasiya.movies.Like;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.harshchourasiya.movies.Adapter.LikeAdapter;
import com.harshchourasiya.movies.Login.Login;
import com.harshchourasiya.movies.Model.LikesDetail;
import com.harshchourasiya.movies.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.harshchourasiya.movies.Data.Data.GET_API;
import static com.harshchourasiya.movies.Data.Data.GET_DETAILS;
import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.KEY;
import static com.harshchourasiya.movies.Data.Data.LIKE;
import static com.harshchourasiya.movies.Data.Data.OVERVIEW;
import static com.harshchourasiya.movies.Data.Data.POSTER;
import static com.harshchourasiya.movies.Data.Data.RATE;
import static com.harshchourasiya.movies.Data.Data.TITLE;
import static com.harshchourasiya.movies.Data.Data.USERNAME;
import static com.harshchourasiya.movies.Data.Data.USERS;
import static com.harshchourasiya.movies.Data.Data.getEmail;
import static com.harshchourasiya.movies.Data.Data.getUserKey;
import static com.harshchourasiya.movies.Data.Data.toCheckInternet;


public class Like extends Fragment {


    LikeAdapter adapter;
    ArrayList<LikesDetail> list = new ArrayList<>();

    public Like() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_like, container, false);
        // Checking Internet
        toCheckInternet(getContext());
        adapter = new LikeAdapter(list, getContext());
        // Loading Recycler veiw
        toLoadRecycler(view);
        // to Load like id Data
        toLoadLikeData(view);
        return view;
    }

    /*

    Clear Code when swipe left or right Code start from here

     */

    // Recycler view swipe to delete code

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            LikesDetail like = list.get(position);
            toRemoveLike(like);
            list.remove(position);
            adapter.notifyDataSetChanged();
            Snackbar.make(getView(), like.getTitle() + " Removed", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    };

    // Code to Remove like

    private void toRemoveLike(LikesDetail like) {
        // Delete Like id from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String ref = USERS + "/" + getUserKey(getContext()) + "/" + LIKE;
        DatabaseReference myRef = database.getReference(ref);
        myRef.orderByChild(ID).equalTo(like.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    // Load like data from firebase user database

    private void toLoadLikeData(View view) {
        // if user login or not
        if (!getEmail(getContext()).equals(USERNAME)) {
            // getting data from firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String ref = USERS + "/" + getUserKey(getContext()) + "/" + LIKE;
            DatabaseReference myRef = database.getReference(ref);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        com.harshchourasiya.movies.Model.Like like = child.getValue(com.harshchourasiya.movies.Model.Like.class);
                        toLoadRecyclerData(GET_DETAILS + like.getId() + GET_API, view);
                    }
                    // if list is empty then your like nothing
                    if (!snapshot.exists()) {
                        // Write code here if list is zero
                        ((ProgressBar) view.findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.zero_like).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // If something gone wrong
                }
            });


        } else {
            // Ask user to login
            ((ProgressBar) view.findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.login_to_like_parent).setVisibility(View.VISIBLE);
            view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getContext().startActivity(intent);
                }
            });
        }
    }


    // Loading recycler view


    private void toLoadRecycler(View view) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    // Loading Data for Recycler view

    private void toLoadRecyclerData(String url, View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        ((ProgressBar) view.findViewById(R.id.progress)).setVisibility(View.INVISIBLE);

                        try {
                            //getting the whole json object from the response
                            // getting data
                            JSONObject obj = new JSONObject(response);
                            LikesDetail detail = new LikesDetail(obj.getString(TITLE), obj.getString(POSTER), obj.getString(RATE),
                                    obj.getString(OVERVIEW), obj.getString(ID)
                            );
                            list.add(detail);
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
                        ((ProgressBar) view.findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

}