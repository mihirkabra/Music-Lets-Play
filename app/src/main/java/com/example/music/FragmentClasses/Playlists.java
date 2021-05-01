package com.example.music.FragmentClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.music.Dialogs.AddSongs;
import com.example.music.Dialogs.DeleteDialog;
import com.example.music.Dialogs.EmptyDialog;
import com.example.music.Dialogs.RenameDialog;
import com.example.music.MainActivity;
import com.example.music.MusicPlayer;
import com.example.music.PlaylistClasses.PlaylistAdapter;
import com.example.music.PlaylistClasses.SQLiteHelperPlaylist;
import com.example.music.PlaylistClasses.SQLiteHelperSongs;
import com.example.music.R;
import com.example.music.RecyclerItemClickListener;
import com.example.music.Songs;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.music.FragmentClasses.ContentHome.collection;
import static com.example.music.MainActivity.PlaylistNameForSongs;
import static com.example.music.MainActivity.editor;

public class Playlists extends Fragment {

    RecyclerView playlistRecyclerView;
    public static ArrayList<String> PlaylistName = new ArrayList<>();
    public static PlaylistAdapter adapter;

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


                PlaylistNameForSongs=PlaylistName.get(position);
                //((MainActivity)getActivity()).setViewPager(4);
                Intent intent = new Intent(getActivity(), Songs.class);
                intent.putExtra("Playlist Position", position);
                startActivity(intent);
                editor.putInt("fragmentNo", 1);
                editor.apply();
            }

            @Override
            public void onLongItemClick(View view, final int position) {

                PlaylistNameForSongs=PlaylistName.get(position);

                PopupMenu popup = new PopupMenu(getContext(),view);

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

                                if(NameList.size()==0)
                                {
                                    MainActivity m=new MainActivity();
                                    m.getSnackBar(getActivity(), MainActivity.layout, "The Playlist: "+PlaylistNameForSongs+" is Empty!", R.color.whiteColor, R.color.colorPrimaryDark, Snackbar.LENGTH_LONG);
                                }
                                else {
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
            if(PlaylistName.size()==0)
            {
                EmptyDialog d = new EmptyDialog(getActivity(),"No Playlist is yet created!");
                d.showDialog();
            }
        }
    }

    public void getData() {
        PlaylistName = sqLiteHelperPlaylist.getPlaylistName();
    }
}
