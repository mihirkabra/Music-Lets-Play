package com.miklabs.music;

import static com.miklabs.music.MusicPlayer.mediaPlayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.miklabs.music.Dialogs.ExitDialog;
import com.miklabs.music.Dialogs.HomeAddPlaylistDialog;
import com.miklabs.music.FragmentClasses.AboutUs;
import com.miklabs.music.FragmentClasses.ContentHome;
import com.miklabs.music.FragmentClasses.NavigationStatePagerAdapter;
import com.miklabs.music.FragmentClasses.NonSwipeableViewPager;
import com.miklabs.music.FragmentClasses.Playlists;
import com.miklabs.music.Menu.DrawerAdapter;
import com.miklabs.music.Menu.DrawerItem;
import com.miklabs.music.Menu.SimpleItem;
import com.miklabs.music.Menu.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener {

    private static final int POS_HOME = 0;
    private static final int POS_PLAYLISTS = 1;
    private static final int POS_ABOUT_US = 2;
    private static final int POS_EXIT = 4;
    public static RecyclerView list;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences db;
    public static CollapsingToolbarLayout collapsingToolbar;
    public static CoordinatorLayout layout;
    public static String PlaylistNameForSongs = "PlaylistName";
    AppBarLayout appBar;
    Toolbar toolbar;
    NonSwipeableViewPager viewPager;
    private SlidingRootNav slidingRootNav;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private int[] cardBackgrounds;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Music List");
        appBar = findViewById(R.id.appbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.splashScreenStatusBar));

        db = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        viewPager = findViewById(R.id.mainViewPager);
        setupViewPager(viewPager);

        setupNavigationDrawer(savedInstanceState);
    }

    //Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_playlist, menu);

        return true;
    }

    //Method to add Fragments to the NonSwipeableViewPager
    public void setupViewPager(NonSwipeableViewPager viewPager) {

        NavigationStatePagerAdapter pagerAdapter = new NavigationStatePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new ContentHome(), "Music List");
        pagerAdapter.addFragment(new Playlists(), "Playlists");
        pagerAdapter.addFragment(new AboutUs(), "About Us");

        viewPager.setAdapter(pagerAdapter);

    }

    //Method to set Active Fragment in the NonSwipeableViewPager
    public void setViewPager(int number) {
        viewPager.setCurrentItem(number);
    }

    public void setupNavigationDrawer(Bundle savedInstance) {
        //NAVIGATION
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstance)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();
        cardBackgrounds = loadCardBackgrounds();

        DrawerAdapter drawadapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_PLAYLISTS),
                createItemFor(POS_ABOUT_US),
                new SpaceItem(48),
                createItemFor(POS_EXIT)));
        drawadapter.setListener(this);

        list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);

        drawadapter.setSelected(POS_HOME);
    }

    //Navigation Menu Item-Onclick Funtions
    @Override
    public void onItemSelected(int position) {
        editor = db.edit();
        if (position == POS_HOME) {
            setViewPager(0);
            collapsingToolbar.setTitle("Music List");
            appBar.setExpanded(true);
            editor.putInt("fragmentNo", 0);
            editor.apply();
        }
        if (position == POS_EXIT) {
            ExitDialog d = new ExitDialog(MainActivity.this);
            d.showDialog();
        }
        if (position == POS_PLAYLISTS) {
            setViewPager(1);
            collapsingToolbar.setTitle("Playlists");
            appBar.setExpanded(true);
            editor.putInt("fragmentNo", 1);
            editor.apply();
        }
        if (position == POS_ABOUT_US) {
            setViewPager(2);
            collapsingToolbar.setTitle("About Us");
            editor.putInt("fragmentNo", 2);
            editor.apply();
        }
        slidingRootNav.closeMenu();
    }

    //Method for customizing Menu-items
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position], cardBackgrounds[position])
                //Icon color when not selected
                .withIconTint(color(R.color.blackColor))

                //Text color when not selected
                .withTextTint(color(R.color.blackColor))

                //Background color when not selected
                .withCardTint(color(R.color.menuBackgroundUnselected))

                //Icon color when selected
                .withSelectedIconTint(color(R.color.whiteColor))

                //Text color when selected
                .withSelectedTextTint(color(R.color.whiteColor))

                //Background color when selected
                .withSelectedCardTint(color(R.color.menuBackgroundSelected));
    }

    //Method for getting the Menu-items' Titles
    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    //Method for getting the Menu-items' Icons
    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    //Method for getting the Menu-items' Background-color
    private int[] loadCardBackgrounds() {
        return getResources().getIntArray(R.array.cardBackgrounds);
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    //Method to Stop the running Foregroundservice
    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    //Method for closing the Application Completely
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void exitApp() {

        editor.putInt("fragmentNo", 0);
        editor.apply();

        finishAffinity();
        System.exit(0);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_add_playlist) {
            HomeAddPlaylistDialog d = new HomeAddPlaylistDialog(MainActivity.this);
            d.showDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    public void getSnackBar(Activity activity, View view, String stringMessage, int backgroundColor, int textColor, int duration) {
        Snackbar snackbar = Snackbar.make(view, stringMessage, duration);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(activity.getResources().getColor(backgroundColor));
        TextView tv = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(activity.getResources().getColor(textColor));
        snackbar.show();
    }

    @Override
    public void onBackPressed() {

        ExitDialog d = new ExitDialog(MainActivity.this);
        d.showDialog();

    }
}