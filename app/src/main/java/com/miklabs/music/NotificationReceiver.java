package com.miklabs.music;

import static com.miklabs.music.FragmentClasses.ContentHome.collection;
import static com.miklabs.music.MusicPlayer.artistName;
import static com.miklabs.music.MusicPlayer.currentDurationTime;
import static com.miklabs.music.MusicPlayer.currentTime;
import static com.miklabs.music.MusicPlayer.isMute;
import static com.miklabs.music.MusicPlayer.isRepeat;
import static com.miklabs.music.MusicPlayer.mediaPlayer;
import static com.miklabs.music.MusicPlayer.musicSeekbar;
import static com.miklabs.music.MusicPlayer.paused;
import static com.miklabs.music.MusicPlayer.play;
import static com.miklabs.music.MusicPlayer.playing;
import static com.miklabs.music.MusicPlayer.position;
import static com.miklabs.music.MusicPlayer.songImage;
import static com.miklabs.music.MusicPlayer.songName;
import static com.miklabs.music.MusicPlayer.songs;
import static com.miklabs.music.MusicPlayer.totalDurationTime;
import static com.miklabs.music.MusicPlayer.totalTime;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NotificationReceiver extends BroadcastReceiver {

    String sname, artistname, song;
    Handler handler = new Handler();
    private final Runnable UpdateSongTime = new Runnable() {

        public void run() {

            currentDurationTime = mediaPlayer.getCurrentPosition();


            currentTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) currentDurationTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentDurationTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) currentDurationTime)))
            );

            handler.postDelayed(this, 100);

        }
    };

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getStringExtra("Action");
        switch (action) {
            case "NEXT_ACTION":

                mediaPlayer.stop();

                mediaPlayer.release();

                position = ((position + 1) % songs.size());
                collection.putInt("pos", position);
                collection.commit();

                configMediaPlayer(context);

                break;
            case "PREVIOUS_ACTION":

                mediaPlayer.stop();

                mediaPlayer.release();

                position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
                collection.putInt("pos", position);
                collection.commit();

                configMediaPlayer(context);
                break;
            case "PAUSE_ACTION":

                musicSeekbar.setMax(mediaPlayer.getDuration());

                if (mediaPlayer.isPlaying()) {
                    play.setBackgroundResource(R.drawable.button_play);
                    mediaPlayer.pause();
                    playing = false;
                    paused = true;
                    startService(position, context);
                } else {
                    play.setBackgroundResource(R.drawable.button_pause);
                    mediaPlayer.start();
                    playing = true;
                    paused = false;
                    startService(position, context);
                }

                break;
            case "REPLAY_ACTION":

                mediaPlayer.stop();

                mediaPlayer.release();

                configMediaPlayer(context);

                break;
            case "SHUFFLE_ACTION":

                position = 0;


                long seed = System.nanoTime();

                Collections.shuffle(songs, new Random(seed));

                mediaPlayer.stop();

                mediaPlayer.release();

                sname = songs.get(position).getSongName();

                loadAlbumArt(songs.get(position).getAlbumArt(), songImage, context);

                artistname = songs.get(position).getArtistName();

                artistName.setText(artistname);

                song = songs.get(position).getData();

                Uri u = Uri.parse(song);

                mediaPlayer = MediaPlayer.create(context, u);

                songName.setText(sname);

                setTotalTime();

                mediaPlayer.start();

                musicSeekbar.setMax(mediaPlayer.getDuration());

                startService(position, context);

                musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });

                play.setBackgroundResource(R.drawable.button_pause);

                playing = true;
                paused = false;

                if (isMute) {
                    mediaPlayer.setVolume(0, 0);
                }
                if (isRepeat) {

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            mediaPlayer.setLooping(true);

                            configMediaPlayer(context);

                        }
                    });
                } else {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            mediaPlayer.stop();

                            mediaPlayer.release();

                            position = ((position + 1) % songs.size());
                            collection.putInt("pos", position);
                            collection.commit();

                            configMediaPlayer(context);

                        }
                    });
                }


                break;
        }

    }

    public void configMediaPlayer(final Context c) {


        song = songs.get(position).getData();

        Uri u = Uri.parse(song);

        mediaPlayer = MediaPlayer.create(c, u);

        sname = songs.get(position).getSongName();

        loadAlbumArt(songs.get(position).getAlbumArt(), songImage, c);

        artistname = songs.get(position).getArtistName();

        artistName.setText(artistname);

        songName.setText(sname);

        setTotalTime();

        mediaPlayer.start();

        musicSeekbar.setMax(mediaPlayer.getDuration());

        startService(position, c);

        musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        if (!playing) {
            play.setBackgroundResource(R.drawable.button_play);
            mediaPlayer.pause();
            paused = true;
        }
        if (isMute) {
            mediaPlayer.setVolume(0, 0);
        }

        if (isRepeat) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mediaPlayer.setLooping(true);

                    configMediaPlayer(c);

                }
            });
        } else {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mediaPlayer.stop();

                    mediaPlayer.release();

                    position = ((position + 1) % songs.size());
                    collection.putInt("pos", position);
                    collection.commit();

                    configMediaPlayer(c);

                }
            });
        }
    }

    public void loadAlbumArt(int albumId, ImageView view, Context context) {
        Drawable mDefaultBackground = context.getResources().getDrawable(R.drawable.player_background);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        // Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(context).load(path).override(300, 350).error(mDefaultBackground).into(view);
    }

    public void startService(int position, Context context) {
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        serviceIntent.putExtra("MusicPlayerPosition", position);

        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public void setTotalTime() {
        totalDurationTime = mediaPlayer.getDuration();

        totalTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) totalDurationTime),
                TimeUnit.MILLISECONDS.toSeconds((long) totalDurationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                totalDurationTime)))
        );


        currentDurationTime = mediaPlayer.getCurrentPosition();

        currentTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) currentDurationTime),
                TimeUnit.MILLISECONDS.toSeconds((long) currentDurationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                currentDurationTime)))
        );

        handler.postDelayed(UpdateSongTime, 100);

    }

}