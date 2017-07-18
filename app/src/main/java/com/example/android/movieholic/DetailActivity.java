package com.example.android.movieholic;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieholic.adapter.TrailerAdapter;
import com.example.android.movieholic.api.JsonUtils;
import com.example.android.movieholic.api.NetworkUtils;
import com.example.android.movieholic.data.FavoriteContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

// TODO: 7/15/17 Add sort by favorite
// TODO: 7/16/17 Movie Details layout contains a section for displaying trailer videos and user reviews.
// TODO: 7/16/17 http://api.themoviedb.org/3/movie/321612/reviews?api_key=02f5881e0bcbaac43e7595337872da18
public class DetailActivity extends AppCompatActivity implements TrailerAdapter.OnTrailerItemClick {
    public static final String TAG = DetailActivity.class.getSimpleName();

    public static final String ARG_DETAIL = "detail";
    public static final String ARG_TYPE = "type";

    private String mMovieId;
    private String mMovieThumb;
    private String mMovieTitle;
    private String mMovieYear;
    private String mMovieSummary;

    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieDurationTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieSynopsisTextView;

    private Button mFavoriteButton;
    private Button mRemoveFavoriteButton;

    private ProgressBar mLoadingIndicatorProgressBar;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        mMovieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        mMovieDurationTextView = (TextView) findViewById(R.id.tv_movie_duration);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        mMovieSynopsisTextView = (TextView) findViewById(R.id.tv_movie_summary);

        mFavoriteButton = (Button) findViewById(R.id.btn_movie_favorite);
        mRemoveFavoriteButton = (Button) findViewById(R.id.btn_remove_movie_favorite);

        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTrailerAdapter = new TrailerAdapter(this, this);

        showMovieDetail();
        showTrailerData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_favourites) {
            Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
            startActivity(favoriteIntent);
            return true;

        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMovieDetail() {
        Intent intent = getIntent();
        String detailData = intent.getStringExtra(ARG_DETAIL);

        String movieId = null;
        if (intent.hasExtra(ARG_TYPE)) {
            movieId = detailData;
        } else {
            String[] parsedDetail = detailData.split("\\|");
            movieId = parsedDetail[0];
        }

        Uri movieDetailUri = NetworkUtils.detailUri(movieId);
        new MovieDetailTask().execute(NetworkUtils.buildUrl(movieDetailUri));
    }

    private void showTrailerData() {
        Intent intent = getIntent();
        String detailData = intent.getStringExtra(ARG_DETAIL);

        Uri trailerUri = null;
        if (intent.hasExtra(ARG_TYPE)) {
            trailerUri = NetworkUtils.trailerUri(detailData);
        } else {
            String[] parsedDetail = detailData.split("\\|");
            trailerUri = NetworkUtils.trailerUri(parsedDetail[0]);
        }

        new TrailerTask().execute(NetworkUtils.buildUrl(trailerUri));
    }

    @Override
    public void viewTrailer(String trailerData) {
        String[] trailers = trailerData.split("\\|");
        String youtubeKey = trailers[0];
        URL youtubeUrl = NetworkUtils.getYoutubeUrl(youtubeKey);

        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl.toString()));
        if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(youtubeIntent);
        }
    }

    /**
     * Action save movie to Favorite DB
     * @param view
     */
    public void addToFavorite(View view) {
        final ContentResolver contentResolver = getContentResolver();

        // Do add to favorite
        ContentValues favoriteValue = new ContentValues();
        favoriteValue.put(FavoriteContract.FavoriteEntry.MOVIE_ID, mMovieId);
        favoriteValue.put(FavoriteContract.FavoriteEntry.MOVIE_THUMB, mMovieThumb);
        favoriteValue.put(FavoriteContract.FavoriteEntry.MOVIE_TITLE, mMovieTitle);
        favoriteValue.put(FavoriteContract.FavoriteEntry.MOVIE_YEAR, mMovieYear);
        favoriteValue.put(FavoriteContract.FavoriteEntry.MOVIE_SUMMARY, mMovieSummary);

        Uri returnUri = contentResolver.insert(FavoriteContract.FavoriteEntry.CONTENT_URI, favoriteValue);

        if (returnUri != null) {
            Toast.makeText(this, "Added to your favorite", Toast.LENGTH_SHORT).show();
            updateFavoriteButton();
        }
    }

    /**
     * Action to remove from Favorite DB
     * @param view
     */
    public void removeFromFavorite(View view) {
        final ContentResolver contentResolver = getContentResolver();
        final Uri queryUri = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI,
                Long.valueOf(mMovieId));

        int deletedRow = contentResolver.delete(
                queryUri,
                null,
                null
        );

        if (deletedRow > 0) {
            Toast.makeText(this, "Removed from your favorite", Toast.LENGTH_SHORT).show();
            updateFavoriteButton();
        }
    }

    /**
     * To check if movie is already exists in Favorite DB or not
     * @return true if exist, false if doesn't exists
     */
    private boolean isExistsInFavorite() {
        final ContentResolver contentResolver = getContentResolver();

        final Uri queryUri = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI,
                Long.valueOf(mMovieId));

        final Cursor cursor = contentResolver.query(
                queryUri,
                new String[] { FavoriteContract.FavoriteEntry._ID },
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Show and hide the appropriate buttons
     * mRemoveFavoriteButton or mFavoriteButton
     */
    private void updateFavoriteButton() {
        if (isExistsInFavorite()) {
            mRemoveFavoriteButton.setVisibility(View.VISIBLE);
            mFavoriteButton.setVisibility(View.GONE);
        } else {
            mRemoveFavoriteButton.setVisibility(View.GONE);
            mFavoriteButton.setVisibility(View.VISIBLE);
        }
    }

    private class MovieDetailTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];

            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                try {
                    JSONObject details = new JSONObject(s);

                    mMovieId = String.valueOf(details.getInt("id"));
                    mMovieThumb = details.getString("poster_path");
                    mMovieTitle = details.getString("original_title");
                    mMovieYear = details.getString("release_date").split("-")[0];
                    mMovieSummary = details.getString("overview");

                    mMovieTitleTextView.setText(mMovieTitle);
                    Picasso.with(DetailActivity.this)
                            .load(NetworkUtils.imageUri(mMovieThumb))
                            .resize(500, 0)
                            .into(mMoviePosterImageView);
                    mMovieReleaseDateTextView.setText(mMovieYear);
                    mMovieDurationTextView.setText(getString(R.string.text_duration, details.getInt("runtime")));
                    mMovieRatingTextView.setText(getString(R.string.text_rating, details.getDouble("vote_average")));
                    mMovieSynopsisTextView.setText(mMovieSummary);

                    updateFavoriteButton();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(DetailActivity.this, "Failed to get data", Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TrailerTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];

            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoadingIndicatorProgressBar.setVisibility(View.GONE);

            if (s != null && !s.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    JSONArray trailerArray = jsonResponse.getJSONArray("results");

                    mTrailerAdapter.setTrailers(JsonUtils.toStringArray(trailerArray, JsonUtils.DATA_TRAILER));
                    mTrailerRecyclerView.setAdapter(mTrailerAdapter);

                    mTrailerRecyclerView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}