package com.udacity.stockhawk.data;

import android.database.Cursor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by phili on 5/19/2017.
 */


public class Utils {

    public static String formatPrice(double price){
        NumberFormat nf = new DecimalFormat("#.##");
        return "$" + nf.format(price);
    }

    public static String formatChangePercentage(String change) {
        double d = Double.parseDouble(change);
        if(d > 0)
            return "+" + change + "%";
        return change + "%";
    }
    public static boolean isInAlphabet(String name) {
        return name.matches("[a-zA-Z]+");
    }
}
