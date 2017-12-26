package com.thestk.camex.database;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Neba on 16-Jul-17.
 * Class is the movie contract defining the names of table and table columns
 */

public class DataContract {

    //authority
    public static final String CONTENT_AUTHORITY = "com.thestk.camex.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String DATA_PATH = "data";
    public static final String DATA_PATH_BY_DATA_ID = "data_by_id";

    public static final class DataEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the movie table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(DATA_PATH)
                .build();

        public static final Uri GET_BY_DATA_ID_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(DATA_PATH_BY_DATA_ID)
                .build();

        //table name
        public static final String TABLE_NAME = "tbl_data";

        //define table columns matching with Back end data Database
        public static final String COLUMN_DATA_ID = "music_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST_ACTORS = "artist_actors";
        public static final String COLUMN_MUSIC_GENRE = "music_genre";
        public static final String COLUMN_YOUTUBE_ID = "youtube_id";
        public static final String COLUMN_VIEWS = "views";
        public static final String COLUMN_LIKES = "likes";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_ASPECT_RATIO = "aspect_ratio";


        public static Uri buildUriWithDataId(Integer data_id) {
            return GET_BY_DATA_ID_URI.buildUpon()
                    .appendPath(Integer.toString(data_id))
                    .build();
        }

        public static Uri buildUriWithMSqlId(Integer id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

    }
}
