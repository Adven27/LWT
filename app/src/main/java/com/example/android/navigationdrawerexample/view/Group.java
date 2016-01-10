package com.example.android.navigationdrawerexample.view;

import com.example.android.navigationdrawerexample.model.Phrase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by adven on 14.05.14.
 */
public class Group {
    public final List<String> children = new ArrayList<String>();
    private final Phrase phrase;
    private final String sentence;

    public Group(Map.Entry<Phrase, String> string) {
        this.phrase = string.getKey();
        this.sentence = string.getValue();
    }

    public String getSentence() {
        return sentence;
    }

    public Phrase getPhrase() {
        return phrase;
    }
}