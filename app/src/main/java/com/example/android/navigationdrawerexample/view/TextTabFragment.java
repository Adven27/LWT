package com.example.android.navigationdrawerexample.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.android.navigationdrawerexample.MainActivity;
import com.example.android.navigationdrawerexample.R;
import com.example.android.navigationdrawerexample.model.Phrase;
import com.example.android.navigationdrawerexample.model.WordStatus;
import com.example.android.navigationdrawerexample.util.DBAssetHelper;
import com.example.android.navigationdrawerexample.util.DemoJavaScriptInterface;
import com.example.android.navigationdrawerexample.util.JSHelper;
import com.example.android.navigationdrawerexample.util.MyWebChromeClient;
import com.example.android.navigationdrawerexample.util.MyWebViewClient;
import com.example.android.navigationdrawerexample.util.StubHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.FeatureGeneratorUtil;


/**
 * Created by adven on 10.05.14.
 */
public class TextTabFragment extends Fragment {
    private static final String TAG = "TextTabFragment";
    private static Float scrollSpot;
    private TextView mainTextView;
    private WebView mainWebView;
    private ViewSwitcher viewSwitcher;
    private int curContentIndex = 0;
    private int contentCount = 0;
    private SelectedSpan selectedSpan;
    private ScrollView scrollView;

    public TextTabFragment() {
    }

    public SelectedSpan getSelectedSpan() {
        return selectedSpan;
    }

    public WebView getMainWebView() {
        return mainWebView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text_tab, container, false);

        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.mainviewswitcher);
        mainTextView = (TextView) rootView.findViewById(R.id.main);
        // mActivity.registerForContextMenu(mainTextView);

        scrollView = (ScrollView) rootView.findViewById(R.id.scroller);

        // prevents it from running at certain times, such as the first time you launch the activity
        if (scrollSpot != null) {
            scrollView.post(new Runnable() {
                public void run() {
                    setScrollSpot(scrollSpot);
                }
            });
        }


        mainWebView = (WebView) rootView.findViewById(R.id.mainwebview);
        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mainWebView.setWebChromeClient(new MyWebChromeClient());
        mainWebView.setWebViewClient(new MyWebViewClient());
        mainWebView.addJavascriptInterface(new DemoJavaScriptInterface(this.getActivity()) {
            @Override
            public void initContents(int newCount) {
                contentCount = newCount;
                curContentIndex = 0;
            }
        }, "Android");

        // Handle the intent that started this activity
        handleIntent(this.getActivity().getIntent());

        //registerForContextMenu(mainTextView);
        return rootView;
    }


    public void onDestroy() {
        super.onDestroy();
        scrollSpot = getScrollSpot();
    }

    /**
     * @return an encoded float, where the integer portion is the offset of the
     * first character of the first fully visible line, and the decimal
     * portion is the percentage of a line that is visible above it.
     */
    private float getScrollSpot() {
        int y = scrollView.getScrollY();
        Layout layout = mainTextView.getLayout();
        int topPadding = -layout.getTopPadding();
        if (y <= topPadding) {
            return (float) (topPadding - y) / mainTextView.getLineHeight();
        }

        int line = layout.getLineForVertical(y - 1) + 1;
        int offset = layout.getLineStart(line);
        int above = layout.getLineTop(line) - y;
        return offset + (float) above / mainTextView.getLineHeight();
    }

    private void setScrollSpot(float spot) {
        int offset = (int) spot;
        int above = (int) ((spot - offset) * mainTextView.getLineHeight());
        Layout layout = mainTextView.getLayout();
        int line = layout.getLineForOffset(offset);
        int y = (line == 0 ? -layout.getTopPadding() : layout.getLineTop(line))
                - above;
        scrollView.scrollTo(0, y);
    }

    /**
     * on button "follow" press
     *
     * @param view pressed button
     */
    public void runJS(View view) {
        mainWebView.loadUrl(JSHelper.WEB_CLIPPER_JS);
    }

    public void upJS(View view) {
        if (curContentIndex > 0) {
            int nextIndex = curContentIndex - 1;
            mainWebView.loadUrl(String.format(JSHelper.NEXT_JS, curContentIndex, nextIndex));
            curContentIndex = nextIndex;
        }
    }

    public void downJS(View view) {
        if (curContentIndex < contentCount - 1) {
            int nextIndex = curContentIndex + 1;
            mainWebView.loadUrl(String.format(JSHelper.NEXT_JS, curContentIndex, nextIndex));
            curContentIndex = nextIndex;
        }
    }

    public void importJS(View view) {
        mainWebView.loadUrl(String.format(JSHelper.IMPORT_JS, curContentIndex));
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        //пришел текст
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        } else {
            String text = StubHelper.getLongText(10);
            initTextView(text);
        }


    }

    private void handleSendText(String sharedText) {
        URL url = null;
        System.out.println("handleSendText sharedText = [" + sharedText + "]");
        try {
            url = new URL(sharedText);
        } catch (MalformedURLException e) {
            Log.e(TAG, "bad url entered");
        }

        //got URL: switch to webView and load url
        if (url != null) {
            if (R.id.mainwebview != viewSwitcher.getCurrentView().getId()) {
                switchView();
            }
            mainWebView.loadUrl(url.toString());
        } else {
            initTextView(sharedText);
        }
    }

    private void switchView() {
        System.out.println("TextTabFragment.switchView");
        Animation inAnim = new AlphaAnimation(0, 1);
        inAnim.setDuration(2000);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(2000);
        viewSwitcher.setInAnimation(inAnim);
        viewSwitcher.setOutAnimation(outAnim);
        viewSwitcher.showNext();
    }

    private void initTextView(String text) {
        mainTextView.setText(text);
        reInitTextView();
    }

    private Object[] smartSearch(DBAssetHelper db, String txt, List<Span> tokens, int i, boolean newIfEmpty) {
        if (tokens.size() <= i) {
            return exactSearch(db, txt, tokens, i - 1, i == 1);
        }
        String str = "";
        for (int j = 0; j <= i; j++) {
            int start = tokens.get(j).getStart();
            int end = tokens.get(j).getEnd();
            str += txt.substring(start, end) + " ";
        }
        String searchWords = str.trim();
        List<Phrase> phrases = db.getPhrasesLike(searchWords, newIfEmpty);
        if (phrases.isEmpty()) {
            return exactSearch(db, txt, tokens, i - 1, i == 1);
        } else if (phrases.size() == 1) {
            Phrase p = phrases.get(0);
            if (p.getPhrase().equals(searchWords)) {
                return new Object[]{p, i};
            }
        }
        return smartSearch(db, txt, tokens, i + 1, false);
    }

    private Object[] exactSearch(DBAssetHelper db, String txt, List<Span> tokens, int i, boolean newIfEmpty) {
        String str = "";
        for (int j = 0; j <= i; j++) {
            int start = tokens.get(j).getStart();
            int end = tokens.get(j).getEnd();
            str += txt.substring(start, end) + " ";
        }
        Phrase p = db.getPhrase(str.trim(), newIfEmpty);
        if (p != null) {
            return new Object[]{p, i};
        }
        return exactSearch(db, txt, tokens, i - 1, i == 1);
    }

    public void reInitTextView() {
        //make words clickable
        mainTextView.setMovementMethod(LinkMovementMethod.getInstance());
        new TextParseTask().execute(/*StubHelper.getLongText(10)*/String.valueOf(mainTextView.getText()));
        Log.i("Time", "initTextView end");
    }

    private Spannable parse(String url) {
        Spannable spans = new SpannableString(url);
        for (WordSpan ws : spans.getSpans(0, spans.length(), WordSpan.class)) {
            spans.removeSpan(ws);
        }

        String txt = spans.toString();

        List<Span> tokens = getTokens(txt);
        List<Span> sentences = getSentences(txt);

        List<Span> tokenList = new ArrayList<Span>(tokens);
        while (!tokenList.isEmpty()) {
            Object[] res = smartSearch(((MainActivity) getActivity()).getDbAssetHelper(), txt, tokenList, 0, true);
            Phrase p = (Phrase) res[0];
            Integer i = (Integer) res[1];

            int start = tokenList.get(0).getStart();
            int end = tokenList.get(i).getEnd();
            spans.setSpan(new WordSpan(this, p), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (p.getStatus().equals(WordStatus.LEARN) && !((MainActivity) getActivity()).learnPhraseExist(p)) {
                for (Span sen : sentences) {
                    if (sen.contains(start)) {
                        ((MainActivity) getActivity()).addLearnPhrase(p, (String) sen.getCoveredText(txt));
                        break;
                    }
                }
            }

            for (int j = 0; j <= i; j++) {
                tokenList.remove(0);
            }
        }

        //reSetSelectedSpan(spans);
        return spans;
    }

    private void reSetSelectedSpan(Spannable spans) {
        if (selectedSpan != null) {
            int st = spans.getSpanStart(selectedSpan);
            int en = spans.getSpanEnd(selectedSpan);
            for (SelectedSpan ss : spans.getSpans(0, spans.length(), SelectedSpan.class)) {
                spans.removeSpan(ss);
            }
            spans.setSpan(selectedSpan, st, en, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private List<Span> getTokens(String txt) {
        List<Span> res = new ArrayList<Span>();
        try {
            //modelInT = mActivity.getAssets().open("nlp/en-token.bin");
            //  final TokenizerModel modelT = new TokenizerModel(modelInT);
            //modelInT.close();

            //TokenizerME tokenizerME = new TokenizerME(modelT);
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            List<Span> tokens = Arrays.asList(tokenizer.tokenizePos(txt));
            for (Span s : tokens) {
                String coveredText = (String) s.getCoveredText(txt);
                String feature = FeatureGeneratorUtil.tokenFeature(coveredText);
                if (!"other".equals(feature) && !"num".equals(feature)
                        && !"2d".equals(feature) && !"4d".equals(feature)) {
                    res.add(s);
                }
                Log.i("NLP", s.getStart() + " " + s.getEnd() + " " + coveredText + " " + feature);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return res;
    }

    private List<Span> getSentences(String txt) {
        List<Span> res = new ArrayList<Span>();
        InputStream modelIn = null;
        try {
            modelIn = getActivity().getAssets().open("nlp/en-sent.bin");
            final SentenceModel model = new SentenceModel(modelIn);
            modelIn.close();

            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            res = Arrays.asList(sentenceDetector.sentPosDetect(txt));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                }
            }
        }
        return res;
    }

    void onWordSpanClick(WordSpan wordSpan) {
        selectWordSpan(mainTextView, wordSpan);
        //update dict panel
        ((MainActivity) getActivity()).searchPhrase(wordSpan.getPhrase());
    }

    public void nextNewWord() {
        Log.i("Time", "nextNewWord start");
        TextView tv = mainTextView;
        Spannable spans = (Spannable) tv.getText();
        //there is no selected span - make first "unknown" wordSpan selected
        if (selectedSpan == null) {
            makeFirstSelected(tv, spans);
        } else {
            //make selected next "unknown" word span after selected span
            for (WordSpan ws : spans.getSpans(spans.getSpanEnd(selectedSpan), spans.length(), WordSpan.class)) {
                if (ws.getPhrase().getStatus() == WordStatus.UNKNOWN) {
                    selectWordSpan(tv, spans, ws);
                    break;
                }
            }
        }
    }

    public void prevNewWord() {
        TextView tv = mainTextView;
        Spannable spans = (Spannable) tv.getText();
        //there is no selected span - make first "unknown" wordSpan selected
        if (selectedSpan == null) {
            makeFirstSelected(tv, spans);
        } else {
            //make selected prev "unknown" word span before selected span
            WordSpan[] wordSpans = spans.getSpans(0, spans.getSpanStart(selectedSpan), WordSpan.class);
            for (int i = wordSpans.length - 1; i >= 0; i--) {
                WordSpan ws = wordSpans[i];
                if (ws.getPhrase().getStatus() == WordStatus.UNKNOWN) {
                    selectWordSpan(tv, spans, ws);
                    break;
                }
            }
        }
    }

    private void makeFirstSelected(TextView tv, Spannable spans) {
        WordSpan[] wordSpans = spans.getSpans(0, spans.length(), WordSpan.class);
        WordSpan firstUnknSpan = null;
        for (WordSpan ws : wordSpans) {
            if (ws.getPhrase().getStatus() == WordStatus.UNKNOWN) {
                firstUnknSpan = ws;
                break;
            }
        }
        if (firstUnknSpan != null) {
            selectWordSpan(tv, spans, firstUnknSpan);
        }
    }

    private void selectWordSpan(TextView tv, Spannable spans, WordSpan ws) {
        spans.removeSpan(selectedSpan);
        selectedSpan = new SelectedSpan(ws.getPhrase());
        spans.setSpan(selectedSpan, spans.getSpanStart(ws), spans.getSpanEnd(ws), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spans, TextView.BufferType.SPANNABLE);
        tv.invalidate();
    }

    private void selectWordSpan(TextView tv, WordSpan ws) {
        Spannable spans = (Spannable) tv.getText();
        selectWordSpan(tv, spans, ws);
    }

    public void addWord() {
        if (selectedSpan != null) {
            TextView tv = mainTextView;
            Spannable spans = (Spannable) tv.getText();
            //add to selection next word span after selected span
            for (WordSpan ws : spans.getSpans(spans.getSpanEnd(selectedSpan), spans.length(), WordSpan.class)) {
                updateSelection(tv, spans, ws);
                break;
            }
        }
    }

    public void removeWord() {
        if (selectedSpan != null) {
            TextView tv = mainTextView;
            Spannable spans = (Spannable) tv.getText();
            //deselect last word span into selection if there is more than 1 of it
            WordSpan[] selectedWordSpans = spans.getSpans(spans.getSpanStart(selectedSpan), spans.getSpanEnd(selectedSpan), WordSpan.class);
            if (selectedWordSpans.length > 1) {
                WordSpan beforeLastWordSpan = selectedWordSpans[selectedWordSpans.length - 2];
                updateSelection(tv, spans, beforeLastWordSpan);
            }
        }
    }

    private void updateSelection(TextView tv, Spannable spans, WordSpan lastWordSpan) {
        int start = spans.getSpanStart(selectedSpan);
        int end = spans.getSpanEnd(lastWordSpan);

        spans.removeSpan(selectedSpan);
        selectedSpan =
                new SelectedSpan(
                        new Phrase(selectedSpan.getPhrase().getPhrase() + " " + lastWordSpan.getPhrase().getPhrase(),
                                "", WordStatus.UNKNOWN)
                );
        spans.setSpan(selectedSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spans, TextView.BufferType.SPANNABLE);
        tv.invalidate();
    }

    private class TextParseTask extends AsyncTask<String, Void, Spannable> {
        @Override
        protected void onPreExecute() {
            ((MainActivity) getActivity()).enableLoadState();
            super.onPreExecute();
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Spannable doInBackground(String... urls) {
            try {
                return parse(urls[0]);
            } catch (Exception e) {
                return (Spannable) mainTextView.getText();
            }
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(Spannable result) {
            try {
                mainTextView.setText(result);
                ((MainActivity) getActivity()).disableLoadState();
            } catch (Exception e) {

            }
        }
    }

}