package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by phili on 5/19/2017.
 */

public class StockAppWidgetProvider extends AppWidgetProvider {


    public static final String EXTRA_ITEM = "com.udacity.stockhawk.listwidget.EXTRA_ITEM";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        String strAction = intent.getAction();
        Log.d(TAG, "receiving event");
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(strAction)) {


            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);


            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                    StockAppWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(ids,
                    R.id.lv_stocks);
            Log.d(TAG, "receiving event2");
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {


            // Set up the intent that starts the StackViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListWidgetService.class);


            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));


            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);

            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects
            // to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            rv.setRemoteAdapter(appWidgetIds[i], R.id.lv_stocks, intent);


            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews
            // object above.
            rv.setEmptyView(R.id.lv_stocks, R.id.empty_view);


            // Add click event
            Intent detailIntent = new Intent(context, DetailActivity.class);
            detailIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            PendingIntent detailPendingIntent = PendingIntent.getActivity(context, 0, detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.lv_stocks, detailPendingIntent);





            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }




}
