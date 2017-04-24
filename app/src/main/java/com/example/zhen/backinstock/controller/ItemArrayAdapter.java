package com.example.zhen.backinstock.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zhen.backinstock.R;
import com.example.zhen.backinstock.model.Item;

import java.util.List;

//import static com.example.zhen.backinstock.R.id.imageView;

/**
 * Created by Zhen on 4/10/2017.
 */

public class ItemArrayAdapter extends ArrayAdapter<Item> {

    private Context context;
    private int layoutResource;
    private List<Item> itemList;

    public ItemArrayAdapter(Context context, int resource, List<Item> itemList) {
        super(context, resource, itemList);
        this.context = context;
        this.layoutResource = resource;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.listview_item, parent, false);

        TextView nameText = (TextView) view.findViewById(R.id.nameText);
        TextView priceText = (TextView) view.findViewById(R.id.priceText);
        TextView statusText = (TextView) view.findViewById(R.id.statusText);

        //ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
        //CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        Item item = itemList.get(position);

        nameText.setText(item.getTitle());
        priceText.setText("$" + item.getPrice());
        statusText.setText(item.isInStock() ? "In Stock" : "Out of Stock");
        statusText.setTextColor(item.isInStock() ? Color.GREEN : Color.RED);
        //itemList.get(position).setChecked((checkBox.isChecked()) ? true : false);

        return view;
    }
}
