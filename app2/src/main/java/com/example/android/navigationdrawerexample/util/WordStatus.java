package com.example.android.navigationdrawerexample.util;

import android.graphics.Color;

/**
 * Created by adven on 23.03.14.
 */
public enum WordStatus {
    UNKNOWN(Color.BLUE),
    LEARN(Color.YELLOW),
    KNOWN(Color.TRANSPARENT);
    private final int color;

    WordStatus(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
