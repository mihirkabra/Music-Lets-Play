package com.example.music.Dialogs;

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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.music.FragmentClasses.Playlists;
import com.example.music.MainActivity;
import com.example.music.PlaylistClasses.SQLiteHelperPlaylist;
import com.example.music.PlaylistClasses.SQLiteHelperSongs;
import com.example.music.R;
import com.example.music.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static com.example.music.Dialogs.HomeDialogRecyclerAdapter.ALBUM_ART;
import static com.example.music.Dialogs.HomeDialogRecyclerAdapter.ARTIST_NAME;
import static com.example.music.Dialogs.HomeDialogRecyclerAdapter.DATA;
import static com.example.music.Dialogs.HomeDialogRecyclerAdapter.SONG_NAME;
import static com.example.music.Dialogs.HomeDialogRecyclerAdapter.TIME;
import static com.example.music.PlaylistClasses.SQLiteHelperPlaylist.PLAYLIST_TABLE_NAME;
import static com.example.music.PlaylistClasses.SQLiteHelperPlaylist.Table_Playlist_ID;
import static com.example.music.PlaylistClasses.SQLiteHelperPlaylist.Table_Playlist_Name;
import static com.example.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_Artist;
import static com.example.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_ID;
import static com.example.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_Name;
import static com.example.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_SongDATA;
import static com.example.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_SongDuration;
import static com.example.music.PlaylistClasses.SQLiteHelperSongs.Table_Song_SongID;

public class HomeAddPlaylistDialog extends Dialog {

    Activity activity;
    public static String NAME;
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

    ArrayList<String> arrayListName, arrayListArtist, arrayListDuration, arrayListData;
    ArrayList<Integer> idArrayList;
    public static ArrayList<String> arrayListNameStorage, arrayListArtistStorage, arrayListDataStorage, arrayListDurationStorage;
    public static ArrayList<Integer> idArrayListStorage;


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
        close = (TextView) dialog.findViewById(R.id.home_dialog_close);
        text = (EditText) dialog.findViewById(R.id.home_dialog_text);
        recyclerView = (RecyclerView) dialog.findViewById(R.id.home_dialog_recycler);
        createBtn = (Button) dialog.findViewById(R.id.home_dialog_create_btn);
        layout = (RelativeLayout) dialog.findViewById(R.id.home_dialog_layout);
        sqLiteHelperPlaylist = new SQLiteHelperPlaylist(activity);
        sqLiteHelperSongs = new SQLiteHelperSongs(activity);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        doStuff();


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NAME = text.getText().toString();
                if (NAME.equals("")) {
                    mainActivity.getSnackBar(activity, layout, "Please Give a Name to the Playlist", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                    text.requestFocus();
                } else {
                    PlaylistAlreadyExistsOrNot();
                }
            }
        });

        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void doStuff() {
        //musicListView = (ListView) findViewById(R.id.musicList);
        arrayListName = new ArrayList<>();
        arrayListArtist = new ArrayList<>();
        arrayListDuration = new ArrayList<>();
        arrayListData = new ArrayList<>();
        idArrayList = new ArrayList<>();

        arrayListArtistStorage = new ArrayList<>();
        arrayListNameStorage = new ArrayList<>();
        idArrayListStorage = new ArrayList<>();
        arrayListDataStorage = new ArrayList<>();
        arrayListDurationStorage = new ArrayList<>();

        getMusic();
        // ListAdapter adapter = new ListAdapter();

        recyclerView = dialog.findViewById(R.id.home_dialog_recycler);
        recyclerView.setHasFixedSize(true);
        Adapter = new HomeDialogRecyclerAdapter(activity, arrayListName, arrayListArtist, idArrayList, arrayListData, arrayListDuration);

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
    }

    public void delete(int position) {
        String name, artist, albumArt, data, time;
        name = String.valueOf(SONG_NAME.get(position));
        artist = String.valueOf(ARTIST_NAME.get(position));
        albumArt = String.valueOf(ALBUM_ART.get(position));
        data = String.valueOf(DATA.get(position));
        time = String.valueOf(TIME.get(position));

        arrayListNameStorage.add(name);
        arrayListArtistStorage.add(artist);
        idArrayListStorage.add(Integer.valueOf(albumArt));
        arrayListDataStorage.add(data);
        arrayListDurationStorage.add(time);

        SONG_NAME.remove(position);
        ALBUM_ART.remove(position);
        ARTIST_NAME.remove(position);
        DATA.remove(position);
        TIME.remove(position);

        Adapter.notifyItemRemoved(position);
    }

    public void SQLiteAddPlaylist() {
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.example.music/databases/Playlists.db", new SQLiteDatabase.CursorFactory() {
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
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.example.music/databases/PlaylistSongs.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                //Assign the values of masterQuery,query,editTable as per your requirements
                return new SQLiteCursor(masterQuery, editTable, query);
            }
        }, null);


        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + NAME + " (" + Table_Song_ID + " INTEGER PRIMARY KEY, " + Table_Song_Name + " VARCHAR, " + Table_Song_Artist + " VARCHAR, " + Table_Song_SongID + " INTEGER, " + Table_Song_SongDATA + " VARCHAR, " + Table_Song_SongDuration + " VARCHAR)");

        for (int i = 0; i < arrayListNameStorage.size(); i++) {
            String name, artist, data, time;
            int id;

            name = arrayListNameStorage.get(i);
            artist = arrayListArtistStorage.get(i);
            data = arrayListDataStorage.get(i);
            time = arrayListDurationStorage.get(i);

            id = idArrayListStorage.get(i);

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
