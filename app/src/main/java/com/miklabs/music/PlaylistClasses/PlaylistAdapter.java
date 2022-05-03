package com.miklabs.music.PlaylistClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miklabs.music.R;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    ArrayList<String> playlistNameArrayList;
    Context mContext;

    public PlaylistAdapter(Context mContext, ArrayList<String> playlistNameArrayList) {
        this.mContext = mContext;
        this.playlistNameArrayList = playlistNameArrayList;
    }

    @NonNull
    @Override
    public PlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_list_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyViewHolder myViewHolder, int i) {

        String playlistNameString;

        playlistNameString = playlistNameArrayList.get(i);
        myViewHolder.playlist_name.setText(playlistNameString);

        myViewHolder.clipart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.clipart));

    }

    @Override
    public int getItemCount() {
        return playlistNameArrayList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView clipart;
        TextView playlist_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clipart = itemView.findViewById(R.id.playlist_list_image);
            playlist_name = itemView.findViewById(R.id.playlist_name);
        }
    }
}
