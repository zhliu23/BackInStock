package com.zhenliu.backinstock.controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.zhenliu.backinstock.R;
import com.zhenliu.backinstock.model.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addFAB;

    private ListView listView;
    private ItemArrayAdapter itemAdapter;
    private ArrayList<Item> itemList;
    private ItemsDB itemsDB;

    private ProgressDialog progressDialog;
    private boolean browserRunning = false; //to ensure service doesn't start when internal browser activity starts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();
        initItemAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!itemList.isEmpty() && !browserRunning)
            runService(true);
        else if(itemList.isEmpty() && !browserRunning)
            runService(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_refresh:
                if(!itemList.isEmpty())
                    new updateItemInfo().execute(itemList);
                break;

            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage("1. Tab on item entry to open item in browser\n" +
                                    "2. Hold on item entry to delete item.\n\n" +
                                    "Currently supports:\nAmazon")
                        .show();
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                String url = data.getStringExtra("URL");
                new parseURL().execute(new String[] {url});
                browserRunning = false;
            }
        }
    }

    /**
     * Initialize the floating action button
     */
    public void initButtons() {
        addFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                browserRunning = true;
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * Initialize itemList by populating it with data from local database
     */
    public void initItemAdapter() {
        itemsDB = new ItemsDB(this);
        itemList = itemsDB.getAllItems();

        //ListView
        listView = (ListView) findViewById(R.id.listView);
        itemAdapter = new ItemArrayAdapter(this, R.layout.listview_item, itemList, itemsDB);
        listView.setAdapter(itemAdapter);

        if(!itemList.isEmpty()) {
            progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            new updateItemInfo().execute(itemList);
        }
    }

    /**
     * Initialize Intent, PendingIntent and AlarmManager to start up BackgroundService
     */
    public void runService(boolean start) {
        Intent intent = new Intent(this, NetworkScheduler.class);
        intent.putParcelableArrayListExtra("ItemList", itemList);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, NetworkScheduler.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if(start)
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
        else
            alarm.cancel(pIntent);
    }

    /**
     * Call when a new item is added, this background thread will parse the
     * html source.
     */
    private class parseURL extends AsyncTask<String, Void, String> {

        //Jsoup API
        private Document doc;
        private Element stockBlock;
        private Elements priceBlock;

        private Item newItem;
        private String url, name, price;
        private boolean inStock;


        @Override
        protected String doInBackground(String... strings) {
            try {
                url = strings[0];
                doc  = Jsoup.connect(url).get();
                name = doc.select("#productTitle").first().text();

                stockBlock = doc.select("#outOfStock").first();
                inStock = (stockBlock != null) ? false : true;

                if(inStock) {
                    priceBlock = doc.select("#priceblock_ourprice");
                    if(priceBlock.text().equals(""))
                        priceBlock = doc.select("#priceblock_dealprice");
                    if(priceBlock.text().equals("")) {
                        price = "Available from other sellers.";
                        inStock = false;
                    } else {
                        price = priceBlock.text().replaceAll("\\s+|\\.", "");
                        price = new StringBuilder(price).insert(price.length() - 2, ".").toString();
                    }
                } else
                    price = "$0.00";

                newItem = new Item(url, name, price, inStock);
                return "Pass";
            }
            catch(Throwable t) {
                return "Fail";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("Pass")) {
                if(itemsDB.addItem(newItem))
                    itemAdapter.add(newItem);
                else
                    Toast.makeText(MainActivity.this, "Duplicate Item", Toast.LENGTH_SHORT).show();
                itemAdapter.notifyDataSetChanged();
            }
            else
                Toast.makeText(MainActivity.this, "This isn't a product", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Call at app startup/refresh to update the information of each item.
     */
    private class updateItemInfo extends AsyncTask<ArrayList<Item>, Void, String> {

        //Jsoup API
        private Document doc;
        private Element stockBlock;
        private Elements priceBlock;

        private ArrayList<Item> list;
        private String price;
        private boolean inStock;

        @Override
        protected String doInBackground(ArrayList<Item>... items) {
            try {
                list = items[0];
                for(Item item : list) {
                    doc = Jsoup.connect(item.getUrl()).get();
                    item.setName(doc.select("#productTitle").first().text());

                    stockBlock = doc.select("#outOfStock").first();
                    inStock = (stockBlock != null) ? false : true;
                    item.setStock(inStock);

                    if (inStock) {
                        priceBlock = doc.select("#priceblock_ourprice");
                        if (priceBlock.text().equals(""))
                            priceBlock = doc.select("#priceblock_dealprice");
                        if(priceBlock.text().equals("")) {
                            price = "Available from other sellers.";
                            item.setStock(false);
                        }
                        else {
                            price = priceBlock.text().replaceAll("\\s+|\\.", "");
                            price = new StringBuilder(price).insert(price.length() - 2, ".").toString();
                        }
                    } else
                         price = "$0.00";
                    item.setPrice(price);
                }
                return "Pass";
            }
            catch(Throwable t) {
                return "Fail";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("Pass"))
                itemAdapter.notifyDataSetChanged();
        }
    }
}
