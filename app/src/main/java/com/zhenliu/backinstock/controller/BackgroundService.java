package com.zhenliu.backinstock.controller;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.zhenliu.backinstock.R;
import com.zhenliu.backinstock.model.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


/**
 * Created by Zhen on 5/2/2017.
 */

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("Background Network Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("BackgroundService", "Starting update");
        ArrayList<Item> itemList = intent.getParcelableArrayListExtra("ItemList");

        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String msg = "Available now~!";
            int numberInStock = 0;
            for(Item item : itemList) {
                Document doc = Jsoup.connect(item.getUrl()).get();

                Element stockBlock = doc.select("#outOfStock").first();
                boolean inStock = (stockBlock != null) ? false : true;

                if (inStock) {
                    Elements priceBlock = doc.select("#priceblock_ourprice");
                    if (priceBlock.text().equals(""))
                        priceBlock = doc.select("#priceblock_dealprice");
                    if (!priceBlock.text().equals("")) {
                        msg += "\n" + doc.select("#productTitle").first().text();
                        numberInStock++;
                    }
                }
            }

            if(numberInStock > 0) {
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                Notification notification = new Notification.Builder(this)
                        .setContentTitle("Back in Stock:")
                        .setContentText(msg)
                        .setStyle(new Notification.BigTextStyle().bigText(msg))
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[]{0, 1000, 1000, 1000, 1000})
                        .setContentIntent(pIntent)
                        .build();
                notificationManager.notify(0, notification);
            }
        }
        catch(Throwable t) {
            Log.e("BackgroundService", "Items fail to update.");
        }
    }

}
