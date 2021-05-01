package com.example.music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.example.music.FragmentClasses.ContentHome;
import com.example.music.PlaylistClasses.SQLiteHelperSongs;
import com.example.music.PlaylistClasses.SongsAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.music.FragmentClasses.ContentHome.collection;
import static com.example.music.MainActivity.PlaylistNameForSongs;

public class Songs extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBar;
    Toolbar toolbar;
    CoordinatorLayout layout;

    RecyclerView playlistSongRecycler;
    SongsAdapter SongAdapter;
    ArrayList<Integer> SongIdList;
    ArrayList<String> SongNameList, SongArtistList, SongDataList, SongDurationList;

    SQLiteHelperSongs sqLiteHelperSongs;

    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;

    int POSITION;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);


        layout = findViewById(R.id.activity_songs);


        toolbar = findViewById(R.id.songsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbar = findViewById(R.id.songsCollapsing_toolbar);
        collapsingToolbar.setTitle("Music List");
        appBar=(AppBarLayout)findViewById(R.id.songsAppbar);
        collapsingToolbar.setTitle("Playlist: "+PlaylistNameForSongs);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.splashScreenStatusBar));


        sqLiteHelperSongs = new SQLiteHelperSongs(this);

        getData();

        playlistSongRecycler = (RecyclerView)findViewById(R.id.songsRecycler);
        SongAdapter = new SongsAdapter(SongIdList, SongNameList, SongArtistList, SongDataList, SongDurationList, this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playlistSongRecycler.setLayoutManager(layoutManager);
        playlistSongRecycler.setAdapter(SongAdapter);
        SongAdapter.notifyDataSetChanged();

        Bundle bundle = new Bundle();
        POSITION = bundle.getInt("Playlist Position", 0);




        playlistSongRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, playlistSongRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ContentHome.goingToMusic = true;

                SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                collection = db.edit();

                Gson gson0 = new Gson();
                String songs = gson0.toJson(SongDataList);

                Gson gson1 = new Gson();
                String songname = gson1.toJson(SongNameList);

                Gson gson2 = new Gson();
                String ID = gson2.toJson(SongIdList);

                Gson gson3 = new Gson();
                String artist = gson3.toJson(SongArtistList);

                collection.putString("songs", songs);
                collection.putString("songname", songname);
                collection.putString("artist", artist);
                collection.putString("id", ID);
                collection.putInt("pos", position);
                collection.apply();
                startActivity(new Intent(Songs.this, MusicPlayer.class));

            }

            @Override
            public void onLongItemClick(View view, final int position) {
                PopupMenu popup = new PopupMenu(getApplicationContext(),view);

                popup.inflate(R.menu.playlist_song_more_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.playlist_song_more_menu_play:

                                ContentHome.goingToMusic = true;

                                SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                collection = db.edit();

                                Gson gson0 = new Gson();
                                String songs = gson0.toJson(SongDataList);

                                Gson gson1 = new Gson();
                                String songname = gson1.toJson(SongNameList);

                                Gson gson2 = new Gson();
                                String ID = gson2.toJson(SongIdList);

                                Gson gson3 = new Gson();
                                String artist = gson3.toJson(SongArtistList);

                                collection.putString("songs", songs);
                                collection.putString("songname", songname);
                                collection.putString("artist", artist);
                                collection.putString("id", ID);
                                collection.putInt("pos", position);
                                collection.apply();
                                startActivity(new Intent(Songs.this, MusicPlayer.class));

                                return true;

                            case R.id.playlist_song_more_menu_remove:

                                sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.example.music/databases/PlaylistSongs.db", Context.MODE_PRIVATE, null);


                                SQLiteDataBaseQueryHolder="delete from "+PlaylistNameForSongs+" where SongName ='"+SongNameList.get(position)+"'";
                                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
                                sqLiteDatabaseObj.close();

                                SongNameList.remove(position);
                                SongArtistList.remove(position);
                                SongIdList.remove(position);
                                SongDataList.remove(position);

                                SongAdapter.notifyDataSetChanged();

                                return true;

                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        }));
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.songs_options_menu, menu);

        return true;
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            /*case R.id.songs_options_menu:
                View v = this.findViewById(R.id.songs_options_menu);
                PopupMenu popup = new PopupMenu(this, v);
                popup.inflate(R.menu.songs_options);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.songs_more_menu_add:
                                AddSongs addSongsDialog = new AddSongs(Songs.this, POSITION);
                                addSongsDialog.showDialog();
                                return true;

                            case R.id.songs_more_menu_rename:
                                RenameDialog renameDialog = new RenameDialog(Songs.this, POSITION);
                                renameDialog.showDialog();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }
    public void getData()
    {
        SongNameList = sqLiteHelperSongs.getSongNames(PlaylistNameForSongs);
        SongArtistList=sqLiteHelperSongs.getArtistNames(PlaylistNameForSongs);
        SongDataList = sqLiteHelperSongs.getSongData(PlaylistNameForSongs);
        SongIdList = sqLiteHelperSongs.getSongId(PlaylistNameForSongs);
        SongDurationList = sqLiteHelperSongs.getSongDuration(PlaylistNameForSongs);
        if(SongNameList.size()==0)
        {
            MainActivity m= new MainActivity();
            m.getSnackBar(Songs.this, layout,
                    "No Songs were added to this Playlist!",
                    R.color.whiteColor,
                    R.color.colorPrimaryDark,
                    Snackbar.LENGTH_LONG);
        }
    }
}