package com.example.android.movieholic.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fanjavaid on 7/15/17.
 */

public class FavoriteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favourites";
    public static final int DATABASE_VERSION = 2;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE ")
                .append(FavoriteContract.FavoriteEntry.TABLE_NAME).append("(")
                .append(FavoriteContract.FavoriteEntry._ID).append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ")
                .append(FavoriteContract.FavoriteEntry.MOVIE_ID).append(" INTEGER NOT NULL, ")
                .append(FavoriteContract.FavoriteEntry.MOVIE_THUMB).append(" TEXT NOT NULL, ")
                .append(FavoriteContract.FavoriteEntry.MOVIE_YEAR).append(" INTEGER NOT NULL, ")
                .append(FavoriteContract.FavoriteEntry.MOVIE_TITLE).append(" TEXT NOT NULL, ")
                .append(FavoriteContract.FavoriteEntry.MOVIE_SUMMARY).append(" TEXT NOT NULL, ")
                .append(FavoriteContract.FavoriteEntry.CREATED_AT).append(" TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
                .append(");");

        String query = queryBuilder.toString();
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);

        onCreate(db);
    }
}
