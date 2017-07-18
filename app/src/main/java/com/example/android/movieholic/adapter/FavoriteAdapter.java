package com.example.android.movieholic.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movieholic.R;
import com.example.android.movieholic.api.NetworkUtils;
import com.example.android.movieholic.data.FavoriteContract;
import com.squareup.picasso.Picasso;

/**
 * Created by fanjavaid on 7/16/17.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnFavoriteClickListener mListener;

    public FavoriteAdapter(Context mContext, OnFavoriteClickListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int favoriteItemLayout = R.layout.movie_item;
        final LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(favoriteItemLayout, parent, false);
        FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);

//            int movieId = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.MOVIE_ID);
            int thumbColIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.MOVIE_THUMB);
            String thumb = mCursor.getString(thumbColIndex);

            Picasso.with(mContext)
                    .load(NetworkUtils.imageUri(thumb))
                    .into(holder.mMoviePosterImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;

        return mCursor.getCount();
    }

    public void updateCursor(Cursor cursor) {
        mCursor = cursor;
        this.notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mMoviePosterImageView;

        public FavoriteViewHolder(View itemView) {
            super(itemView);

            mMoviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);

            // Get movie ID
            int movieIdIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.MOVIE_ID);
            String movieId = String.valueOf(mCursor.getInt(movieIdIndex));

            mListener.viewFavoriteDetail(movieId);
        }
    }

    public interface OnFavoriteClickListener {
        void viewFavoriteDetail(String data);
    }
}
