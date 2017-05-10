package com.example.zhen.backinstock.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhen.backinstock.R;
import com.example.zhen.backinstock.model.Item;

import java.util.List;

/**
 * Created by Zhen on 4/10/2017.
 */

public class ItemArrayAdapter extends ArrayAdapter<Item> {

    private Context context;
    private int layoutResource;
    private List<Item> itemList;
    private ItemsDB itemsDB;

    public ItemArrayAdapter(Context context, int resource, List<Item> itemList, ItemsDB itemsDB) {
        super(context, resource, itemList);
        this.context = context;
        this.layoutResource = resource;
        this.itemList = itemList;
        this.itemsDB = itemsDB;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.listview_item, parent, false);

        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemList.get(position).getUrl()));
                context.startActivity(browserIntent);
            }
        });

        view.setLongClickable(true);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context);
                confirmDialog.setTitle("Confirm Deletion");

                final TextView msgTextView = new TextView(context);
                msgTextView.setText("Are you sure?");
                msgTextView.setGravity(Gravity.CENTER);
                msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


                confirmDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemsDB.deleteItem(itemList.get(position));
                        itemList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                confirmDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                confirmDialog.setView(msgTextView);
                confirmDialog.show();
                return false;
            }
        });

        TextView nameText = (TextView) view.findViewById(R.id.nameText);
        TextView priceText = (TextView) view.findViewById(R.id.priceText);
        TextView statusText = (TextView) view.findViewById(R.id.statusText);

        Item item = itemList.get(position);

        nameText.setText(item.getName());
        priceText.setText(item.getPrice());
        statusText.setText(item.getStock() ? "In Stock" : "Out of Stock");
        statusText.setTextColor(item.getStock() ? Color.GREEN : Color.RED);

        return view;
    }

}
