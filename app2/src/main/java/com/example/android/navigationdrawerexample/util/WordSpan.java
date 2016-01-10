package com.example.android.navigationdrawerexample.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class WordSpan extends ClickableSpan {

    final private String word;
    private WordStatus status;
    private TextPaint textpaint;
    private int id;

    public WordSpan(int anID, String word, WordStatus status) {
        id = anID;
        this.word = word;
        this.status = status;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        textpaint = ds;
        textpaint.bgColor = status.getColor();
        textpaint.setARGB(255, 255, 255, 255);

        //Remove default underline associated with spans
        ds.setUnderlineText(false);
    }

    /**
     * Смена текущего статуса слова: "unknown" в "known" и т.п.
     *
     * @param widget - TODO: для инвалидации чего-то там, чтобы чтото там перерисовалось?
     * @param status - новый статус
     */
    public void changeStatus(View widget, WordStatus status) {
        this.status = status;
        updateDrawState(textpaint);
        widget.invalidate();
    }

    /**
     * Handle click on word
     *
     * @param widget
     */
    @Override
    public void onClick(View widget) {
        Log.i("tapped on:", word);
        Toast.makeText(widget.getContext(), word, Toast.LENGTH_SHORT).show();
    }

    /**
     * Return the wordId of this span
     *
     * @return id
     */
    public int getSpanTextID() {
        return id;
    }

    /**
     * This function sets the span to record the word number, as the span ID
     *
     * @param spanID
     */
    public void setSpanTextID(int spanID) {
        id = spanID;
    }
}