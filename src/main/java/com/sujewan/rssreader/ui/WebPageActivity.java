package com.sujewan.rssreader.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sujewan.rssreader.R;

public class WebPageActivity extends Activity
{
    WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent in = getIntent();
        String pageUrl = in.getStringExtra("pageUrl");

        webview = (WebView) findViewById(R.id.webpage);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.loadUrl(pageUrl);

        webview.setWebViewClient(new DisPlayWebPageActivityClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack())
        {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class DisPlayWebPageActivityClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}
