package com.miklabs.music;

import static com.miklabs.music.FragmentClasses.ContentHome.collection;
import static com.miklabs.music.MainActivity.PlaylistNameForSongs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

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
import com.google.gson.Gson;
import com.miklabs.music.FragmentClasses.ContentHome;
import com.miklabs.music.PlaylistClasses.SQLiteHelperSongs;
import com.miklabs.music.PlaylistClasses.SongsAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class Songs extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBar;
    Toolbar toolbar;
    CoordinatorLayout layout;

    RecyclerView playlistSongRecycler;
    SongsAdapter SongAdapter;
    ArrayList<SongsModel> songs;

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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbar = findViewById(R.id.songsCollapsing_toolbar);
        collapsingToolbar.setTitle("Music List");
        appBar = findViewById(R.id.songsAppbar);
        collapsingToolbar.setTitle("Playlist: " + PlaylistNameForSongs);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.splashScreenStatusBar));


        sqLiteHelperSongs = new SQLiteHelperSongs(this);

        getData();

        playlistSongRecycler = findViewById(R.id.songsRecycler);
//        SongAdapter = new SongsAdapter(SongIdList, SongNameList, SongArtistList, SongDataList, SongDurationList, this);
        SongAdapter = new SongsAdapter(this, songs);
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
                String songsStr = gson0.toJson(songs);

                collection.putString("songs", songsStr);
                collection.putInt("pos", position);
                collection.apply();
                startActivity(new Intent(Songs.this, MusicPlayer.class));

            }

            @Override
            public void onLongItemClick(View view, final int position) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);

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
                                String songsStr = gson0.toJson(songs);

                                collection.putString("songs", songsStr);
                                collection.putInt("pos", position);
                                collection.apply();
                                startActivity(new Intent(Songs.this, MusicPlayer.class));

                                return true;

                            case R.id.playlist_song_more_menu_remove:

                                sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.miklabs.music/databases/PlaylistSongs.db", Context.MODE_PRIVATE, null);


                                SQLiteDataBaseQueryHolder = "delete from " + PlaylistNameForSongs + " where SongName ='" + songs.get(position).getSongName() + "'";
                                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
                                sqLiteDatabaseObj.close();

                                songs.remove(position);

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
        if (item.getItemId() == android.R.id.home) {
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

    public void getData() {
//        SongNameList = sqLiteHelperSongs.getSongNames(PlaylistNameForSongs);
//        SongArtistList = sqLiteHelperSongs.getArtistNames(PlaylistNameForSongs);
//        SongDataList = sqLiteHelperSongs.getSongData(PlaylistNameForSongs);
//        SongIdList = sqLiteHelperSongs.getSongId(PlaylistNameForSongs);
//        SongDurationList = sqLiteHelperSongs.getSongDuration(PlaylistNameForSongs);
        songs = sqLiteHelperSongs.getSongs(PlaylistNameForSongs);
        if (songs.size() == 0) {
            MainActivity m = new MainActivity();
            m.getSnackBar(Songs.this, layout,
                    "No Songs were added to this Playlist!",
                    R.color.whiteColor,
                    R.color.colorPrimaryDark,
                    Snackbar.LENGTH_LONG);
        }
    }
}