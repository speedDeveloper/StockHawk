package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.Utils;
import com.udacity.stockhawk.mock.MockUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import static android.content.ContentValues.TAG;

public final class QuoteSyncJob {

    private static final String TAG = QuoteSyncJob.class.getSimpleName();
    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {
    }

    static void getQuotes(final Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {

            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            Timber.d(stockCopy.toString());

            if (stockArray.length == 0) {
                return;
            }
            for(String s : stockArray) {
                Log.d(TAG, s);
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();
            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                final String symbol = iterator.next();

                if(!Utils.isInAlphabet(symbol)) {
                    PrefUtils.removeStock(context, symbol);
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_stock_invalid, symbol), Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                }
                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();
                if(!stock.isValid()) {

                    PrefUtils.removeStock(context, symbol);
                    // create a handler to post messages to the main thread
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_stock_invalid, symbol), Toast.LENGTH_LONG).show();
                        }
                    });


                    break;
                }


                float price = quote.getPrice().floatValue();
                float change = quote.getChange().floatValue();
                float percentChange = quote.getChangeInPercent().floatValue();






                // WARNING! Don't request historical data for a stock that doesn't exist!
                // The request will hang forever X_x


// My own bug fix, replaced by bug fix from Udacity
//                List<HistoricalQuote> history = new ArrayList<>(); //stock.getHistory(from, to, Interval.WEEKLY);
//
//                // Adding Fake Data
//                // TODO load data from web
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 0);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(24), new BigDecimal(20), 5000l));
//                calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 1);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(22), new BigDecimal(20), 5000l));
//                calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 2);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(20), new BigDecimal(20), 5000l));
//                calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 3);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(27), new BigDecimal(20), 5000l));
//                calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 4);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(19), new BigDecimal(20), 5000l));
//                calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 5);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(14), new BigDecimal(20), 5000l));
//                calendar = Calendar.getInstance();
//                calendar.set(Calendar.DATE, 6);
//                history.add(new HistoricalQuote(symbol, calendar, new BigDecimal(20), new BigDecimal(10), new BigDecimal(25), new BigDecimal(price), new BigDecimal(20), 5000l)); // actual price
//                // we use the correct price here so that the Graph and TextView use the same price

                // Note for reviewer
                // Due to the problems with Yahoo API we have commented the line above
                // and included this one to fetch the history from MockUtils
                // This should be enough as to develop and review while the API is down
                List<HistoricalQuote> history = MockUtils.getHistory();


                StringBuilder historyBuilder = new StringBuilder();

                for (HistoricalQuote it : history) {
                    historyBuilder.append(it.getDate().getTimeInMillis());
                    historyBuilder.append(", ");
                    historyBuilder.append(it.getClose());
                    historyBuilder.append("\n");
                }

                ContentValues quoteCV = new ContentValues();
                quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);


                quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

                quoteCVs.add(quoteCV);

            }

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

            Intent dataUpdatedIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }


}
