package com.example.android.navigationdrawerexample.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.example.android.navigationdrawerexample.model.Phrase;

/**
 * Created by adven on 10.05.14.
 */
public final class WordSpan extends ClickableSpan {
    private final Phrase phrase;
    private TextTabFragment context;

    public WordSpan(TextTabFragment context, Phrase phrase) {
        this.context = context;
        this.phrase = phrase;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        TextPaint textpaint = ds;
        textpaint.bgColor = phrase.getStatus().getColor();

        //Remove default underline associated with spans
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        context.onWordSpanClick(this);
    }
}