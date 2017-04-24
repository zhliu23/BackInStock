package com.example.zhen.backinstock.controller;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhen.backinstock.R;
import com.example.zhen.backinstock.model.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addFAB;

    private ListView listView;
    private ItemArrayAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initItemList();

        //init all variables for ListView
        listView = (ListView) findViewById(R.id.listView);
        itemAdapter = new ItemArrayAdapter(this, R.layout.listview_item, itemList);
        listView.setAdapter(itemAdapter);

        //Floating Action Button
        addFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, NewEntryActivity.class));
                AlertDialog.Builder newEntryBuilder = new AlertDialog.Builder(MainActivity.this);
                newEntryBuilder.setTitle("New Entry");

                final EditText urlText = new EditText(MainActivity.this);
                urlText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                urlText.setHint("URL");

                newEntryBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!urlText.getText().toString().matches(""))
                            (new parseURL()).execute(new String[]{urlText.getText().toString()});
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

    private void initItemList() {
        itemList = new ArrayList<>();
        itemList.add(new Item("", "Fire Emblem", 59.99, false));
        itemList.add(new Item("", "Nintendo Switch", 299.99, true));
        itemList.add(new Item("", "Final Fantasy", 59.99, true));

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
                return true;
            case R.id.action_search:
                return true;
//            case R.id.action_settings:
//                return true;
            default:
                return true;
        }
    }

    private class parseURL extends AsyncTask<String, Void, String> {

        Item newItem;
        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc  = Jsoup.connect(strings[0]).get();
                String title = doc.title();
                newItem = new Item("", title, 0.0, false);

            }
            catch(Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            itemAdapter.add(newItem);
            itemAdapter.notifyDataSetChanged();
        }
    }
}
