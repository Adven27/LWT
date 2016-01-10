package com.example.android.navigationdrawerexample.util;

import android.content.Context;

/**
 * Created by adven on 16.04.14.
 */
public class DemoJavaScriptInterface {
    Context mContext;

    public DemoJavaScriptInterface(Context c) {
        mContext = c;
    }

    /**
     * used from javascript
     *
     * @param newCount count of content containers on page
     */
    @SuppressWarnings("unused")
    public void initContents(int newCount) {
    }
}