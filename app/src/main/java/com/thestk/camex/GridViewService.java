package com.thestk.camex;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.thestk.camex.database.DataContract;
import com.thestk.camex.models.DataModel;

import java.util.ArrayList;

/**
 * Created by Neba on 16-Oct-17.
 */

public class GridViewService extends RemoteViewsService {

    private final String LOG_TAG = "GridViewService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String MUSIC_LIST = "music_list";
    private final String LOG_TAG = "GridViewService";
    private final String SELECTED_MUSIC = "selected_music";
    Context mContext;
    Cursor mCursor;
    private ArrayList<DataModel> musicList;

    public GridRemoteViewsFactory(Context context) {

        this.mContext = context;
        musicList = new ArrayList<>();
    }

    @Override
    public void onCreate() {


    }


    @Override
    public void onDataSetChanged() {
        // Get all favourite recipes
        //if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(DataContract.DataEntry.CONTENT_URI, null, null, null, null);


        if (mCursor != null && mCursor.moveToFirst()) {
            for (int i = 0; i < mCursor.getCount(); i++) {
                DataModel dataModel = new DataModel();
                dataModel.setId(mCursor.getInt(mCursor.getColumnIndex(DataContract.DataEntry._ID)));
                dataModel.setData_id(mCursor.getInt(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_DATA_ID)));
                dataModel.setTitle(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_TITLE)));
                dataModel.setArtist_actors(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_ARTIST_ACTORS)));
                dataModel.setYoutube_id(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_YOUTUBE_ID)));
                dataModel.setMusic_genre(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_MUSIC_GENRE)));
                dataModel.setImage(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_IMAGE)));
                dataModel.setType(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_TYPE)));
                dataModel.setAspect_ratio(mCursor.getFloat(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_ASPECT_RATIO)));

                musicList.add(dataModel);
                mCursor.moveToNext();
            }
            Log.e(LOG_TAG, "" + musicList.size());
            DatabaseUtils.dumpCursor(mCursor);

        }

    }

    @Override
    public void onDestroy() {
        mCursor.close();

    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            Log.e("no data", "no data");

            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        DataModel dataModel = new DataModel();
        dataModel.setId(mCursor.getInt(mCursor.getColumnIndex(DataContract.DataEntry._ID)));
        dataModel.setData_id(mCursor.getInt(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_DATA_ID)));
        dataModel.setTitle(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_TITLE)));
        dataModel.setArtist_actors(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_ARTIST_ACTORS)));
        dataModel.setYoutube_id(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_YOUTUBE_ID)));
        dataModel.setMusic_genre(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_MUSIC_GENRE)));
        dataModel.setImage(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_IMAGE)));
        dataModel.setType(mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_TYPE)));
        dataModel.setAspect_ratio(mCursor.getFloat(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_ASPECT_RATIO)));


        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_gridview_helper);
        views.setTextViewText(R.id.music_title, dataModel.getTitle() + " \n" + dataModel.getArtist_actors());
        views.setTextViewText(R.id.tv_type, dataModel.getType());

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putParcelableArrayList(MUSIC_LIST, musicList);
        extras.putInt(SELECTED_MUSIC, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_relativell, fillInIntent);

        return views;

    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

