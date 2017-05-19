package com.udacity.stockhawk.widget;

/**
 * Created by phili on 5/19/2017.
 */

public class WidgetItem {


    public String symbol;
    public int price;
    public String change;


    public WidgetItem(String symbol, int price, String change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

}
