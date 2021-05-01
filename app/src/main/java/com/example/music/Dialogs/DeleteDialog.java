package com.example.music.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.FragmentClasses.Playlists;
import com.example.music.MainActivity;
import com.example.music.PlaylistClasses.SQLiteHelperPlaylist;
import com.example.music.PlaylistClasses.SQLiteHelperSongs;
import com.example.music.R;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DeleteDialog extends Dialog {

    Activity activity;
    Dialog dialog;

    int posOfPlaylist;
    String name;

    ImageView image;
    TextView text, text_NO;
    Button button_YES;

    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;
    SQLiteHelperPlaylist sqLiteHelperPlaylist;
    SQLiteHelperSongs sqLiteHelperSongs;

    MainActivity mainActivity;

    public DeleteDialog(Activity activity, int posOfPlaylist) {
        super(activity);
        this.activity = activity;
        this.posOfPlaylist = posOfPlaylist;
    }

    public void showDialog()
    {
        dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_simple);

        mainActivity = new MainActivity();

        text = (TextView) dialog.findViewById(R.id.dialogSimple_text);
        text_NO = (TextView)dialog.findViewById(R.id.dialogSimple_NO);

        image = (ImageView) dialog.findViewById(R.id.dialogSimple_image);

        button_YES = (Button) dialog.findViewById(R.id.dialogSimple_YES);

        name = Playlists.PlaylistName.get(posOfPlaylist);

        text.setText("Are you sure you want to Delete the Playlist: "+name+" ?");
        image.setVisibility(View.GONE);

        sqLiteHelperPlaylist = new SQLiteHelperPlaylist(activity);
        sqLiteHelperSongs = new SQLiteHelperSongs(activity);

        text_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        button_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.example.music/databases/PlaylistSongs.db", new SQLiteDatabase.CursorFactory() {
                    @Override
                    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                        //Assign the values of masterQuery,query,editTable as per your requirements
                        return new SQLiteCursor(masterQuery,editTable,query);
                    }
                }, null);

                sqLiteDatabaseObj.execSQL("delete from "+ name);
                sqLiteDatabaseObj.close();

                sqLiteDatabaseObj = openOrCreateDatabase("/data/data/com.example.music/databases/Playlists.db", new SQLiteDatabase.CursorFactory() {
                    @Override
                    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                        //Assign the values of masterQuery,query,editTable as per your requirements
                        return new SQLiteCursor(masterQuery,editTable,query);
                    }
                }, null);


                SQLiteDataBaseQueryHolder="delete from Playlist where PlaylistName ='"+name+"'";
                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
                sqLiteDatabaseObj.close();

                Playlists.PlaylistName.remove(posOfPlaylist);

                Playlists.adapter.notifyDataSetChanged();

                dialog.dismiss();

                mainActivity.getSnackBar(activity, MainActivity.layout, "Playlist: "+name+" Successfully Deleted!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}
