package com.example.zhen.backinstock.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhen.backinstock.R;

import java.util.List;

//import static com.example.zhen.backinstock.R.id.imageView;

/**
 * Created by Zhen on 4/10/2017.
 */

public class ItemAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        super(context, R.layout.fragment_row, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.fragment_row, parent, false);

        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView priceText = (TextView) convertView.findViewById(R.id.priceText);
        TextView statusText = (TextView) convertView.findViewById(R.id.statusText);

        //ImageView imageView = (ImageView) convertView.findViewById(R.id.itemImage);
        //CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        Item item = itemList.get(position);

        nameText.setText(item.getTitle());
        priceText.setText("$" + item.getPrice());
        statusText.setText(item.isInStock() ? "In Stock" : "Out of Stock");
        statusText.setTextColor(item.isInStock() ? Color.GREEN : Color.RED);
        //itemList.get(position).setChecked((checkBox.isChecked()) ? true : false);

        return convertView;
    }
}
