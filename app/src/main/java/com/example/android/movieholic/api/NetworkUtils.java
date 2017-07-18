package com.example.android.movieholic.api;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private final static String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String API_KEY = "02f5881e0bcbaac43e7595337872da18";

    private final static String QUERY_KEY = "api_key";

    private final static String PATH_POPULAR = "popular";
    private final static String PATH_TOP_RATED = "top_rated";

    private final static String PATH_TRAILER = "videos";
    private final static String PATH_REVIEW = "reviews";

    // For Trailer
    private final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private final static String YOUTUBE_QUERY_KEY = "v";

    private static Uri.Builder baseUri() {

        return Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_KEY, API_KEY);
    }

    public static Uri popularUri() {
        return baseUri().appendPath(PATH_POPULAR).build();
    }

    public static Uri topRatedUri() {
        return baseUri().appendPath(PATH_TOP_RATED).build();
    }

    public static Uri detailUri(String movieId) {
        return baseUri().appendPath(movieId).build();
    }

    public static Uri reviewUri(String movieId) {
        return detailUri(movieId).buildUpon().appendPath(PATH_REVIEW).build();
    }

    public static Uri imageUri(String imagePath) {

        return Uri.parse(MOVIE_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(imagePath)
                .build();
    }

    public static Uri trailerUri(String movieId) {
        return baseUri().appendPath(movieId)
                .appendPath(PATH_TRAILER)
                .build();
    }

    public static URL buildUrl(Uri movieUri) {
        URL movieUrl = null;
        try {
            movieUrl = new URL(movieUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return movieUrl;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d(NetworkUtils.class.getSimpleName(), url.toString());

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(NetworkUtils.class.getSimpleName(), "getResponseFromHttpUrl: " + e.getLocalizedMessage());
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL getYoutubeUrl(String youtubeKey) {
        Uri youtubeUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_QUERY_KEY, youtubeKey)
                .build();

        URL youtubeUrl = null;
        try {
            youtubeUrl = new URL(youtubeUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return youtubeUrl;
    }
}
