package com.example.zhen.backinstock;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhen.backinstock.model.ItemAdapter;
import com.example.zhen.backinstock.model.Item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addFAB;

    private ListView listView;
    private ItemAdapter adapter;

    private List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init all variables for ListView
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ItemAdapter(this, itemList);
        listView.setAdapter(adapter);

        //Floating Action Button
        addFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hello there!", Toast.LENGTH_LONG).show();
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
            case R.id.action_refresh:
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
            default:
                return true;
        }
    }
}
