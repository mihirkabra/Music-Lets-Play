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
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.miklabs.music.FragmentClasses.ContentHome;
import com.miklabs.music.MainActivity;
import com.miklabs.music.PlaylistClasses.SQLiteHelperSongs;
import com.miklabs.music.R;
import com.miklabs.music.RecyclerItemClickListener;

public class AddToPlaylistDialog extends Dialog {

    Activity activity;
    Dialog dialog;

    String F_Result = "Not_Found";
    String name, artist, data, time;
    String SQLiteDataBaseQueryHolder;
    String playlistName;
    int id;

    RecyclerView dialogAddRecycler;
    TextView title;
    Button button;
    RelativeLayout layout;

    SQLiteHelperSongs sqLiteHelperSongs;
    SQLiteDatabase sqLiteDatabaseObj;
    Cursor cursor;

    AddToPlaylistAdapter adapter;

    MainActivity mainActivity;
    int POSITION;


    public AddToPlaylistDialog(Activity activity, int POSITION) {
        super(activity);
        this.activity = activity;
        this.POSITION = POSITION;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add);

        mainActivity = new MainActivity();
        sqLiteHelperSongs = new SQLiteHelperSongs(activity);

        name = ContentHome.arrayListName.get(POSITION);
        artist = ContentHome.arrayListArtist.get(POSITION);
        data = ContentHome.arrayListData.get(POSITION);
        time = ContentHome.arrayListDuration.get(POSITION);

        id = ContentHome.idArrayList.get(POSITION);

        dialogAddRecycler = dialog.findViewById(R.id.dialogAdd_recycler);
        adapter = new AddToPlaylistAdapter(getContext(), PlaylistName);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        dialogAddRecycler.setLayoutManager(layoutManager);
        dialogAddRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dialogAddRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), dialogAddRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playlistName = PlaylistName.get(position);
                songCheck();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        title = dialog.findViewById(R.id.dialogAdd_text);
        title.setText("Select the Playlist");


        button = dialog.findViewById(R.id.dialogAdd_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        layout = dialog.findViewById(R.id.dialogAdd_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    public void SQLiteAddSongs() {
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

    public void songCheck() {
        sqLiteDatabaseObj = sqLiteHelperSongs.getWritableDatabase();

        cursor = sqLiteDatabaseObj.query(playlistName, null, "  SongName =?", new String[]{name}, null, null, null);

        while (cursor.moveToNext()) {

            if (cursor.isFirst()) {

                cursor.moveToFirst();
                F_Result = "SongFound";
                cursor.close();
            }
        }
        finalCheck();

    }

    public void finalCheck() {
        if (F_Result.equalsIgnoreCase("SongFound")) {
            mainActivity.getSnackBar(activity, layout, "Song already Exists in Playlist:" + playlistName + "", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);

        } else {
            SQLiteAddSongs();
            dialog.dismiss();
            mainActivity.getSnackBar(activity, MainActivity.layout, "Song added to Playlist:" + playlistName + " Successfully", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);

        }

        F_Result = "Not_Found";
    }

}
