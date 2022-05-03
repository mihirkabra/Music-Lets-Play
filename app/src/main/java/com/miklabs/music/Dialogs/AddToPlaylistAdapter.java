package com.miklabs.music.Dialogs;

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

public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.MyViewHolder> {

    ArrayList<String> playlistNameArrayList;
    Context mContext;

    public AddToPlaylistAdapter(Context mContext, ArrayList<String> playlistNameArrayList) {
        this.mContext = mContext;
        this.playlistNameArrayList = playlistNameArrayList;
    }

    @NonNull
    @Override
    public AddToPlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_list_dialog_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddToPlaylistAdapter.MyViewHolder myViewHolder, int i) {

        String playlistNameString;

        playlistNameString = playlistNameArrayList.get(i).toString();
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
            clipart = (ImageView) itemView.findViewById(R.id.playlist_list_image_dialog);
            playlist_name = (TextView) itemView.findViewById(R.id.playlist_name_dialog);
        }
    }
}
