package com.thestk.camex;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetManager extends AppWidgetProvider {


    /**
     * get remoteview for no favourites available
     *
     * @param context {@link Context}
     * @return
     */
    public static RemoteViews getRemoteViewNoFavourites(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view_with_textview);
        Intent launchIntent = new Intent(context, MusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_rll, pendingIntent);
        return views;
    }

    /**
     * get remote view for some favourites available, but no space to show them
     *
     * @param context
     * @return
     */
    public static RemoteViews getRemoteViewFavourites(Context context, int count) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view_with_textview);

        Intent launchIntent = new Intent(context, FavouriteMusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        views.setTextViewText(R.id.tv_text, count + " " + context.getString(R.string.favourite));
        views.setOnClickPendingIntent(R.id.appwidget_rll, pendingIntent);
        return views;
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the GridView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the GridView mode widget
     */
    public static RemoteViews getRemoteGridView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favourite_gridview);
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, GridViewService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        // Set the RecipeDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MusicDetailsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        // Handle empty favourites
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent my_intent = new Intent(context, AppWidgetService.class);
        my_intent.setAction(AppWidgetService.WIDGET_INTENT_SERVICE);
        context.startService(my_intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Intent my_intent = new Intent(context, AppWidgetService.class);
        my_intent.setAction(AppWidgetService.WIDGET_INTENT_SERVICE);
        my_intent.putExtra(AppWidgetService.WIDGET_ID, appWidgetId);
        context.startService(my_intent);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetService.WIDGET_INTENT_SERVICE.equals(intent.getAction())) {

            Intent my_intent = new Intent(context, AppWidgetService.class);
            my_intent.setAction(AppWidgetService.WIDGET_INTENT_SERVICE);
            context.startService(my_intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

