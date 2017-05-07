package com.example.zhen.backinstock.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhen.backinstock.R;
import com.example.zhen.backinstock.model.Item;

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

    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtons();
        initItemAdapter();
        //initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //IntentFilter filter = new IntentFilter(BackgroundService.ACTION);
        //LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        startService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_settings:
                return true;

            default:
                return true;
        }
    }

    /**
     * Initialize the buttons
     */
    public void initButtons() {
        //Floating Action Button
        addFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder newEntryBuilder = new AlertDialog.Builder(MainActivity.this);
                newEntryBuilder.setTitle("New Entry");

                final EditText urlEditText = new EditText(MainActivity.this);
                urlEditText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                urlEditText.setHint("Amazon URL");

                newEntryBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = urlEditText.getText().toString();
                        if(!url.matches("") && url.startsWith("https://www.amazon.com/"))
                            (new parseURL()).execute(new String[]{ url });
                        else
                            dialog.cancel();
                    }
                });
                newEntryBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newEntryBuilder.setView(urlEditText);
                newEntryBuilder.show();
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

        if(!itemList.isEmpty())
            new updateItemInfo().execute(itemList);
    }

    /**
     * Initialize the BroadcastReceiver
     */
    public void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
                if (resultCode == RESULT_OK) {
                    itemList = intent.getParcelableArrayListExtra("ItemList");
                    for(Item item : itemList)
                        itemsDB.updateItem(item);
                    Log.e("Receiver", "Finish updating.");
                }
            }
        };
    }

    /**
     * Initialize Intent, PendingIntent and AlarmManager to start up BackgroundService
     */
    public void startService() {
        Intent intent = new Intent(getApplicationContext(), NetworkScheduler.class);
        intent.putParcelableArrayListExtra("ItemList", itemList);

        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NetworkScheduler.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
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
                    }
                    else {
                        price = priceBlock.text().replaceAll("\\s+", "");
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
                    Toast.makeText(MainActivity.this, "Duplicate item.", Toast.LENGTH_SHORT).show();
                itemAdapter.notifyDataSetChanged();
            }
            else
                Toast.makeText(MainActivity.this, "Invalid link.", Toast.LENGTH_SHORT).show();
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
                            price = priceBlock.text().replaceAll("\\s+", "");
                            price = new StringBuilder(price).insert(price.length() - 2, ".").toString();
                        }
                    } else
                        price = "Available from other sellers.";
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
            Log.e("UpdateList", "finished");
            if(s.equals("Pass"))
                itemAdapter.notifyDataSetChanged();
        }
    }
}
