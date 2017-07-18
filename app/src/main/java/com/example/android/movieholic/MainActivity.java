package com.example.android.movieholic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.movieholic.adapter.FavoriteAdapter;
import com.example.android.movieholic.adapter.MovieAdapter;
import com.example.android.movieholic.api.JsonUtils;
import com.example.android.movieholic.api.NetworkUtils;
import com.example.android.movieholic.data.FavoriteContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.OnMovieItemClickListener,
        FavoriteAdapter.OnFavoriteClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    
    public static final int MOVIE_LOADER_ID = 1000;
    public static final int FAVORITE_LOADER_ID = 1001;

    public static final int TYPE_POPULAR = 1;
    public static final int TYPE_RATED = 2;
    public static final int TYPE_FAVORITE= 3;

    public static final String MOVIE_LOADER_PARAM = "MOVIE_LOADER_PARAM";
    public static final String FAVORITE_LOADER_PARAM = "FAVORITE_LOADER_PARAM";

    // Flag last screen state
    private static int mCurrentState;
    private static final String LAST_STATE = "LAST_STATE";

    private ProgressBar mLoadingIndicatorProgressBar;

    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;
    private FavoriteAdapter mFavoriteAdapter;

    private Toast mToast;

    private LoaderManager.LoaderCallbacks<String> mMovieLoader;
    private LoaderManager.LoaderCallbacks<Cursor> mFavoriteLoader;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = this.getPreferences(Context.MODE_PRIVATE);

        // Restore State
        if (savedInstanceState == null) {
            mCurrentState = TYPE_POPULAR;
        } else {
            mCurrentState = savedInstanceState.getInt(LAST_STATE);
        }

        mToast = Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_LONG);

        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMovieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMovieRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMovieRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieAdapter = new MovieAdapter(this, this);
        mFavoriteAdapter = new FavoriteAdapter(this, this);

        // Use Loader
        mMovieLoader = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<String>(MainActivity.this) {
                    @Override
                    protected void onStartLoading() {

                        Log.d(TAG, "onStartLoading: movie");
                        if (null == args)
                            return;

                        mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
                        forceLoad();
                    }

                    @Override
                    public String loadInBackground() {
                        Log.d(TAG, "loadInBackground: movie");
                        String paramUrl = (String) args.get(MOVIE_LOADER_PARAM);

                        String response = null;
                        try {
                            URL movieUrl = NetworkUtils.buildUrl(Uri.parse(paramUrl));
                            response = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return response;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onLoadFinished: movie");

                if (data != null && !data.equals("")) {
                    try {
                        JSONObject jsonResponse = new JSONObject(data);
                        JSONArray movieArray = jsonResponse.getJSONArray("results");

                        mMovieRecyclerView.setAdapter(mMovieAdapter);
                        mMovieAdapter.setMovies(JsonUtils.toStringArray(movieArray, JsonUtils.DATA_MOVIE));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (mToast.getView().isShown()) {
                        mToast.cancel();
                    }

                    mToast.show();
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {
                // do nothing
                Log.d(TAG, "onLoaderReset: movie");
            }
        };

        mFavoriteLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.d(TAG, "onCreateLoader: fav");

                return new CursorLoader(
                        MainActivity.this,
                        FavoriteContract.FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.d(TAG, "onLoadFinished: fav");
                if (data != null) {
                    mMovieRecyclerView.setAdapter(mFavoriteAdapter);
                    mFavoriteAdapter.updateCursor(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                Log.d(TAG, "onLoaderReset: fav");
                if (loader != null)
                    loader.cancelLoad();

                mFavoriteAdapter.updateCursor(null);
            }
        };

        Log.d(TAG, "onCreate: ");

        loadRemoteMovies(mCurrentState);
    }

    // FIXME: 7/17/17 First Loader always executed!
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: " + mCurrentState);
        if (mCurrentState == TYPE_FAVORITE) {
            loadFavoriteMovie();
        } else {
            loadRemoteMovies(mCurrentState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenu = item.getItemId();

        if (selectedMenu == R.id.action_popular) {
            loadRemoteMovies(TYPE_POPULAR);
        } else if (selectedMenu == R.id.action_highest_rate) {
            loadRemoteMovies(TYPE_RATED);
        } else if (selectedMenu == R.id.action_favourites) {
            loadFavoriteMovie();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle rotation change so user not lose the last state
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState: ");
        
        outState.putInt(LAST_STATE, mCurrentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "onRestoreInstanceState: ");

        mCurrentState = savedInstanceState.getInt(LAST_STATE);
    }

    /**
     * savedInstanceState not executed when Acitvity going to Destroyed
     * So, save data in onPause to Preferences
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: ");

        final SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(LAST_STATE, mCurrentState);
        preferencesEditor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: ");
        mCurrentState = TYPE_POPULAR;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: ");
    }

    /**
     * Initialize Loader MOVIE_LOADER_ID
     * To load movie from Remote Server
     * @param type Popular or Rated
     */
    private void loadRemoteMovies(int type) {
        // Update state
        mCurrentState = type;

        Uri movieUri = null;

        switch (type) {
            case TYPE_POPULAR:
                movieUri = NetworkUtils.popularUri();
                break;

            case TYPE_RATED:
                movieUri = NetworkUtils.topRatedUri();
                break;

            default:
                throw new IllegalArgumentException("Unknown Type");
        }

        // Register Loader
        Bundle movieBundle = new Bundle();
        movieBundle.putString(MOVIE_LOADER_PARAM, movieUri.toString());
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, movieBundle, mMovieLoader);
    }

    private void loadFavoriteMovie() {
        // Update current state
        mCurrentState = TYPE_FAVORITE;

        Bundle favoriteBundle = new Bundle();
        favoriteBundle.putString(FAVORITE_LOADER_PARAM, FavoriteContract.FavoriteEntry.CONTENT_URI.toString());
        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, favoriteBundle, mFavoriteLoader);
    }

    @Override
    public void viewMovieDetail(String data) {
        Class detailActivity = DetailActivity.class;

        Intent detailIntent = new Intent(this, detailActivity);
        detailIntent.putExtra(DetailActivity.ARG_DETAIL, data);

        startActivity(detailIntent);
    }

    @Override
    public void viewFavoriteDetail(String data) {
        Class detailActivity = DetailActivity.class;

        Intent detailIntent = new Intent(this, detailActivity);
        detailIntent.putExtra(DetailActivity.ARG_DETAIL, data);
        detailIntent.putExtra(DetailActivity.ARG_TYPE, "favorite");

        startActivity(detailIntent);
    }
}
