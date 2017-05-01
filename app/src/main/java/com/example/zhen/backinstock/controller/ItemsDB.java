package com.example.zhen.backinstock.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zhen.backinstock.model.Item;

import java.util.ArrayList;

/**
 * Created by Zhen on 4/30/2017.
 */

public class ItemsDB extends SQLiteOpenHelper {

    //Database version and name
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ItemInfo.db";

    //Table Name
    public static final String TABLE_ITEMS = "Items";

    //Table Column Names
    public static final String KEY_URL = "url";

    public ItemsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_URL + " TEXT"
                + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    /**
     * Adds a new item into the database
     * @param item - an item object that stores the url, name, price, and stock status
     */
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_URL, item.getUrl());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    /**
     * Returns an arraylist containing all saved URL
     * @return itemList
     */
    public ArrayList<Item> getAllItems() {
        ArrayList<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            Item item;
            do {
                item = new Item();
                item.setUrl(cursor.getString(0));
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return itemList;
    }

    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_URL + " = ?", new String[] {item.getUrl()});
        db.close();
    }

    public void deleteAllItem() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_ITEMS);
        db.close();
    }

    public int getItemsCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor.getCount();
    }
}
