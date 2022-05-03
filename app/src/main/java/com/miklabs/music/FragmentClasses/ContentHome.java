package com.miklabs.music.FragmentClasses;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.miklabs.music.Dialogs.AddToPlaylistDialog;
import com.miklabs.music.MainActivity;
import com.miklabs.music.MusicAdapter;
import com.miklabs.music.MusicPlayer;
import com.miklabs.music.R;
import com.miklabs.music.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ContentHome extends Fragment {
    public static SharedPreferences.Editor collection;
    public static boolean goingToMusic;
    public static ArrayList<String> arrayListName, arrayListArtist, arrayListDuration, arrayListData;
    public static ArrayList<Integer> idArrayList;
    RecyclerView recyclerView;
    MusicAdapter Adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home, container, false);


        requestPermission(view);


        return view;
    }


    public void requestPermission(final View v) {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //display();
                        doStuff(v);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }


    public void doStuff(View v) {
        //musicListView = (ListView) findViewById(R.id.musicList);
        arrayListName = new ArrayList<>();
        arrayListArtist = new ArrayList<>();
        arrayListDuration = new ArrayList<>();
        arrayListData = new ArrayList<>();
        idArrayList = new ArrayList<>();

        getMusic();
        // ListAdapter adapter = new ListAdapter();

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        Adapter = new MusicAdapter(getActivity(), arrayListName, arrayListArtist, idArrayList, arrayListDuration);


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                MainActivity.PlaylistNameForSongs = "PlaylistName";

                goingToMusic = true;

                SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getActivity());

                collection = db.edit();

                Gson gson0 = new Gson();
                String songs = gson0.toJson(arrayListData);

                Gson gson1 = new Gson();
                String songname = gson1.toJson(arrayListName);

                Gson gson2 = new Gson();
                String ID = gson2.toJson(idArrayList);

                Gson gson3 = new Gson();
                String artist = gson3.toJson(arrayListArtist);

                collection.putString("songs", songs);
                collection.putString("songname", songname);
                collection.putString("artist", artist);
                collection.putString("id", ID);
                collection.putInt("pos", position);
                collection.apply();
                startActivity(new Intent(getContext(), MusicPlayer.class));
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                MusicAdapter.MyViewHolder holder = new MusicAdapter.MyViewHolder(view);
                PopupMenu popup = new PopupMenu(getContext(), view);

                popup.inflate(R.menu.music_more_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.music_more_menu_play:

                                MainActivity.PlaylistNameForSongs = "PlaylistName";

                                goingToMusic = true;

                                SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getActivity());

                                collection = db.edit();

                                Gson gson0 = new Gson();
                                String songs = gson0.toJson(arrayListData);

                                Gson gson1 = new Gson();
                                String songname = gson1.toJson(arrayListName);

                                Gson gson2 = new Gson();
                                String ID = gson2.toJson(idArrayList);

                                Gson gson3 = new Gson();
                                String artist = gson3.toJson(arrayListArtist);

                                collection.putString("songs", songs);
                                collection.putString("songname", songname);
                                collection.putString("artist", artist);
                                collection.putString("id", ID);
                                collection.putInt("pos", position);
                                collection.apply();
                                startActivity(new Intent(getContext(), MusicPlayer.class));

                                return true;

                            case R.id.music_more_menu_add:
                                AddToPlaylistDialog d = new AddToPlaylistDialog(getActivity(), position);
                                d.showDialog();
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                popup.show();
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
        ContentResolver contentResolver = getActivity().getContentResolver();
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


}
