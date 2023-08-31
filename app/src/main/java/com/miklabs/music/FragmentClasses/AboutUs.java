package com.miklabs.music.FragmentClasses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miklabs.music.R;

public class AboutUs extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_us_layout, container, false);

        LinearLayout developer = view.findViewById(R.id.developer);
        developer.setOnClickListener(v -> {
            Uri url = Uri.parse("https://www.google.com/search?q=mihir+kabra");
            Intent i = new Intent(Intent.ACTION_VIEW, url);
            startActivity(i);
        });

        TextView privacyPolicy = view.findViewById(R.id.privacy);
        privacyPolicy.setOnClickListener(v -> {
            Uri url = Uri.parse("https://blogsm.xyz/music-lets-play/privacypolicy.html");
            Intent i = new Intent(Intent.ACTION_VIEW, url);
            startActivity(i);
        });

        return view;

    }
}
