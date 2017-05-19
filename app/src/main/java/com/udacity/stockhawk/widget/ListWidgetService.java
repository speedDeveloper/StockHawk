package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Utils;
import com.udacity.stockhawk.ui.DetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phili on 5/19/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}


class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor;
    public ListRemoteViewsFactory(Context context, Intent intent) {

        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    public void onCreate() {


        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        Log.d(TAG, "test2");
        mCursor = mContext.getContentResolver().query(Contract.Quote.URI, null, null, null, Contract.Quote.COLUMN_PRICE + " ASC");
        Log.d(TAG, "test2");
        while(mCursor.moveToNext()){
            Log.d(TAG, "test");
            mWidgetItems.add(new WidgetItem(mCursor.getString(Contract.Quote.POSITION_SYMBOL), mCursor.getInt(Contract.Quote.POSITION_PRICE), mCursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE)));
        }



        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {

        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear();
        mCursor.close();
    }


    public int getCount() {
            return mWidgetItems.size();
        }


    public RemoteViews getViewAt(int position) {

        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        rv.setTextViewText(R.id.symbol, mWidgetItems.get(position).symbol);
        rv.setTextViewText(R.id.price, Utils.formatPrice(mWidgetItems.get(position).price));
        rv.setTextViewText(R.id.change, Utils.formatChangePercentage(mWidgetItems.get(position).change));



        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(StockAppWidgetProvider.EXTRA_ITEM, position);
        extras.putString(mContext.getString(R.string.intent_extra_symbol), mWidgetItems.get(position).symbol);
        Intent fillInIntent = new Intent(mContext, DetailActivity.class);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.list_item, fillInIntent);



        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        try {
            System.out.println("Loading view " + position);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        // Return the remote views object.
        return rv;
    }
    public RemoteViews getLoadingView() {

        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }


    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }
}