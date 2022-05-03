package com.miklabs.music.PlaylistClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteHelperSongs extends SQLiteOpenHelper {

    public static final String SONGS_TABLE_NAME = "PlaylistName";
    public static final String Table_Song_ID = "id";
    public static final String Table_Song_Name = "SongName";
    public static final String Table_Song_Artist = "SongArtist";
    public static final String Table_Song_SongID = "SongId";
    public static final String Table_Song_SongDATA = "SongData";
    public static final String Table_Song_SongDuration = "SongDuration";
    static String DATABASE_NAME = "PlaylistSongs.db";


    public SQLiteHelperSongs(Context context) {

        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + SONGS_TABLE_NAME + " (" + Table_Song_ID + " INTEGER PRIMARY KEY, " + Table_Song_Name + " VARCHAR, " + Table_Song_Artist + " VARCHAR, " + Table_Song_SongID + " INTEGER, " + Table_Song_SongDATA + " VARCHAR, " + Table_Song_SongDuration + " VARCHAR)";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SONGS_TABLE_NAME);
        onCreate(db);

    }

    public ArrayList<String> getSongNames(String PlaylistNameForSongs) {
        ArrayList<String> SongName = new ArrayList<>();

        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT " + Table_Song_Name + " FROM " + PlaylistNameForSongs, null);

        if (mCursor.moveToFirst()) {
            do {
                String s = mCursor.getString(0);
                SongName.add(s);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return SongName;

    }

    public ArrayList<String> getArtistNames(String PlaylistNameForSongs) {
        ArrayList<String> ArtistName = new ArrayList<>();

        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT " + Table_Song_Artist + " FROM " + PlaylistNameForSongs, null);

        if (mCursor.moveToFirst()) {
            do {
                String s = mCursor.getString(0);
                ArtistName.add(s);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return ArtistName;

    }

    public ArrayList<String> getSongData(String PlaylistNameForSongs) {
        ArrayList<String> SongData = new ArrayList<>();

        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT " + Table_Song_SongDATA + " FROM " + PlaylistNameForSongs, null);

        if (mCursor.moveToFirst()) {
            do {
                String s = mCursor.getString(0);
                SongData.add(s);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return SongData;

    }

    public ArrayList<Integer> getSongId(String PlaylistNameForSongs) {
        ArrayList<Integer> SongIDs = new ArrayList<>();

        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT " + Table_Song_SongID + " FROM " + PlaylistNameForSongs, null);

        if (mCursor.moveToFirst()) {
            do {
                String s = mCursor.getString(0);
                SongIDs.add(Integer.parseInt(s));
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return SongIDs;

    }

    public ArrayList<String> getSongDuration(String PlaylistNameForSongs) {
        ArrayList<String> duration = new ArrayList<>();

        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT " + Table_Song_SongDuration + " FROM " + PlaylistNameForSongs, null);

        if (mCursor.moveToFirst()) {
            do {
                String s = mCursor.getString(0);
                duration.add(s);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return duration;

    }

}

