package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Utils;

import java.util.ArrayList;
import java.util.List;

import yahoofinance.Stock;

public class DetailActivity extends AppCompatActivity {

    TextView mErrorTextView;
    TextView mPriceTextView;
    TextView mSymbolTextView;
    LineChart mLineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        mSymbolTextView = (TextView) findViewById(R.id.tv_symbol);
        mErrorTextView = (TextView) findViewById(R.id.tv_error);
        mPriceTextView = (TextView) findViewById(R.id.tv_price);

        mLineChart = (LineChart) findViewById(R.id.chart);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null && bundle.containsKey(this.getString(R.string.intent_extra_symbol))){

            // Query stock and insert it's data in the views
            String symbol = bundle.getString(this.getString(R.string.intent_extra_symbol));
            Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null, null);

            if(cursor.moveToNext()){
                insertDataFromCursor(cursor);
            }else{
                showErrorMessage(getString(R.string.error_detail));
            }
        }else{
            showErrorMessage(getString(R.string.error_detail));
        }
    }


    public void showErrorMessage(String message){
        mErrorTextView.setText(message);
        mErrorTextView.setVisibility(View.VISIBLE);
    }


    // inserts data into views
    public void insertDataFromCursor(Cursor cursor){

        String symbol = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
        String absoluteChange = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
        String percentageChange = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
        double price = cursor.getDouble(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        String history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

        mSymbolTextView.setText(symbol);
        mPriceTextView.setText(Utils.formatPrice(price));

        List<Entry> entries = new ArrayList<Entry>();
        String[] histories = history.split("\\r?\\n");

        for (String historyString : histories) {
            String[] historyValues = historyString.split(", ");

            // turn your data into Entry objects
            entries.add(new Entry(Float.parseFloat(historyValues[0]), Float.parseFloat(historyValues[1])));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.label_stock_price)); // add entries to dataset
        LineData lineData = new LineData(dataSet);


        lineData.setValueTextColor(Color.WHITE);

        mLineChart.getAxisLeft().setTextColor(Color.WHITE);
        mLineChart.getLegend().setTextColor(Color.WHITE);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setData(lineData);
        mLineChart.invalidate(); // refresh
        mLineChart.getXAxis().setDrawLabels(false);
        mLineChart.getAxisRight().setDrawLabels(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
