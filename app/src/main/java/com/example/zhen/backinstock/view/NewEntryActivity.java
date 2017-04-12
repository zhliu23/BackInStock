package com.example.zhen.backinstock.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.zhen.backinstock.R;

/**
 * Created by Zhen on 4/11/2017.
 */

public class NewEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (height * .5));
    }
}
