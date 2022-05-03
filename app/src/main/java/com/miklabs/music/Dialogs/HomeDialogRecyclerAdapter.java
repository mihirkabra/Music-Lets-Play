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
import com.miklabs.music.SongsModel;

import java.util.ArrayList;


public class HomeDialogRecyclerAdapter extends RecyclerView.Adapter<HomeDialogRecyclerAdapter.MyViewHolder> {


    public static ArrayList<SongsModel> SONGS;
    //#9900FF2A
    private Context mContext;

    public HomeDialogRecyclerAdapter(Context mContext, ArrayList<SongsModel> SONGS) {
        this.mContext = mContext;
        this.SONGS = SONGS;
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

        songname = SONGS.get(position).getSongName();
        artistname = SONGS.get(position).getArtistName();

        loadAlbumArt(SONGS.get(position).getAlbumArt(), holder.ALBUMart);
        holder.SONGname.setText(songname);
        holder.ARTISTname.setText(artistname);

    }

    @Override
    public int getItemCount() {
        return SONGS.size();
    }

    public void loadAlbumArt(int albumId, ImageView view) {
        Drawable mDefaultBackground = view.getResources().getDrawable(R.drawable.clipart);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        //Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(view.getContext()).load(path).override(250, 250).error(mDefaultBackground).into(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView SONGname, ARTISTname;
        public ImageView ALBUMart;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            SONGname = view.findViewById(R.id.Home_Dialog_Recycler_layout_name);
            ARTISTname = view.findViewById(R.id.Home_Dialog_Recycler_layout_artist);
            ALBUMart = view.findViewById(R.id.Home_Dialog_Recycler_Image);
            cardView = view.findViewById(R.id.Home_Dialog_Recycler_Card);
        }

    }


}
