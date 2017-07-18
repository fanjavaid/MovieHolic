package com.example.android.movieholic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movieholic.R;

/**
 * Created by fanjavaid on 7/18/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private Context mContext;
    private String mData;

    public ReviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setReviewData(String mData) {
        this.mData = mData;
        this.notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int reviewItemLayout = R.layout.review_item;
        final LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(reviewItemLayout, parent, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mAuthorTextView;
        private TextView mReviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_author_name);
            mReviewTextView = (TextView) itemView.findViewById(R.id.tv_review);
        }
    }
}
