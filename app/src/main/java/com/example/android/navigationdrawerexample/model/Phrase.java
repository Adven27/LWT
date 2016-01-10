package com.example.android.navigationdrawerexample.model;

/**
 * Created by adven on 23.03.14.
 */
public class Phrase {
    private final String phrase;
    private String translate;

    private WordStatus status;

    public Phrase(String phrase, String translate, WordStatus status) {
        this.phrase = phrase;
        this.translate = translate;
        this.status = status;
    }

    @Override
    public int hashCode() {
        return phrase.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.getPhrase().equals(((Phrase) o).getPhrase());
    }

    public String getPhrase() {
        return phrase;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public WordStatus getStatus() {
        return status;
    }

    public void setStatus(WordStatus status) {
        this.status = status;
    }

}
