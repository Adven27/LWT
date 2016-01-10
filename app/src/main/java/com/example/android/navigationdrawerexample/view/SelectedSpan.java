package com.example.android.navigationdrawerexample.view;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

import com.example.android.navigationdrawerexample.model.Phrase;

/**
 * Created by adven on 10.05.14.
 */
public final class SelectedSpan extends ForegroundColorSpan /*extends ReplacementSpan */ {
    private final Phrase phrase;
    private int mWidth;

    public SelectedSpan(Phrase phrase) {
        super(phrase.getStatus().getColor());
        this.phrase = phrase;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        int oldColor = ds.getColor();
        super.updateDrawState(ds);
        if (Color.TRANSPARENT == ds.getColor()) {
            ds.setColor(oldColor);
        }

        ds.bgColor = Color.RED/*phrase.getStatus().getColor()*/;
    }
/*
    //@Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }

    //@Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(phrase.getStatus().getColor());
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(0);
        RectF rectF = new RectF((int) x, top, (int) (x + paint.measureText(text, start, end)), bottom);
        canvas.drawRoundRect(rectF, 20, 20, p);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3.5f);
        canvas.drawRoundRect(rectF, 20, 20, p);
        canvas.drawText(text.subSequence(start, end).toString(), x, y, paint);
    }*/
}
