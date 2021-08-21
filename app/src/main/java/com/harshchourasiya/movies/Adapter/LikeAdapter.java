package com.harshchourasiya.movies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.harshchourasiya.movies.Detail.Detail;
import com.harshchourasiya.movies.Model.LikesDetail;
import com.harshchourasiya.movies.R;

import java.util.ArrayList;

import static com.harshchourasiya.movies.Data.Data.ID;
import static com.harshchourasiya.movies.Data.Data.IMAGE_W200;
import static com.harshchourasiya.movies.Data.Data.TITLE;


public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ParentViewHolder> {

    Context context;
    ArrayList<LikesDetail> list;

    public LikeAdapter(ArrayList<LikesDetail> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_poster, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        // Setting animation
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.movie_poster);
        animation.setStartOffset(position * 80);
        holder.parent.setAnimation(animation);

        // Setting Image, Title and Overview
        Glide.with(context).load(IMAGE_W200 + list.get(position).getPoster()).into(holder.poster);
        holder.title.setText(list.get(position).getTitle());
        holder.rate.setText(list.get(position).getRate());
        int progress = (int) Integer.parseInt(list.get(position).getRate().substring(0, 1));
        holder.rateProgress.setProgress(progress);
        holder.overview.setText(list.get(position).getOverview());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Detailed Activity
                Intent intent = new Intent(context, Detail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(TITLE, list.get(position).getTitle());
                intent.putExtra(ID, list.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, rate, overview;
        ProgressBar rateProgress;
        LinearLayout parent;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            rate = (TextView) itemView.findViewById(R.id.rate);
            rateProgress = (ProgressBar) itemView.findViewById(R.id.rate_progress);
            overview = (TextView) itemView.findViewById(R.id.overview);
            parent = (LinearLayout) itemView.findViewById(R.id.parent);
        }
    }
}