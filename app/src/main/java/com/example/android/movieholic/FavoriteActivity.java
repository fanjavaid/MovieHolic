package com.example.android.movieholic;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.movieholic.adapter.FavoriteAdapter;
import com.example.android.movieholic.data.FavoriteContract;

public class FavoriteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = FavoriteActivity.class.getSimpleName();

    private static final int FAVORITE_LOADER_ID = 300892;

    private RecyclerView mRecyclerView;
    private FavoriteAdapter mFavoriteAdapter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_favorite);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFavoriteAdapter = new FavoriteAdapter(this, null);
        mRecyclerView.setAdapter(mFavoriteAdapter);

        showLoading();
        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoading() {
        mProgressDialog.show();
    }

    private void hideLoading() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                FavoriteContract.FavoriteEntry.CREATED_AT + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteAdapter.updateCursor(data);

        hideLoading();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteAdapter.updateCursor(null);
    }
}
