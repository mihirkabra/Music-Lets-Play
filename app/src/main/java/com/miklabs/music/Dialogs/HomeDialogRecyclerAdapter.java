package com.miklabs.music.Dialogs;

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


public class HomeDialogRecyclerAdapter extends RecyclerView.Adapter<HomeDialogRecyclerAdapter.MyViewHolder> {


    public static ArrayList SONG_NAME, ARTIST_NAME, ALBUM_ART, DATA, TIME;
    //#9900FF2A
    private Context mContext;

    public HomeDialogRecyclerAdapter(Context mContext, ArrayList SONG_NAME, ArrayList ARTIST_NAME, ArrayList ALBUM_ART, ArrayList DATA, ArrayList TIME) {
        this.mContext = mContext;
        this.SONG_NAME = SONG_NAME;
        this.ARTIST_NAME = ARTIST_NAME;
        this.ALBUM_ART = ALBUM_ART;
        this.DATA = DATA;
        this.TIME = TIME;
    }

    @NonNull
    @Override
    public HomeDialogRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.home_dialog_recycler_layout, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeDialogRecyclerAdapter.MyViewHolder holder, int position) {
        String songname, artistname;

        songname = SONG_NAME.get(position).toString();
        artistname = ARTIST_NAME.get(position).toString();

        loadAlbumArt((Integer) ALBUM_ART.get(position), holder.ALBUMart);
        holder.SONGname.setText(songname);
        holder.ARTISTname.setText(artistname);

    }

    @Override
    public int getItemCount() {
        return SONG_NAME.size();
    }

    public void loadAlbumArt(int albumId, ImageView view) {
        Drawable mDefaultBackground = view.getResources().getDrawable(R.drawable.clipart);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        //Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(view.getContext()).load(path).override(250, 250).error(mDefaultBackground).into(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView SONGname, ARTISTname;
        public ImageView ALBUMart;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            SONGname = (TextView) view.findViewById(R.id.Home_Dialog_Recycler_layout_name);
            ARTISTname = (TextView) view.findViewById(R.id.Home_Dialog_Recycler_layout_artist);
            ALBUMart = (ImageView) view.findViewById(R.id.Home_Dialog_Recycler_Image);
            cardView = (CardView) view.findViewById(R.id.Home_Dialog_Recycler_Card);
        }

    }


}
