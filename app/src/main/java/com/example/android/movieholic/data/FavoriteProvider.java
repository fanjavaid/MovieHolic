package com.example.android.movieholic.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by fanjavaid on 7/15/17.
 */

public class FavoriteProvider extends ContentProvider {

    // Define URI types
    public static final int FAVOURITE = 100;
    public static final int FAVOURITE_BY_ID = 101;

    // Create URI matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Define Favourite db helper
    private FavoriteDbHelper mFavoriteDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add criteria
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH, FAVOURITE);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH + "/#", FAVOURITE_BY_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFavoriteDbHelper = new FavoriteDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int queryUri = sUriMatcher.match(uri);
        final SQLiteDatabase db = mFavoriteDbHelper.getReadableDatabase();
        final Cursor returnedCursor;

        switch (queryUri) {
            case FAVOURITE:
                returnedCursor = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        FavoriteContract.FavoriteEntry.CREATED_AT + " DESC"
                );

                break;

            case FAVOURITE_BY_ID:
                String movieId = uri.getLastPathSegment();
                String mSelection = FavoriteContract.FavoriteEntry.MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{ movieId };

                returnedCursor = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        FavoriteContract.FavoriteEntry.CREATED_AT + " DESC"
                );

                break;

            default:
                throw new UnsupportedOperationException("Cannot perform action for URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnedCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int insertUri = sUriMatcher.match(uri);
        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();
        Uri returnedUri = null;

        switch (insertUri) {
            case FAVOURITE:
                long insertedId = db.insert(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        null,
                        values
                );

                if (insertedId != -1)
                    returnedUri = ContentUris.withAppendedId(uri, insertedId);

                break;

            default:
                throw new UnsupportedOperationException("Cannot perform action for URI " + uri);
        }

        getContext().getContentResolver().notifyChange(returnedUri, null);

        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleteUri = sUriMatcher.match(uri);
        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        final int deletedRowId;
        switch (deleteUri) {
            case FAVOURITE_BY_ID:
                String movieId = uri.getLastPathSegment();

                deletedRowId = db.delete(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        FavoriteContract.FavoriteEntry.MOVIE_ID + "=?",
                        new String[] { movieId }
                );
                break;

            default:
                throw new UnsupportedOperationException("Cannot perform action for URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return deletedRowId;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported for update operation!");
    }
}
