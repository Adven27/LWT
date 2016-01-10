package com.example.android.navigationdrawerexample;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.model.Phrase;
import com.example.android.navigationdrawerexample.model.WordStatus;
import com.example.android.navigationdrawerexample.util.DBAssetHelper;
import com.example.android.navigationdrawerexample.util.DBHelper;
import com.example.android.navigationdrawerexample.util.MyWebViewClient;
import com.example.android.navigationdrawerexample.view.TabsPagerAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import java.util.HashMap;
import java.util.Map;

import de.halfreal.spezi.views.ProgressButton;


public class MainActivity extends FragmentActivity {
    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
    private static final String TAG = "MainActivity";
    private static final int TRANSLATE_REQUEST_CODE = 555;
    private static Phrase mLastPhrase;
    private Map<Phrase, String> learnPhrases = new HashMap<Phrase, String>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private WebView dictWebView;
    private ViewPager viewPager;
    private DBAssetHelper dbAssetHelper;
    private TabsPagerAdapter mTabsAdapter;
    private boolean isSelectionButtonMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbAssetHelper = DBHelper.getDbAssetHelper(this);

        /* for BOTTOM slide bar*/
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        //slidingUpPanelLayout.setPanelHeight(112);


        // Initilization Tabs
        tabsInit();
        slidingUpPanelInit();
        drawerInit();
        dictViewInit();

        //viewPager.setPadding(0,112,0,0);

        boolean isActionBarHidden = savedInstanceState != null &&
                savedInstanceState.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false);
        if (isActionBarHidden) {
            getActionBar().hide();
        }
    }

    private void dictViewInit() {
        dictWebView = (WebView) findViewById(R.id.dictwebview);
        dictWebView.getSettings().setJavaScriptEnabled(true);
        dictWebView.setWebViewClient(new MyWebViewClient());
    }

    private void drawerInit() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /*ActionBarDrawerToggle ties together the the proper interactions
        between the sliding drawer and the action bar app icon*/
        //TODO: get rid of text constants
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void tabsInit() {
        mTabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mTabsAdapter);

        //   getActionBar().addTab(getActionBar().newTab().setText("Text").setTabListener(this));
        //   getActionBar().addTab(getActionBar().newTab().setText("Term:").setTabListener(this));

        //on swiping the viewpager make respective tab selected
        /*viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page make respected tab selected
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });*/
    }

    private void slidingUpPanelInit() {
        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        //layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        layout.setAnchorPoint(0.7f);
        layout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                if (slideOffset < 0.2) {
                    if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                } else {
                    if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, !getActionBar().isShowing());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*The action bar home/up action should open or close the drawer.
        ActionBarDrawerToggle will take care of this.*/
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_webmode:
//                switchView();
                return true;
            case R.id.clear_base:
                int delRows = dbAssetHelper.clearBase();
                Log.i(TAG, "DELETED ROWS:" + String.valueOf(delRows));

                //TODO: hard restart
                intent = getIntent();
                finish();
                startActivity(intent);
                //recreate();
                return true;
            case R.id.restart:
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onPostCreate");
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "DESTROY");
        super.onDestroy();
        DBHelper.getDbAssetHelper(this).close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (TRANSLATE_REQUEST_CODE == requestCode) {
            //process resultCode
        }
    }

/*
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
*/

    public void searchPhrase(Phrase word) {
        mLastPhrase = word;
        dictWebView.loadUrl("http://www.thefreedictionary.com/_/dict.aspx?word=" + mLastPhrase.getPhrase());
    }

    public void changePhraseStatus(WordStatus status) {
        if (mLastPhrase != null && status != mLastPhrase.getStatus()) {
            Toast.makeText(this, "changePhraseStatus " + mLastPhrase.getPhrase() + " status " + status.getCode(), Toast.LENGTH_SHORT).show();
            WordStatus curStatus = mLastPhrase.getStatus();
            mLastPhrase.setStatus(status);
            if (curStatus == WordStatus.UNKNOWN) {
                dbAssetHelper.insertPhrase(mLastPhrase);
            } else {
                if (status == WordStatus.UNKNOWN) {
                    dbAssetHelper.deletePhrase(mLastPhrase);
                } else {
                    dbAssetHelper.updatePhrase(mLastPhrase);
                }
            }
            learnPhrases.clear();
            mTabsAdapter.reInitArticle();
        }
    }


    public void onClickPhraseActionButton(View view) {
//        ProgressButton button = (ProgressButton) view;

        switch (view.getId()) {
            case R.id.known_word:
                changePhraseStatus(WordStatus.KNOWN);
                break;
            case R.id.learn_word:
                changePhraseStatus(WordStatus.LEARN);
                break;
            case R.id.forget_word:
                changePhraseStatus(WordStatus.UNKNOWN);
                break;
        }
    }

    public DBAssetHelper getDbAssetHelper() {
        return dbAssetHelper;
    }

    public void prevWord(View view) {
        if (isSelectionButtonMode) {
            mTabsAdapter.removeWord();
        } else {
            mTabsAdapter.prevNewWord();
        }
        searchPhrase(mTabsAdapter.getSelectedPhrase());
    }

    public void nextWord(View view) {
        if (isSelectionButtonMode) {
            mTabsAdapter.addWord();
        } else {
            mTabsAdapter.nextNewWord();
        }

        searchPhrase(mTabsAdapter.getSelectedPhrase());
    }

    public void toggleButtonMode(View view) {
        isSelectionButtonMode = !isSelectionButtonMode;

        int newColor = isSelectionButtonMode ? R.color.select_word
                : R.color.new_word;
        int olDColor = !isSelectionButtonMode ? R.color.select_word
                : R.color.new_word;

        ImageButton nextWordBtn = (ImageButton) findViewById(R.id.next_word);
        ImageButton prevtWordBtn = (ImageButton) findViewById(R.id.prev_word);
        ImageButton modeBtn = (ImageButton) findViewById(R.id.toggle_mode);

        modeBtn.setImageResource(olDColor);
        nextWordBtn.setBackgroundColor(newColor);
        prevtWordBtn.setBackgroundColor(newColor);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //user has long pressed your TextView
        menu.add(0, v.getId(), 0, "text that you want to show in the context menu - I use simply Copy");
    }

    public Map<Phrase, String> getLearnPhrases() {
        Log.i("LP", "GET " + learnPhrases.size());
        return learnPhrases;
    }

    public void addLearnPhrase(Phrase p, String sentence) {
        Log.i("LP", "ADD " + p.getPhrase());
        learnPhrases.put(p, sentence);
    }

    public boolean learnPhraseExist(Phrase p) {
        Log.i("LP", "Exist " + p.getPhrase() + " " + learnPhrases.containsKey(p));
        return learnPhrases.containsKey(p);
    }

    public void enableLoadState() {
        ProgressButton button = (ProgressButton) findViewById(R.id.learn_word);
        button.setLoadingState(null);
    }

    public void disableLoadState() {
        ProgressButton button = (ProgressButton) findViewById(R.id.learn_word);

        if (button.isLoading()) {
            button.setLoadingState(true);
        } else if (button.isSelected()) {
            button.setLoadingState(false);
        }

        mTabsAdapter.getTermTabFragment().reInitListView();
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //selectItem(position);
        }
    }
}