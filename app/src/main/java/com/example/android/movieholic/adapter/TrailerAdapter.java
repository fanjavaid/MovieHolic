package com.example.android.movieholic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movieholic.R;

import java.util.Arrays;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private final Context mContext;
    private String[] mTrailers;
    private final OnTrailerItemClick mListener;

    public TrailerAdapter(Context mContext, OnTrailerItemClick mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public void setTrailers(String[] mTrailers) {
        this.mTrailers = mTrailers;
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int trailerItemLayout = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(trailerItemLayout, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Log.d(TrailerAdapter.class.getSimpleName(), "onBindViewHolder: " + Arrays.toString(mTrailers));
        String data = mTrailers[position];
        String[] trailers = data.split("\\|");

        holder.mTrailerTitle.setText(trailers[1]);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailers)
            return 0;

        return mTrailers.length;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTrailerTitle;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            mTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int selectedItem = getAdapterPosition();
            mListener.viewTrailer(mTrailers[selectedItem]);
        }
    }

    public interface OnTrailerItemClick {
        void viewTrailer(String trailerData);
    }
}
