package com.miklabs.music.FragmentClasses;

import static com.miklabs.music.FragmentClasses.ContentHome.collection;
import static com.miklabs.music.MainActivity.PlaylistNameForSongs;
import static com.miklabs.music.MainActivity.editor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.miklabs.music.Dialogs.AddSongs;
import com.miklabs.music.Dialogs.DeleteDialog;
import com.miklabs.music.Dialogs.EmptyDialog;
import com.miklabs.music.Dialogs.RenameDialog;
import com.miklabs.music.MainActivity;
import com.miklabs.music.MusicPlayer;
import com.miklabs.music.PlaylistClasses.PlaylistAdapter;
import com.miklabs.music.PlaylistClasses.SQLiteHelperPlaylist;
import com.miklabs.music.PlaylistClasses.SQLiteHelperSongs;
import com.miklabs.music.R;
import com.miklabs.music.RecyclerItemClickListener;
import com.miklabs.music.Songs;

import java.util.ArrayList;

public class Playlists extends Fragment {

    public static ArrayList<String> PlaylistName = new ArrayList<>();
    public static PlaylistAdapter adapter;
    RecyclerView playlistRecyclerView;
    SQLiteHelperPlaylist sqLiteHelperPlaylist;
    SQLiteHelperSongs sqLiteHelperSongs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_layout, container, false);

        sqLiteHelperPlaylist = new SQLiteHelperPlaylist(getContext());
        sqLiteHelperSongs = new SQLiteHelperSongs(getContext());
        getData();

        playlistRecyclerView = (RecyclerView) view.findViewById(R.id.playlistRecyclerView);
        playlistRecyclerView.setHasFixedSize(true);
        adapter = new PlaylistAdapter(getContext(), PlaylistName);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        playlistRecyclerView.setLayoutManager(layoutManager);
        playlistRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        playlistRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), playlistRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                PlaylistNameForSongs = PlaylistName.get(position);
                //((MainActivity)getActivity()).setViewPager(4);
                Intent intent = new Intent(getActivity(), Songs.class);
                intent.putExtra("Playlist Position", position);
                startActivity(intent);
                editor.putInt("fragmentNo", 1);
                editor.apply();
            }

            @Override
            public void onLongItemClick(View view, final int position) {

                PlaylistNameForSongs = PlaylistName.get(position);

                PopupMenu popup = new PopupMenu(getContext(), view);

                popup.inflate(R.menu.playlist_more_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.playlist_more_menu_play:

                                ArrayList DataList, NameList, IdList, ArtistList;
                                DataList = sqLiteHelperSongs.getSongData(PlaylistNameForSongs);
                                NameList = sqLiteHelperSongs.getSongNames(PlaylistNameForSongs);
                                IdList = sqLiteHelperSongs.getSongId(PlaylistNameForSongs);
                                ArtistList = sqLiteHelperSongs.getArtistNames(PlaylistNameForSongs);

                                if (NameList.size() == 0) {
                                    MainActivity m = new MainActivity();
                                    m.getSnackBar(getActivity(), MainActivity.layout, "The Playlist: " + PlaylistNameForSongs + " is Empty!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                                } else {
                                    ContentHome.goingToMusic = true;

                                    SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(getActivity());

                                    collection = db.edit();

                                    Gson gson0 = new Gson();
                                    String songs = gson0.toJson(DataList);

                                    Gson gson1 = new Gson();
                                    String songname = gson1.toJson(NameList);

                                    Gson gson2 = new Gson();
                                    String ID = gson2.toJson(IdList);

                                    Gson gson3 = new Gson();
                                    String artist = gson3.toJson(ArtistList);

                                    collection.putString("songs", songs);
                                    collection.putString("songname", songname);
                                    collection.putString("artist", artist);
                                    collection.putString("id", ID);
                                    collection.putInt("pos", 0);
                                    collection.apply();
                                    startActivity(new Intent(getContext(), MusicPlayer.class));
                                }

                                return true;

                            case R.id.playlist_more_menu_add:
                                AddSongs addSongsDialog = new AddSongs(getActivity(), position);
                                addSongsDialog.showDialog();
                                return true;

                            case R.id.playlist_more_menu_rename:
                                RenameDialog renameDialog = new RenameDialog(getActivity(), position);
                                renameDialog.showDialog();
                                return true;

                            case R.id.playlist_more_menu_delete:
                                DeleteDialog d = new DeleteDialog(getActivity(), position);
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


        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Code executes EVERY TIME user views the fragment

        if (isVisibleToUser) {
            if (PlaylistName.size() == 0) {
                EmptyDialog d = new EmptyDialog(getActivity(), "No Playlist is yet created!");
                d.showDialog();
            }
        }
    }

    public void getData() {
        PlaylistName = sqLiteHelperPlaylist.getPlaylistName();
    }
}
