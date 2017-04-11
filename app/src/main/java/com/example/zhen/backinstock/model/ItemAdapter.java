package com.example.zhen.backinstock.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhen.backinstock.R;

import java.util.List;

/**
 * Created by Zhen on 4/10/2017.
 */

public class ItemAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList;
    private Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        super(context, R.layout.row, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);

        TextView nameText = (TextView) convertView.findViewById(R.id.itemNameText);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        Item item = itemList.get(position);

        nameText.setText(item.getTitle());
        imageView.setImageResource((item.isInStock()) ? R.drawable.ic_dot_green : R.drawable.ic_dot_red);
        itemList.get(position).setChecked((checkBox.isChecked()) ? true : false);

        return convertView;
    }
}
