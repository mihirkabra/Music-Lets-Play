package com.example.music;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.example.music.FragmentClasses.ContentHome.collection;
import static com.example.music.MusicPlayer.artistName;
import static com.example.music.MusicPlayer.currentDurationTime;
import static com.example.music.MusicPlayer.currentTime;
import static com.example.music.MusicPlayer.isMute;
import static com.example.music.MusicPlayer.isRepeat;
import static com.example.music.MusicPlayer.mediaPlayer;
import static com.example.music.MusicPlayer.musicSeekbar;
import static com.example.music.MusicPlayer.mySongs;
import static com.example.music.MusicPlayer.mySongsAlbumID;
import static com.example.music.MusicPlayer.mySongsArtist;
import static com.example.music.MusicPlayer.mySongsName;
import static com.example.music.MusicPlayer.paused;
import static com.example.music.MusicPlayer.play;
import static com.example.music.MusicPlayer.playing;
import static com.example.music.MusicPlayer.position;
import static com.example.music.MusicPlayer.songImage;
import static com.example.music.MusicPlayer.songName;
import static com.example.music.MusicPlayer.totalDurationTime;
import static com.example.music.MusicPlayer.totalTime;

public class NotificationReceiver extends BroadcastReceiver {

    String sname, artistname, song;
    Handler handler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getStringExtra("Action");
        if (action.equals("NEXT_ACTION")) {

            mediaPlayer.stop();

            mediaPlayer.release();

            position = ((position + 1) % mySongs.size());
            collection.putInt("pos", position);
            collection.commit();

            configMediaPlayer(context);

        } else if (action.equals("PREVIOUS_ACTION")) {

            mediaPlayer.stop();

            mediaPlayer.release();

            position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
            collection.putInt("pos", position);
            collection.commit();

            configMediaPlayer(context);
        } else if (action.equals("PAUSE_ACTION")) {

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

        } else if (action.equals("REPLAY_ACTION")) {

            mediaPlayer.stop();

            mediaPlayer.release();

            configMediaPlayer(context);

        } else if (action.equals("SHUFFLE_ACTION")) {

            position = 0;


            long seed = System.nanoTime();

            Collections.shuffle(mySongs, new Random(seed));


            Collections.shuffle(mySongsName, new Random(seed));

            Collections.shuffle(mySongsAlbumID, new Random(seed));

            Collections.shuffle(mySongsArtist, new Random(seed));


            mediaPlayer.stop();

            mediaPlayer.release();

            sname = mySongsName.get(position).toString();

            loadAlbumArt(Integer.parseInt(mySongsAlbumID.get(position)), songImage, context);

            artistname = mySongsArtist.get(position);

            artistName.setText(artistname);

            song = mySongs.get(position);

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
            paused=false;

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

                        position = ((position + 1) % mySongs.size());
                        collection.putInt("pos", position);
                        collection.commit();

                        configMediaPlayer(context);

                    }
                });
            }


        }

    }


    public void configMediaPlayer(final Context c) {


        song = mySongs.get(position);

        Uri u = Uri.parse(song);

        mediaPlayer = MediaPlayer.create(c, u);

        sname = mySongsName.get(position).toString();

        loadAlbumArt(Integer.parseInt(mySongsAlbumID.get(position)), songImage, c);

        artistname = mySongsArtist.get(position);

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

                    position = ((position + 1) % mySongs.size());
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


    private Runnable UpdateSongTime = new Runnable() {

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