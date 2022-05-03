package com.miklabs.music.Dialogs;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static com.miklabs.music.Dialogs.HomeDialogRecyclerAdapter.SONGS;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist.PLAYLIST_TABLE_NAME;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist.Table_Playlist_ID;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist.Table_Playlist_Name;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_Artist;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_ID;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_Name;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_SongDATA;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_SongDuration;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_SongID;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.miklabs.music.FragmentClasses.Playlists;
import com.miklabs.music.MainActivity;
import com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist;
import com.miklabs.music.PlaylistClasses.SQLiteHelperSongs;
import com.miklabs.music.R;
import com.miklabs.music.RecyclerItemClickListener;
import com.miklabs.music.SongsModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HomeAddPlaylistDialog extends Dialog {

    public static String NAME;
    public static ArrayList<SongsModel> songsStorage;
    Activity activity;
    Dialog dialog;
    MainActivity mainActivity;
    EditText text;
    TextView close;
    RecyclerView recyclerView;
    Button createBtn;
    RelativeLayout layout;
    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;
    SQLiteHelperPlaylist sqLiteHelperPlaylist;
    SQLiteHelperSongs sqLiteHelperSongs;
    Cursor cursor;
    String F_Result = "Not_Found";
    boolean recyclerClicked = false;
    HomeDialogRecyclerAdapter Adapter;
    ArrayList<SongsModel> songs;


    public HomeAddPlaylistDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.home_add_playlist_dialog);
        mainActivity = new MainActivity();
        close = dialog.findViewById(R.id.home_dialog_close);
        text = dialog.findViewById(R.id.home_dialog_text);
        recyclerView = dialog.findViewById(R.id.home_dialog_recycler);
        createBtn = dialog.findViewById(R.id.home_dialog_create_btn);
        layout = dialog.findViewById(R.id.home_dialog_layout);
        sqLiteHelperPlaylist = new SQLiteHelperPlaylist(activity);
        sqLiteHelperSongs = new SQLiteHelperSongs(activity);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        doStuff();

        createBtn.setOnClickListener(v -> {
            NAME = text.getText().toString();
            if (NAME.equals("")) {
                mainActivity.getSnackBar(activity, layout, "Please Give a Name to the Playlist", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                text.requestFocus();
            } else {
                PlaylistAlreadyExistsOrNot();
            }
        });

        dialog.show();

        close.setOnClickListener(v -> dialog.dismiss());
    }

    public void doStuff() {

        songs = new ArrayList<>();

        songsStorage = new ArrayList<>();

        getMusic();

        recyclerView = dialog.findViewById(R.id.home_dialog_recycler);
        recyclerView.setHasFixedSize(true);
        Adapter = new HomeDialogRecyclerAdapter(activity, songs);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(activity, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                recyclerClicked = true;
                delete(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public void getMusic() {
        ContentResolver contentResolver = activity.getContentResolver();
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

                songs.add(new SongsModel(
                        currentTitle,
                        currentArtist,
                        currentID,
                        "• " + String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) currentDuration),
                                TimeUnit.MILLISECONDS.toSeconds((long) currentDuration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                currentDuration))),
                        currentData
                ));

            } while (songCurosr.moveToNext());
        }
    }

    public void delete(int position) {

        songsStorage.add(SONGS.get(position));

        SONGS.remove(position);

        Adapter.notifyItemRemoved(position);
    }

    public void SQLiteAddPlaylist() {
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.miklabs.music/databases/Playlists.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                //Assign the values of masterQuery,query,editTable as per your requirements
                return new SQLiteCursor(masterQuery, editTable, query);
            }
        }, null);


        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + PLAYLIST_TABLE_NAME + " (" + Table_Playlist_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + Table_Playlist_Name + " VARCHAR)");
        SQLiteDataBaseQueryHolder = "INSERT INTO " + PLAYLIST_TABLE_NAME + " (PlaylistName) VALUES('" + NAME + "');";
        sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
        sqLiteDatabaseObj.close();
        Playlists.PlaylistName.add(NAME);
    }

    public void SQLiteAddSongs() {
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.miklabs.music/databases/PlaylistSongs.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                //Assign the values of masterQuery,query,editTable as per your requirements
                return new SQLiteCursor(masterQuery, editTable, query);
            }
        }, null);

        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + NAME + " (" + Table_Song_ID + " INTEGER PRIMARY KEY, " + Table_Song_Name + " VARCHAR, " + Table_Song_Artist + " VARCHAR, " + Table_Song_SongID + " INTEGER, " + Table_Song_SongDATA + " VARCHAR, " + Table_Song_SongDuration + " VARCHAR)");

        for (int i = 0; i < songsStorage.size(); i++) {
            String name, artist, data, time;
            int id;

            name = songsStorage.get(i).getSongName();
            artist = songsStorage.get(i).getArtistName();
            data = songsStorage.get(i).getData();
            time = songsStorage.get(i).getDuration();

            id = songsStorage.get(i).getAlbumArt();

            SQLiteDataBaseQueryHolder = "INSERT INTO " + NAME + " (SongId,SongName,SongArtist,SongData,SongDuration) VALUES('" + id + "', '" + name + "', '" + artist + "', '" + data + "', '" + time + "');";
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
        }

        sqLiteDatabaseObj.close();
    }

    public void PlaylistAlreadyExistsOrNot() {
        sqLiteDatabaseObj = sqLiteHelperPlaylist.getWritableDatabase();

        cursor = sqLiteDatabaseObj.query(PLAYLIST_TABLE_NAME, null, "  PlaylistName =?", new String[]{NAME}, null, null, null);

        while (cursor.moveToNext()) {

            if (cursor.isFirst()) {

                cursor.moveToFirst();
                F_Result = "Email Found";
                cursor.close();
            }
        }
        CheckFinalResult();

    }


    public void CheckFinalResult() {

        if (F_Result.equalsIgnoreCase("Email Found")) {
            mainActivity.getSnackBar(activity, layout, "Playlist with same name already exists. Please give a new Name!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
            text.requestFocus();

        } else {
            SQLiteAddPlaylist();
            SQLiteAddSongs();
            if (!recyclerClicked) {
                dialog.dismiss();
                mainActivity.getSnackBar(activity, MainActivity.layout, "Playlist created! But no songs are selected!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                Playlists.adapter.notifyDataSetChanged();
            } else {
                dialog.dismiss();
                mainActivity.getSnackBar(activity, MainActivity.layout, "Playlist created successfully!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                Playlists.adapter.notifyDataSetChanged();
                recyclerClicked = false;
            }
        }

        F_Result = "Not_Found";

    }
}
