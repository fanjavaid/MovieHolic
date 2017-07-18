package com.example.android.movieholic.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fanjavaid on 7/15/17.
 */

public class FavoriteContract {

    // Define Authority, Content URI, and PATH
    public static final String AUTHORITY = "com.fanjavaid.movieholic";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH = "favourite";

    private FavoriteContract() {
    }

    public static final class FavoriteEntry implements BaseColumns {
        // Build final Content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String TABLE_NAME = "favourites";

        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_YEAR = "movie_year";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_THUMB = "movie_thumb";
        public static final String MOVIE_SUMMARY = "movie_summary";
        public static final String CREATED_AT = "created_at";
    }
}
