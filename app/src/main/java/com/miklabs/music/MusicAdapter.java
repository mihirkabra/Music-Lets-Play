package com.miklabs.music;

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

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<SongsModel> songs;

    public MusicAdapter(Context mContext, ArrayList<SongsModel> songs) {
        this.mContext = mContext;
        this.songs = songs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String songname, artistname, du;

        songname = songs.get(position).getSongName();
        artistname = songs.get(position).getArtistName();
        du = songs.get(position).getDuration();

        loadAlbumArt(songs.get(position).getAlbumArt(), holder.ALBUMart);
        holder.SONGname.setText(songname);
        holder.ARTISTname.setText(artistname);
        holder.DURATION.setText(du);

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
        public TextView SONGname, ARTISTname, DURATION;
        public ImageView ALBUMart;
        public CardView cardView;

        public MyViewHolder(final View view) {
            super(view);
            DURATION = view.findViewById(R.id.layout_duration);
            SONGname = view.findViewById(R.id.layout_name);
            ARTISTname = view.findViewById(R.id.layout_artist);
            ALBUMart = view.findViewById(R.id.layout_image);
            cardView = view.findViewById(R.id.musicListCard);

        }
    }
}
