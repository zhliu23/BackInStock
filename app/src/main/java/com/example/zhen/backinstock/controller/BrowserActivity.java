package com.example.zhen.backinstock.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.zhen.backinstock.R;

/**
 * Created by Zhen on 5/7/2017.
 */

public class BrowserActivity extends Activity {

    private WebView browser;
    private BottomNavigationView bottomBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        browser = (WebView) findViewById(R.id.internalBrowser);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl("https://www.amazon.com/");

        bottomBar = (BottomNavigationView) findViewById(R.id.bottomBar);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()) {

                    case R.id.action_back:
                        if(browser.canGoBack())
                            browser.goBack();
                        break;

                    case R.id.action_forward:
                        if(browser.canGoForward())
                            browser.goForward();
                        break;

                    case R.id.action_add:
                        String url = browser.getUrl();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("URL", url);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        if(browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
