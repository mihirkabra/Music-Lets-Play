package com.example.music.PlaylistClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteHelperPlaylist extends SQLiteOpenHelper {

    public static String DATABASE_NAME="Playlists.db";

    public static final String PLAYLIST_TABLE_NAME ="Playlist";

    public static final String Table_Playlist_ID="id";

    public static final String Table_Playlist_Name="PlaylistName";

    public SQLiteHelperPlaylist(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+ PLAYLIST_TABLE_NAME +" ("+Table_Playlist_ID+" INTEGER PRIMARY KEY, "+Table_Playlist_Name+" VARCHAR)";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ PLAYLIST_TABLE_NAME);
        onCreate(db);

    }
    public ArrayList<String> getPlaylistName()
    {
        ArrayList<String> name = new ArrayList<>();

        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT PlaylistName FROM " + PLAYLIST_TABLE_NAME, null);

        if(mCursor.moveToFirst()) {
            do {
                String s = mCursor.getString(0);
                name.add(s);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return name;

    }
}

