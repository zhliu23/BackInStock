package com.example.zhen.backinstock.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zhen on 4/11/2017.
 */

public class Item implements Parcelable {

    private String url;
    private String name;
    private String price;
    private boolean stock;

    public Item(){}

    public Item(String url, String name, String price, boolean stock) {
        this.url = url;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Item(Parcel in) {
        this.url = in.readString();
        this.name = in.readString();
        this.price = in.readString();
    }

    //Getters
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public String getPrice() { return price; }
    public boolean getStock() { return stock; }
    //Setters
    public void setUrl(String url) { this.url = url; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setStock(boolean inStock) { this.stock = inStock; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(price);
    }

    static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {

        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
