package com.example.android.navigationdrawerexample.util;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by adven on 16.04.14.
 */
public class MyWebChromeClient extends WebChromeClient {

    private static final String TAG = "MyWebChromeClient";

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        //Log.i(TAG, message);
        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
        //initTextView(dbAssetHelper, message);
        //switchView();
        result.confirm();
        return true;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.i(TAG, "consoleMessage = " + consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }
}