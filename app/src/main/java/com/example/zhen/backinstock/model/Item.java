package com.example.zhen.backinstock.model;

/**
 * Created by Zhen on 4/11/2017.
 */

public class Item {

    private String url;
    private String name;
    private double price;
    private boolean stock;
    private boolean selected;

    public Item(){}
    public Item(String url, String name, double price, boolean stock) {
        this.url = url;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.selected = false;
    }

    //Getters
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public double getPrice() { return price; }
    public boolean getStock() { return stock; }
    public boolean getSelected() { return selected; }
    //Setters
    public void setUrl(String url) { this.url = url; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(boolean inStock) { this.stock = inStock; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
