package com.example.android.movieholic.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static final String DATA_MOVIE = "movie";
    public static final String DATA_TRAILER = "trailer";

    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";

    private static final String KEY_UTUBEKEY = "key";
    private static final String KEY_NAME = "name";

    public static String[] toStringArray(JSONArray jsonArray, String dataType) throws JSONException {
        String[] datas = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject result = jsonArray.getJSONObject(i);

            StringBuilder builder = new StringBuilder();

            if (dataType.equals(DATA_MOVIE)) {
                builder.append(String.valueOf(result.getInt(KEY_ID)))
                        .append("|")
                        .append(String.valueOf(result.getInt(KEY_VOTE_AVERAGE)))
                        .append("|")
                        .append(result.getString(KEY_TITLE))
                        .append("|")
                        .append(result.getString(KEY_POSTER_PATH))
                        .append("|")
                        .append(result.getString(KEY_OVERVIEW))
                        .append("|")
                        .append(result.getString(KEY_RELEASE_DATE));
            } else if (dataType.equals(DATA_TRAILER)) {
                builder.append(result.getString(KEY_UTUBEKEY))
                        .append("|")
                        .append(result.getString(KEY_NAME));
            }

            datas[i] = builder.toString();
        }

        return datas;
    }
}
