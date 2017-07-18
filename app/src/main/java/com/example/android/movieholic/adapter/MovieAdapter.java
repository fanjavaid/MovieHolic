package com.example.android.movieholic.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movieholic.R;
import com.example.android.movieholic.api.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieGridViewHolder> {
    private String[] movies;
    private final Context mContext;

    private final OnMovieItemClickListener mListener;

    public MovieAdapter(Context mContext, OnMovieItemClickListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public void setMovies(String[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int movieItemLayout = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(movieItemLayout, parent, false);

        return new MovieGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        String moviePoster = movies[position];
        String[] datas = moviePoster.split("\\|");
        Log.d(MovieAdapter.class.getSimpleName(), "onBindViewHolder: " + moviePoster);
        Log.d(MovieAdapter.class.getSimpleName(), "onBindViewHolder: " + Arrays.toString(datas));

        Uri imageUri = NetworkUtils.imageUri(datas[3]);

        Picasso.with(mContext)
                .load(imageUri)
                .into(holder.ivMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == movies)
            return 0;

        return movies.length;
    }

    class MovieGridViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        private final ImageView ivMoviePosterImageView;

        public MovieGridViewHolder(View itemView) {
            super(itemView);

            ivMoviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int selectedMovie = getAdapterPosition();
            mListener.viewMovieDetail(movies[selectedMovie]);
        }
    }

    // Click Listener
    public interface OnMovieItemClickListener {
        void viewMovieDetail(String data);
    }
}
