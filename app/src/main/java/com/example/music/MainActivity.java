package com.example.music;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.music.Dialogs.ExitDialog;
import com.example.music.Dialogs.HomeAddPlaylistDialog;
import com.example.music.FragmentClasses.AboutUs;
import com.example.music.FragmentClasses.ContentHome;
import com.example.music.FragmentClasses.NavigationStatePagerAdapter;
import com.example.music.FragmentClasses.NonSwipeableViewPager;
import com.example.music.FragmentClasses.Playlists;
import com.example.music.Menu.DrawerAdapter;
import com.example.music.Menu.DrawerItem;
import com.example.music.Menu.SimpleItem;
import com.example.music.Menu.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import static com.example.music.MusicPlayer.mediaPlayer;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener {

    private SlidingRootNav slidingRootNav;
    private static final int POS_HOME = 0;
    private static final int POS_PLAYLISTS = 1;
    private static final int POS_ABOUT_US = 2;
    private static final int POS_EXIT = 4;

    public static RecyclerView list;

    public static SharedPreferences.Editor editor;
    public static SharedPreferences db;

    public static CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBar;
    Toolbar toolbar;

    private String[] screenTitles;
    private Drawable[] screenIcons;
    private int[] cardBackgrounds;

    NonSwipeableViewPager viewPager;

    public static String playlistNameInput;

    public static CoordinatorLayout layout;

    public static String PlaylistNameForSongs="PlaylistName";


    //static SharedPreferences.Editor editor;
    //static boolean goingToMusic;
    //MusicAdapter Adapter;
    //ArrayList<String> arrayListName, arrayListArtist, arrayListDuration, arrayListData;
    //ArrayList<Integer> idArrayList;
    //ListView musicListView;
    //NavigationStatePagerAdapter navigationStatePagerAdapter;
    //ArrayAdapter<Bitmap> arrayAdapter;
    //ArrayList<File> mySongs;
    //NotificationManagerCompat notificationManagerCompat;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.activity_main);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Music List");
        appBar=(AppBarLayout)findViewById(R.id.appbar);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.splashScreenStatusBar));

        db = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        viewPager = (NonSwipeableViewPager) findViewById(R.id.mainViewPager);
        setupViewPager(viewPager);

        setupNavigationDrawer(savedInstanceState);


        //requestPermission(savedInstanceState);

        /* notificationManagerCompat = NotificationManagerCompat.from(this);

        fragment = (LinearLayout) findViewById(R.id.fragment_layout);
        listLayout = (LinearLayout) findViewById(R.id.listView_layout);

        if(mediaPlayer.isPlaying())
        {
            fragment.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    fragment.getLayoutParams();
            params.weight = 1.75f;
            fragment.setLayoutParams(params);


            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)
                    listLayout.getLayoutParams();
            params2.weight = 8.25f;
            listLayout.setLayoutParams(params2);

        }
        else if (mediaPlayer==null){
            fragment.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    fragment.getLayoutParams();
            params.weight = 0f;
            fragment.setLayoutParams(params);


            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)
                    listLayout.getLayoutParams();
            params2.weight = 10f;
            listLayout.setLayoutParams(params2);
        }  */
    }

 /*   public void requestPermission(final Bundle savedInstance) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //display();
                        doStuff();
                        setupNavigationDrawer(savedInstance);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    } */

  /*  public ArrayList<File> findSong(File file){


        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();

        //For Each Loop
        for(File singleFile: files){

            if(singleFile.isDirectory() &&!singleFile.isHidden())
            {
                arrayList.addAll(findSong(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".mp3"))
                {
                    arrayList.add(singleFile);
                }
            }

        }

        return arrayList;
    }

    void display(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        items=new String[mySongs.size()];

        for(int i=0;i<mySongs.size();i++){
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","");
        }

        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        //ListAdapter adapter =new ListAdapter();
        musicListView.setAdapter(arrayAdapter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String songName= musicListView.getItemAtPosition(position).toString();

                startActivity(new Intent(getApplicationContext(), MusicPlayer.class).putExtra("songs",mySongs).putExtra("songname",songName).putExtra("pos",position));


            }
        });

    }  */

  /*  public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCurosr = contentResolver.query(songUri, null, null, null, null);


        if (songCurosr != null && songCurosr.moveToFirst()) {
            int songID = songCurosr.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songTitle = songCurosr.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCurosr.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songDuration = songCurosr.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songData = songCurosr.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);


            do {
                int currentID = songCurosr.getInt(songID);
                String currentTitle = songCurosr.getString(songTitle);
                String currentArtist = songCurosr.getString(songArtist);
                //int currentDuration = parseInt(songCurosr.getString(songDuration));
                int currentDuration = songCurosr.getInt(songDuration);
                String currentData = songCurosr.getString(songData);


                idArrayList.add(currentID);
                arrayListName.add(currentTitle);
                arrayListData.add(currentData);
                arrayListArtist.add(currentArtist);
                arrayListDuration.add("• " + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) currentDuration),
                        TimeUnit.MILLISECONDS.toSeconds((long) currentDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        currentDuration))));

            } while (songCurosr.moveToNext());
        }
    } */

  /*  public void doStuff() {
        //musicListView = (ListView) findViewById(R.id.musicList);
        arrayListName = new ArrayList<>();
        arrayListArtist = new ArrayList<>();
        arrayListDuration = new ArrayList<>();
        arrayListData = new ArrayList<>();
        idArrayList = new ArrayList<>();

        getMusic();
        // ListAdapter adapter = new ListAdapter();

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        Adapter = new MusicAdapter(MainActivity.this, arrayListName, arrayListArtist, idArrayList, arrayListDuration);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goingToMusic = true;

                SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

                editor = db.edit();

                Gson gson0 = new Gson();
                String songs = gson0.toJson(arrayListData);

                Gson gson1 = new Gson();
                String songname = gson1.toJson(arrayListName);

                Gson gson2 = new Gson();
                String ID = gson2.toJson(idArrayList);

                Gson gson3 = new Gson();
                String artist = gson3.toJson(arrayListArtist);

                editor.putString("songs", songs);
                editor.putString("songname", songname);
                editor.putString("artist", artist);
                editor.putString("id", ID);
                editor.putInt("pos", position);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MusicPlayer.class));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

        }));


        //musicListView.setAdapter(adapter);
        /*musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                goingToMusic=true;

                SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

                editor = db.edit();

                Gson gson0 = new Gson();
                String songs = gson0.toJson(arrayListData);

                Gson gson1 = new Gson();
                String songname = gson1.toJson(arrayListName);

                Gson gson2 = new Gson();
                String ID = gson2.toJson(idArrayList);

                Gson gson3 = new Gson();
                String artist = gson3.toJson(arrayListArtist);

                editor.putString("songs", songs);
                editor.putString("songname", songname);
                editor.putString("artist", artist);
                editor.putString("id", ID);
                editor.putInt("pos", position);
                editor.apply();


            }
        }); */


    //}

  /*  class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int array;
            array = arrayListName.size();
            return array;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            convertView = getLayoutInflater().inflate(R.layout.music_list_layout, null);


            ImageView imageView = (ImageView) convertView.findViewById(R.id.layout_image);
            TextView layoutName = (TextView) convertView.findViewById(R.id.layout_name);
            TextView layoutArtist = (TextView) convertView.findViewById(R.id.layout_artist);
            TextView layoutDuration = (TextView) convertView.findViewById(R.id.layout_duration);

            layoutName.setText(arrayListName.get(position));

            layoutArtist.setText(arrayListArtist.get(position));

            layoutDuration.setText(arrayListDuration.get(position));

            loadAlbumArt(idArrayList.get(position), imageView);


            return convertView;
        }
    }  */

    //Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_playlist, menu);

        return true;
    }
/*
    //Menu Item-Onclick Functions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about_us:
                new AlertDialog.Builder(this)
                        .setTitle("About Us")
                        .setMessage(R.string.about_us)
                        .setPositiveButton("Okay", null)
                        .setIcon(R.drawable.icon_info)
                        .show();
                return true;
            case R.id.menu_exit:
                new AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopService();
                                exitApp(this);
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.findViewHolderForAdapterPosition(0).itemView.performClick();

                            }

                        })
                        .setIcon(R.drawable.icon_error)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    } */

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

    public void setupNavigationDrawer(Bundle savedInstance)
    {

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

        list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);

        drawadapter.setSelected(POS_HOME);

        //NAVIGATION ENDS

    }

    //Navigation Menu Item-Onclick Funtions
    @Override
    public void onItemSelected(int position) {
        editor = db.edit();
        if (position == POS_HOME) {

            setViewPager(0);
            collapsingToolbar.setTitle("Music List");
            //viewPager.setVisibility(View.GONE);
            //recyclerView.setVisibility(View.VISIBLE);
            appBar.setExpanded(true);
            editor.putInt("fragmentNo", 0);
            editor.apply();

        }
        if (position == POS_EXIT) {

           /* new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopService();
                            exitApp();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int p = db.getInt("fragmentNo", 0);
                            list.findViewHolderForAdapterPosition(p).itemView.performClick();
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.drawable.icon_error)
                    .show(); */
            ExitDialog d = new ExitDialog(MainActivity.this);
            d.showDialog();

        }
        if (position == POS_PLAYLISTS) {

            setViewPager(1);
            //viewPager.setVisibility(View.VISIBLE);
            //recyclerView.setVisibility(View.GONE);
            collapsingToolbar.setTitle("Playlists");
            appBar.setExpanded(true);
            editor.putInt("fragmentNo", 1);
            editor.apply();
        }
        if (position == POS_ABOUT_US) {

            setViewPager(2);
            //viewPager.setVisibility(View.VISIBLE);
            //recyclerView.setVisibility(View.GONE);
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
        switch (item.getItemId()) {
            case R.id.action_add_playlist:

                HomeAddPlaylistDialog d = new HomeAddPlaylistDialog(MainActivity.this);
                d.showDialog();

               /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter the Name of Playlist");
                builder.setIcon(getResources().getDrawable(R.drawable.icon_add_playlist));

// Set up the input
                final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playlistNameInput = input.getText().toString();
                        PlaylistName.add(playlistNameInput);
                        getSnackBar("Playlist created successfully!");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
                */
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public void getSnackBar(Activity activity, View view, String stringMessage, int backgroundColor, int textColor, int duration ){
        Snackbar snackbar = Snackbar.make(view, stringMessage, duration);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(activity.getResources().getColor(backgroundColor));
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(activity.getResources().getColor(textColor));
        snackbar.show();
    }

    @Override
    public void onBackPressed() {

            ExitDialog d = new ExitDialog(MainActivity.this);
            d.showDialog();

    }
}