package com.miklabs.music.Dialogs;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static com.miklabs.music.FragmentClasses.Playlists.PlaylistName;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.miklabs.music.MainActivity;
import com.miklabs.music.PlaylistClasses.SQLiteHelperSongs;
import com.miklabs.music.R;
import com.miklabs.music.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AddSongs extends Dialog {
    Activity activity;
    Dialog dialog;
    int playlistPOSITION;

    MainActivity mainActivity;

    RecyclerView dialogAddRecycler;
    TextView title;
    Button button;
    RelativeLayout layout;

    String playlistName;

    AddSongsAdapter Adapter;

    ArrayList<String> arrayListName, arrayListArtist, arrayListDuration, arrayListData;
    ArrayList<Integer> idArrayList;

    ArrayList<String> arrayListNameStorage, arrayListArtistStorage, arrayListDataStorage, arrayListDurationStorage;
    ArrayList<Integer> idArrayListStorage;

    boolean recyclerClicked = false;

    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;
    SQLiteHelperSongs sqLiteHelperSongs;
    Cursor cursor;

    String F_Result = "Not_Found";

    public AddSongs(Activity activity, int playlistPOSITION) {
        super(activity);
        this.activity = activity;
        this.playlistPOSITION = playlistPOSITION;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_songs);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mainActivity = new MainActivity();

        sqLiteHelperSongs = new SQLiteHelperSongs(activity);

        playlistName = PlaylistName.get(playlistPOSITION);

        doStuff();

        title = dialog.findViewById(R.id.dialogAddSongs_text);

        button = dialog.findViewById(R.id.dialogAddSongs_create);

        layout = dialog.findViewById(R.id.dialogAddSongs_layout);

        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }

    public void doStuff() {
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

        dialogAddRecycler = dialog.findViewById(R.id.dialogAddSongs_recycler);
        dialogAddRecycler.setHasFixedSize(true);
        Adapter = new AddSongsAdapter(activity, arrayListName, arrayListArtist, idArrayList, arrayListData, arrayListDuration);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        dialogAddRecycler.setLayoutManager(layoutManager);
        dialogAddRecycler.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();
        //songCheck();


        dialogAddRecycler.addOnItemTouchListener(new RecyclerItemClickListener(activity, dialogAddRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                recyclerClicked = true;
                SongCheck(position);
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

    public void SongCheck(int position) {
        Adapter.notifyDataSetChanged();

        String name, artist, albumArt, data, time;
        name = String.valueOf(arrayListName.get(position));
        artist = String.valueOf(arrayListArtist.get(position));
        albumArt = String.valueOf(idArrayList.get(position));
        data = String.valueOf(arrayListData.get(position));
        time = String.valueOf(arrayListDuration.get(position));

        SongAlreadyExistsOrNot(name, artist, data, time, Integer.parseInt(albumArt), position);
    }


    public void SQLiteAddSongs(String name, String artist, String data, String time, int id) {
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.miklabs.music/databases/PlaylistSongs.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                //Assign the values of masterQuery,query,editTable as per your requirements
                return new SQLiteCursor(masterQuery, editTable, query);
            }
        }, null);


        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + playlistName + " (" + Table_Song_ID + " INTEGER PRIMARY KEY, " + Table_Song_Name + " VARCHAR, " + Table_Song_Artist + " VARCHAR, " + Table_Song_SongID + " INTEGER, " + Table_Song_SongDATA + " VARCHAR, " + Table_Song_SongDuration + " VARCHAR)");

        SQLiteDataBaseQueryHolder = "INSERT INTO " + playlistName + " (SongId,SongName,SongArtist,SongData,SongDuration) VALUES('" + id + "', '" + name + "', '" + artist + "', '" + data + "', '" + time + "');";
        sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

        sqLiteDatabaseObj.close();
    }

    public void SongAlreadyExistsOrNot(String name, String artist, String data, String time, int id, int position) {
        sqLiteDatabaseObj = sqLiteHelperSongs.getWritableDatabase();

        cursor = sqLiteDatabaseObj.query(playlistName, null, "  SongData =?", new String[]{data}, null, null, null);

        while (cursor.moveToNext()) {

            if (cursor.isFirst()) {

                cursor.moveToFirst();
                F_Result = "Found";
                cursor.close();
            }
        }
        CheckFinalResult(name, artist, data, time, id, position);

    }


    public void CheckFinalResult(String name, String artist, String data, String time, int id, int position) {

        if (F_Result.equalsIgnoreCase("Found")) {

            mainActivity.getSnackBar(activity, layout, "Song already exists!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_SHORT);

        } else {
            arrayListName.remove(position);
            arrayListArtist.remove(position);
            arrayListDuration.remove(position);
            arrayListData.remove(position);
            idArrayList.remove(position);
            Adapter.notifyItemRemoved(position);
            SQLiteAddSongs(name, artist, data, time, id);
            mainActivity.getSnackBar(activity, layout, "Song added successfully!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_SHORT);
        }

        F_Result = "Not_Found";

    }


}
