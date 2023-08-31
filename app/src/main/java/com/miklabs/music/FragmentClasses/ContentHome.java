package com.miklabs.music.FragmentClasses;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.miklabs.music.Dialogs.AddToPlaylistDialog;
import com.miklabs.music.MainActivity;
import com.miklabs.music.MusicAdapter;
import com.miklabs.music.MusicPlayer;
import com.miklabs.music.R;
import com.miklabs.music.RecyclerItemClickListener;
import com.miklabs.music.SongsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContentHome extends Fragment {
    public static SharedPreferences.Editor collection;
    public static boolean goingToMusic;
    public static ArrayList<SongsModel> songs;
    SongsModel songsModel;
    RecyclerView recyclerView;
    MusicAdapter Adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home, container, false);

        requestPermissions(view);

        return view;
    }

    private void requestPermissions(final View v) {
        String[] requiredPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.FOREGROUND_SERVICE
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requiredPermissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.FOREGROUND_SERVICE
            };
        } else {
            requiredPermissions = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }
        Dexter.withActivity(getActivity())
                .withPermissions(requiredPermissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            doStuff(v);
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> {
                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread().check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permissions Needed");

        builder.setMessage("This app needs all the required permissions for smooth functioning." +
                "\n\n1. Notification Permission: for the Music Control in the notification panel." +
                "\n\n2. Phone Permission: to Play/Pause Music while calling." +
                "\n\n3. Storage Permission: to get all the Music files and their Cover Images." +
                "\n\nYou can grant them in app settings.");
        builder.setPositiveButton("Goto Settings", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }

    public void doStuff(View v) {

        songs = new ArrayList<>();

        getMusic();

        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        Adapter = new MusicAdapter(getActivity(), songs);

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

                Gson songsGson = new Gson();
                String songs = songsGson.toJson(ContentHome.songs);

                collection.putString("songs", songs);
                collection.putInt("pos", position);
                collection.apply();
                startActivity(new Intent(getContext(), MusicPlayer.class));
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                PopupMenu popup = new PopupMenu(getContext(), view);

                popup.inflate(R.menu.music_more_menu);

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.music_more_menu_play:

                            MainActivity.PlaylistNameForSongs = "PlaylistName";

                            goingToMusic = true;

                            SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getActivity());

                            collection = db.edit();

                            Gson gson0 = new Gson();
                            String songs = gson0.toJson(ContentHome.songs);

                            collection.putString("songs", songs);
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
                });

                popup.show();
            }
        }));
    }

    public void getMusic() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
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
                int currentDuration = songCurosr.getInt(songDuration);
                String currentData = songCurosr.getString(songData);

                songsModel = new SongsModel(
                        currentTitle,
                        currentArtist,
                        currentID,
                        "• " + String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) currentDuration),
                                TimeUnit.MILLISECONDS.toSeconds((long) currentDuration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                currentDuration))),
                        currentData
                );

                songs.add(songsModel);

            } while (songCurosr.moveToNext());
        }
    }
}
