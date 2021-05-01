package com.example.music.PlaylistClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music.R;

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

        playlistNameString=playlistNameArrayList.get(i).toString();
        myViewHolder.playlist_name.setText(playlistNameString);

        myViewHolder.clipart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.clipart));

    }

    @Override
    public int getItemCount() {
        return playlistNameArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView clipart;
        TextView playlist_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clipart = (ImageView) itemView.findViewById(R.id.playlist_list_image);
            playlist_name = (TextView) itemView.findViewById(R.id.playlist_name);
        }
    }
}
