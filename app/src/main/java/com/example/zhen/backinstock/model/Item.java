package com.example.zhen.backinstock.model;

/**
 * Created by Zhen on 4/11/2017.
 */

public class Item {

    private String url;
    private String title;
    private double price;
    private boolean inStock;
    private boolean checked;

    public Item(String url, String title, double price, boolean inStock) {
        this.url = url;
        this.title = title;
        this.price = price;
        this.inStock = inStock;
        this.checked = false;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public boolean isInStock() { return inStock; }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
