package com.miklabs.music.PlaylistClasses;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.miklabs.music.R;
import com.miklabs.music.SongsModel;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    ArrayList<SongsModel> songs;
    Context context;

    public SongsAdapter(Context context, ArrayList<SongsModel> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_list_layout, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String songname, artistname, du;

        songname = songs.get(position).getSongName();
        artistname = songs.get(position).getArtistName();
        du = songs.get(position).getDuration();

        loadAlbumArt(songs.get(position).getAlbumArt(), holder.albumArt);
        holder.songName.setText(songname);
        holder.artistName.setText(artistname);
        holder.time.setText(du);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void loadAlbumArt(int albumId, ImageView view) {
        Drawable mDefaultBackground = view.getResources().getDrawable(R.drawable.clipart);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        //Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(view.getContext()).load(path).override(250, 250).error(mDefaultBackground).into(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView artistName, songName, time;
        public ImageView albumArt;
        public CardView cardView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            artistName = view.findViewById(R.id.layout_artist);
            songName = view.findViewById(R.id.layout_name);
            time = view.findViewById(R.id.layout_duration);
            cardView = view.findViewById(R.id.musicListCard);
            albumArt = view.findViewById(R.id.layout_image);
        }
    }
}
