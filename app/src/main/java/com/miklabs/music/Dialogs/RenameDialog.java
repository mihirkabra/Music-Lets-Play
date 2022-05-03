package com.miklabs.music.Dialogs;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static com.miklabs.music.FragmentClasses.Playlists.PlaylistName;
import static com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist.PLAYLIST_TABLE_NAME;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.miklabs.music.FragmentClasses.Playlists;
import com.miklabs.music.MainActivity;
import com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist;
import com.miklabs.music.PlaylistClasses.SQLiteHelperSongs;
import com.miklabs.music.R;

public class RenameDialog extends Dialog {

    Activity activity;
    int POSITION;

    Dialog dialog;

    TextView title, close;
    EditText rename;
    Button button;
    RelativeLayout layout;

    String playlistName, NAME, F_Result = "Not_Found";

    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;
    SQLiteHelperPlaylist sqLiteHelperPlaylist;
    SQLiteHelperSongs sqLiteHelperSongs;
    Cursor cursor;

    MainActivity mainActivity;

    public RenameDialog(Activity activity, int POSITION) {
        super(activity);
        this.activity = activity;
        this.POSITION = POSITION;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_rename);

        mainActivity = new MainActivity();

        title = dialog.findViewById(R.id.dialogRename_text);
        close = dialog.findViewById(R.id.dialogRename_close);
        layout = dialog.findViewById(R.id.dialogRename_layout);
        rename = dialog.findViewById(R.id.dialogRename_edittext);
        button = dialog.findViewById(R.id.dialogRename_botton);

        playlistName = PlaylistName.get(POSITION);
        rename.setText(playlistName);
        rename.requestFocus();

        sqLiteHelperPlaylist = new SQLiteHelperPlaylist(activity);
        sqLiteHelperSongs = new SQLiteHelperSongs(activity);


        close.setOnClickListener(v -> dialog.dismiss());

        button.setOnClickListener(v -> {
            NAME = rename.getText().toString();
            if (NAME.equals("")) {
                mainActivity.getSnackBar(activity, layout, "Please Give a Name to the Playlist", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                rename.requestFocus();
            } else {
                PlaylistAlreadyExistsOrNot();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

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
            mainActivity.getSnackBar(activity, layout, "Playlist with same name already exists. Please try a new Name!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
            rename.requestFocus();

        } else {
            RenamePlaylist();
            RenameSongsTable();

            dialog.dismiss();
            mainActivity.getSnackBar(activity, MainActivity.layout, "Playlist Renamed successfully!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
            Playlists.adapter.notifyDataSetChanged();
        }

        F_Result = "Not_Found";

    }

    public void RenamePlaylist() {
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.miklabs.music/databases/Playlists.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                //Assign the values of masterQuery,query,editTable as per your requirements
                return new SQLiteCursor(masterQuery, editTable, query);
            }
        }, null);


        //SQLiteDataBaseQueryHolder = "INSERT INTO " + PLAYLIST_TABLE_NAME + " (PlaylistName) VALUES('" + NAME + "');";
        SQLiteDataBaseQueryHolder = "UPDATE " + PLAYLIST_TABLE_NAME + " SET PlaylistName = '" + NAME + "' where PlaylistName = '" + playlistName + "'";

        sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
        sqLiteDatabaseObj.close();
        PlaylistName.remove(POSITION);
        PlaylistName.add(POSITION, NAME);
    }

    public void RenameSongsTable() {
        sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.miklabs.music/databases/PlaylistSongs.db", new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                //Assign the values of masterQuery,query,editTable as per your requirements
                return new SQLiteCursor(masterQuery, editTable, query);
            }
        }, null);


        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + playlistName + " (" + Table_Song_ID + " INTEGER PRIMARY KEY, " + Table_Song_Name + " VARCHAR, " + Table_Song_Artist + " VARCHAR, " + Table_Song_SongID + " INTEGER, " + Table_Song_SongDATA + " VARCHAR, " + Table_Song_SongDuration + " VARCHAR)");

        sqLiteDatabaseObj.execSQL("ALTER TABLE " + playlistName + " RENAME TO " + NAME);

        sqLiteDatabaseObj.close();
    }
}
