package com.example.android.navigationdrawerexample.util;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by adven on 16.04.14.
 */
public class MyWebViewClient extends WebViewClient {

    private static final String TAG = "MyWebViewClient";
    ProgressDialog pd;

    @Override
    public void onLoadResource(WebView view, String url) {
        Log.i(TAG, "onLoadResource" + url);
        super.onLoadResource(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(TAG, "shouldOverrideUrlLoading");
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i(TAG, "onPageStarted: Loading URL: " + url);
        pd = new ProgressDialog(view.getContext());
        pd.setTitle("Title");
        pd.setMessage("Message");
        //pd.show();
        view.setVisibility(View.INVISIBLE);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "onPageFinished");
        super.onPageFinished(view, url);
        view.loadUrl(JSHelper.TEST);
        if (pd.isShowing()) {
            pd.dismiss();
        }
        view.setVisibility(View.VISIBLE);
        Log.i(TAG, "Injecting JavaScript to webview.");
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.i(TAG, "onReceivedError: view = [" + view + "], errorCode = [" + errorCode + "], description = [" + description + "], failingUrl = [" + failingUrl + "]");
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}