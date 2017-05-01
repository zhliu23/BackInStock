package com.example.zhen.backinstock.controller;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import com.example.zhen.backinstock.R;
import com.example.zhen.backinstock.model.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addFAB;

    private ListView listView;
    private ItemArrayAdapter itemAdapter;
    private List<Item> itemList;
    private ItemsDB itemsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initItemAdapter();

        //Floating Action Button
        addFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder newEntryBuilder = new AlertDialog.Builder(MainActivity.this);
                newEntryBuilder.setTitle("New Entry");

                final EditText urlText = new EditText(MainActivity.this);
                urlText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                urlText.setHint("URL");

                newEntryBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://www.amazon.com/dp/B011K380S4/ref=wl_it_dp_o_pC_nS_ttl?_encoding=UTF8&colid=477BR7CSIDKM&coliid=I2AHZ95751R3YB";
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
                newEntryBuilder.setView(urlText);
                newEntryBuilder.show();
            }
        });
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
            case R.id.action_delete:
                itemAdapter.setVisibility_Checkbox(false);
                itemAdapter.notifyDataSetChanged();
                addFAB.setVisibility(View.GONE);
                return true;
            case R.id.action_refresh:
                return true;
//            case R.id.action_settings:
//                return true;
            default:
                return true;
        }
    }

    /**
     * Initialize itemList by populating it with data from local database
     */
    public void initItemAdapter() {
        itemList = new ArrayList<>();
        itemsDB = new ItemsDB(this);

        for(Item item : itemsDB.getAllItems())
            (new parseList()).execute(new Item[] { item });

        //init all variables for ListView
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                listView = (ListView) findViewById(R.id.listView);
//                itemAdapter = new ItemArrayAdapter(MainActivity.this, R.layout.listview_item, itemList);
//                listView.setAdapter(itemAdapter);
//            }
//        }, itemList.size() * 2000);

        listView = (ListView) findViewById(R.id.listView);
        itemAdapter = new ItemArrayAdapter(this, R.layout.listview_item, itemList);
        listView.setAdapter(itemAdapter);
    }

    /**
     * This method is call when a new item is added, this background thread will parse the
     * html source.
     */
    private class parseURL extends AsyncTask<String, Void, String> {

        //Jsoup API
        private Document doc;
        private Element stockBlock;
        private Elements priceBlock;

        private Item newItem;
        private boolean inStock;
        private String name;
        private double price;

        @Override
        protected String doInBackground(String... strings) {
            try {
                doc  = Jsoup.connect(strings[0]).get();
                name = doc.title().substring(12);

                stockBlock = doc.select("#outOfStock").first();
                inStock = (stockBlock != null) ? false : true;

                if(inStock) {
                    priceBlock = doc.select("#priceblock_ourprice");
                    String[] text = priceBlock.text().split(" ");
                    if(text.length == 3)
                        price = Double.parseDouble(text[1] + "." + text[2]);
                    else
                        price = Double.parseDouble(text[0].substring(1));
                } else { price = 0.0; }

                newItem = new Item(strings[0], name, price, inStock);
                return "Pass";
            }
            catch(Throwable t) {
                Log.e("Background", "Information didn't fetch.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("Pass")) {
                itemAdapter.add(newItem);
                itemsDB.addItem(newItem);
                itemAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * This method is call at App startup/refresh to update the information of each item.
     */
    private class parseList extends AsyncTask<Item, Void, String> {

        //Jsoup API
        private Document doc;
        private Element stockBlock;
        private Elements priceBlock;

        private Item newItem;
        private boolean inStock;
        private String name;
        private double price;

        @Override
        protected String doInBackground(Item... items) {
            try {
                newItem = new Item();
                newItem.setUrl(items[0].getUrl());

                doc  = Jsoup.connect(newItem.getUrl()).get();
                name = doc.title().substring(12);
                newItem.setName(name);

                stockBlock = doc.select("#outOfStock").first();
                inStock = (stockBlock != null) ? false : true;
                newItem.setStock(inStock);

                if(inStock) {
                    priceBlock = doc.select("#priceblock_ourprice");
                    String[] text = priceBlock.text().split(" ");
                    if(text.length == 3)
                        price = Double.parseDouble(text[1] + "." + text[2]);
                    else
                        price = Double.parseDouble(text[0].substring(1));
                } else { price = 0.0; }

                newItem.setPrice(price);

                return "Pass";
            }
            catch(Throwable t) {
                Log.e("Background", "Information didn't fetch.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Pass"))
            {
                itemAdapter.add(newItem);
                itemAdapter.notifyDataSetChanged();
            }
        }
    }
}
