package com.thestk.camex;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.thestk.camex.database.DataContract;

/**
 * Created by Neba on 16-Oct-17.
 */

public class AppWidgetService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    //intent action
    public static String WIDGET_INTENT_SERVICE = "com.clasence.shu.mywidget_intent_service";
    public static String WIDGET_ID = "widget_id";

    public AppWidgetService() {
        super("AppWidgetService");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("service", "service calle_");
        if (intent.getAction().equalsIgnoreCase(WIDGET_INTENT_SERVICE)) {
            Log.e("service", "service called + intent good");


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                    WidgetManager.class));

            for (int appWidgetId : appWidgetIds) {

                Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

                // Get current width to decide on single plant vs garden grid view
                int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
                RemoteViews rv;

                Cursor data = getContentResolver().query(DataContract.DataEntry.CONTENT_URI, null, null, null, null);
                if (data == null) {

                    rv = WidgetManager.getRemoteViewNoFavourites(getApplicationContext());
                } else if (!data.moveToFirst()) {
                    rv = WidgetManager.getRemoteViewNoFavourites(getApplicationContext());
                    data.close();
                } else {
                    //set textview if no space
                    if (width < 200 && height < 180) {
                        rv = WidgetManager.getRemoteViewFavourites(getApplicationContext(), data.getCount());
                        data.close();
                    } else {
                        //get gridview
                        rv = WidgetManager.getRemoteGridView(getApplicationContext());
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_grid_view);
                        data.close();
                    }

                }


                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, rv);
            }
        }
    }
}
