package com.example.android.navigationdrawerexample.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.navigationdrawerexample.model.Phrase;

/**
 * Created by adven on 12.04.14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private final FragmentManager mFragmentManager;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    public Phrase getSelectedPhrase() {
        return getTextTabFragment().getSelectedSpan().getPhrase();
    }

    public void reInitArticle() {
        getTextTabFragment().reInitTextView();
    }


    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new TextTabFragment();
            case 1:
                return new TermTabFragment();
        }

        return null;
    }

    public TextTabFragment getTextTabFragment() {
        return ((TextTabFragment) mFragmentManager.getFragments().get(0));
    }

    public TermTabFragment getTermTabFragment() {
        return ((TermTabFragment) mFragmentManager.getFragments().get(1));
    }

    @Override
    public int getCount() {
        //TODO: get item count - equal to number of tabs
        return 2;
    }

    public void removeWord() {
        getTextTabFragment().removeWord();
    }

    public void addWord() {
        getTextTabFragment().addWord();
    }

    public void nextNewWord() {
        getTextTabFragment().nextNewWord();
    }

    public void prevNewWord() {
        getTextTabFragment().prevNewWord();
    }
}