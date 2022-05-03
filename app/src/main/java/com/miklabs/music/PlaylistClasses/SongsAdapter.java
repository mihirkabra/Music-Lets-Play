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

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    ArrayList ID;
    ArrayList NAME;
    ArrayList ARTIST;
    ArrayList DATA;
    ArrayList SONG_FRAGMENT_DURATION;
    Context context;

    public SongsAdapter(ArrayList ID, ArrayList NAME, ArrayList ARTIST, ArrayList DATA, ArrayList SONG_FRAGMENT_DURATION, Context context) {
        this.ID = ID;
        this.NAME = NAME;
        this.ARTIST = ARTIST;
        this.DATA = DATA;
        this.context = context;
        this.SONG_FRAGMENT_DURATION = SONG_FRAGMENT_DURATION;
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

        songname = NAME.get(position).toString();
        artistname = ARTIST.get(position).toString();
        du = SONG_FRAGMENT_DURATION.get(position).toString();

        loadAlbumArt((Integer) ID.get(position), holder.albumArt);
        holder.songName.setText(songname);
        holder.artistName.setText(artistname);
        holder.time.setText(du);
    }

    @Override
    public int getItemCount() {
        return ID.size();
    }

    public void loadAlbumArt(int albumId, ImageView view) {
        Drawable mDefaultBackground = view.getResources().getDrawable(R.drawable.clipart);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        //Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(view.getContext()).load(path).override(250, 250).error(mDefaultBackground).into(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView artistName, songName, time;
        public ImageView albumArt;
        public CardView cardView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            artistName = (TextView) view.findViewById(R.id.layout_artist);
            songName = (TextView) view.findViewById(R.id.layout_name);
            time = (TextView) view.findViewById(R.id.layout_duration);
            cardView = (CardView) view.findViewById(R.id.musicListCard);
            albumArt = (ImageView) view.findViewById(R.id.layout_image);
        }
    }
}
