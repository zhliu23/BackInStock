package com.zhenliu.backinstock.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhenliu.backinstock.model.Item;

import java.util.ArrayList;

/**
 * Created by Zhen on 5/6/2017.
 */

public class NetworkScheduler extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BackgroundService.class);
        ArrayList<Item> itemList = intent.getParcelableArrayListExtra("ItemList");

        i.putParcelableArrayListExtra("ItemList", itemList);
        context.startService(i);
    }

}
