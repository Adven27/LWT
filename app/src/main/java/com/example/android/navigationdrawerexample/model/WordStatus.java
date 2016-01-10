package com.example.android.navigationdrawerexample.model;

import android.graphics.Color;

/**
 * Created by adven on 23.03.14.
 */
public enum WordStatus {
    UNKNOWN(Color.CYAN),
    LEARN(Color.YELLOW),
    KNOWN(Color.TRANSPARENT);
    private final int color;
    //private final int radioButtonId;

    WordStatus(int color) {
        //  this.radioButtonId = radioButtonId;
        this.color = color;
    }

    public static WordStatus getByName(String name) {
        for (WordStatus ws : values()) {
            if (ws.name().equals(name)) {
                return ws;
            }
        }
        return null;
    }

    public int getColor() {
        return color;
    }

    public String getCode() {
        return this.name();
    }
}
