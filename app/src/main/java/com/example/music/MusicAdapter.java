package com.example.music;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    static Context mContext;
    private ArrayList SONG_NAME, ARTIST_NAME, ALBUM_ART, DU;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView SONGname, ARTISTname, DURATION;
        public ImageView ALBUMart;
        public CardView cardView;

        public MyViewHolder(final View view) {
            super(view);
            DURATION = (TextView) view.findViewById(R.id.layout_duration);
            SONGname = (TextView) view.findViewById(R.id.layout_name);
            ARTISTname = (TextView) view.findViewById(R.id.layout_artist);
            ALBUMart = (ImageView) view.findViewById(R.id.layout_image);
            cardView = (CardView) view.findViewById(R.id.musicListCard);

        }
    }


    public MusicAdapter(Context mContext, ArrayList SONG_NAME, ArrayList ARTIST_NAME, ArrayList ALBUM_ART, ArrayList DU) {
        this.mContext = mContext;
        this.SONG_NAME = SONG_NAME;
        this.ARTIST_NAME = ARTIST_NAME;
        this.ALBUM_ART = ALBUM_ART;
        this.DU = DU;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //Album album = albumList.get(position);
        String songname, artistname, du;

        songname = SONG_NAME.get(position).toString();
        artistname = ARTIST_NAME.get(position).toString();
        du = DU.get(position).toString();

        loadAlbumArt((Integer) ALBUM_ART.get(position), holder.ALBUMart);
        holder.SONGname.setText(songname);
        holder.ARTISTname.setText(artistname);
        holder.DURATION.setText(du);

    }

    @Override
    public int getItemCount() {
        return ALBUM_ART.size();
    }

    public void loadAlbumArt(int albumId, ImageView view) {
        Drawable mDefaultBackground = view.getResources().getDrawable(R.drawable.clipart);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        //Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(view.getContext()).load(path).override(250, 250).error(mDefaultBackground).into(view);
    }
}
