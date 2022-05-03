package com.miklabs.music;

import static com.miklabs.music.FragmentClasses.ContentHome.collection;
import static com.miklabs.music.FragmentClasses.ContentHome.goingToMusic;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MusicPlayer extends AppCompatActivity {

    public static MediaPlayer mediaPlayer;
    public static Boolean playing = true;
    static Button play, next, previous, shuffle, replay;
    static ToggleButton repeat, mute;
    static TextView currentTime, totalTime, songName, artistName;
    static SeekBar musicSeekbar;
    static ImageView songImage;
    static int position;
    static double currentDurationTime;
    static double totalDurationTime;
    static Boolean isRepeat = false;
    static Boolean isMute = false;
    static Boolean paused = false;
    static ArrayList<SongsModel> songs;
    private final Handler mHandler = new Handler();
    File file;
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                mediaPlayer.pause();
                playing = false;
                play.setBackgroundResource(R.drawable.button_play);
                startService(position);
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {


                mediaPlayer.start();
                playing = true;
                play.setBackgroundResource(R.drawable.button_pause);
                startService(position);

                if (paused) {
                    mediaPlayer.pause();
                    playing = false;
                    play.setBackgroundResource(R.drawable.button_play);
                    startService(position);
                }

                //Not in call: Play music
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                mediaPlayer.pause();
                playing = false;
                play.setBackgroundResource(R.drawable.button_play);
                startService(position);
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };
    String sname, artistname, song;
    Handler handler = new Handler();
    //static ArrayList<Integer> mySongsAlbumID;
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
    int mCurrentPosition;
    SharedPreferences db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.splashScreenStatusBar));

        play = findViewById(R.id.btnPlay);

        next = findViewById(R.id.btnNext);

        previous = findViewById(R.id.btnPrevious);

        repeat = findViewById(R.id.btnRepeat);

        shuffle = findViewById(R.id.btnShuffle);

        replay = findViewById(R.id.btnReplay);

        mute = findViewById(R.id.btnMute);

        currentTime = findViewById(R.id.timePlayed);

        totalTime = findViewById(R.id.timeTotal);

        songName = findViewById(R.id.songName);

        artistName = findViewById(R.id.songArtistName);

        musicSeekbar = findViewById(R.id.musicSeekbar);

        songImage = findViewById(R.id.songImg);

        songImage.setOnTouchListener(new OnSwipeTouchListener(MusicPlayer.this) {
            public void onSwipeRight() {

                mediaPlayer.stop();

                mediaPlayer.release();

                position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
                collection.putInt("pos", position);
                collection.putInt("currentpos", 0);
                collection.commit();


                configMediaPlayer();

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }

            public void onSwipeLeft() {

                mediaPlayer.stop();

                mediaPlayer.release();

                position = ((position + 1) % songs.size());
                collection.putInt("pos", position);
                collection.putInt("currentpos", 0);
                collection.commit();


                configMediaPlayer();

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        db = PreferenceManager.getDefaultSharedPreferences(MusicPlayer.this);

        Gson gson = new Gson();

        String arrayListStringSongName = db.getString("songs", null);
        Type typeSongName = new TypeToken<ArrayList<SongsModel>>() {
        }.getType();
        songs = gson.fromJson(arrayListStringSongName, typeSongName);

        if (goingToMusic) {
            collection.putInt("currentpos", 0);
            collection.commit();

            goingToMusic = false;
        }

        position = db.getInt("pos", 0);

        sname = songs.get(position).getSongName();

        loadAlbumArt(songs.get(position).getAlbumArt(), songImage);

        songName.setText(sname);

        songName.setSelected(true);

        artistname = songs.get(position).getArtistName();

        artistName.setText(artistname);

        artistName.setSelected(true);

        song = songs.get(position).getData();

        Uri u = Uri.parse(song);

        file = new File(u.getPath());

        if (file.exists()) {
            mediaPlayer = MediaPlayer.create(MusicPlayer.this, u);

            mediaPlayer.start();

            musicSeekbar.setMax(mediaPlayer.getDuration());

            setTotalTime();

            startService(position);

            UpdateSeekbar();

            mediaPlayer.seekTo(mCurrentPosition);
        } else {
            position = ((position + 1) % songs.size());
            collection.putInt("pos", position);
            collection.putInt("currentpos", 0);
            collection.commit();

            configMediaPlayer();
        }

        if (!playing) {
            playing = true;
            play.setBackgroundResource(R.drawable.button_pause);
            mediaPlayer.start();
            paused = false;
        }

        mediaPlayer.setOnCompletionListener(mp -> {

            mediaPlayer.stop();

            mediaPlayer.release();

            position = ((position + 1) % songs.size());
            collection.putInt("pos", position);
            collection.putInt("currentpos", 0);
            collection.commit();

            configMediaPlayer();

        });

        play.setOnClickListener(v -> {
            musicSeekbar.setMax(mediaPlayer.getDuration());

            if (mediaPlayer.isPlaying()) {
                play.setBackgroundResource(R.drawable.button_play);
                mediaPlayer.pause();
                playing = false;
                paused = true;
                startService(position);
            } else {
                play.setBackgroundResource(R.drawable.button_pause);
                mediaPlayer.start();
                playing = true;
                paused = false;
                startService(position);
            }
        });

        next.setOnClickListener(v -> {

            mediaPlayer.stop();

            mediaPlayer.release();

            position = ((position + 1) % songs.size());
            collection.putInt("pos", position);
            collection.putInt("currentpos", 0);
            collection.commit();

            configMediaPlayer();

        });

        previous.setOnClickListener(v -> {

            mediaPlayer.stop();

            mediaPlayer.release();

            position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
            collection.putInt("pos", position);
            collection.putInt("currentpos", 0);
            collection.commit();

            configMediaPlayer();

        });

        shuffle.setOnClickListener(v -> {

            position = 0;

            long seed = System.nanoTime();

            Collections.shuffle(songs, new Random(seed));

            Gson songsGson = new Gson();
            String songsStr = songsGson.toJson(songs);
            collection.putString("songs", songsStr);

            collection.commit();

            mediaPlayer.stop();

            mediaPlayer.release();

            sname = songs.get(position).getSongName();

            loadAlbumArt(songs.get(position).getAlbumArt(), songImage);

            artistname = songs.get(position).getArtistName();

            artistName.setText(artistname);

            song = songs.get(position).getData();

            Uri u1 = Uri.parse(song);

            if (new File(u1.getPath()).exists()) {
                mediaPlayer = MediaPlayer.create(MusicPlayer.this, u1);
            } else {
                position = ((position + 1) % songs.size());
                collection.putInt("pos", position);
                collection.putInt("currentpos", 0);
                collection.commit();


                configMediaPlayer();
            }

            songName.setText(sname);

            setTotalTime();

            mediaPlayer.start();

            musicSeekbar.setMax(mediaPlayer.getDuration());

            collection.putInt("currentpos", 0);
            collection.commit();

            startService(position);

            UpdateSeekbar();
            mediaPlayer.seekTo(mCurrentPosition);

            play.setBackgroundResource(R.drawable.button_pause);

            playing = true;
            paused = false;

            if (isMute) {
                mediaPlayer.setVolume(0, 0);
            }
            if (isRepeat) {

                mediaPlayer.setOnCompletionListener(mp -> {

                    mediaPlayer.setLooping(true);

                    configMediaPlayer();

                });
            } else {
                mediaPlayer.setOnCompletionListener(mp -> {

                    mediaPlayer.stop();

                    mediaPlayer.release();

                    position = ((position + 1) % songs.size());
                    collection.putInt("pos", position);
                    collection.putInt("currentpos", 0);
                    collection.commit();

                    configMediaPlayer();

                });
            }
        });

        replay.setOnClickListener(v -> {

            mediaPlayer.stop();

            mediaPlayer.release();

            collection.putInt("currentpos", 0);
            collection.commit();

            configMediaPlayer();
        });

        mute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isMute = true;
                    mediaPlayer.setVolume(0, 0);
                    mute.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.smallButtonActivate)));
                } else {
                    isMute = false;
                    mediaPlayer.setVolume(1, 1);
                    mute.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.smallButtonColor)));
                }
            }
        });


        repeat.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                repeat.setBackgroundResource(R.drawable.button_repeat_on);

                mediaPlayer.setOnCompletionListener(mp -> {

                    mediaPlayer.stop();

                    mediaPlayer.release();

                    configMediaPlayer();

                });

                isRepeat = true;
            } else {
                repeat.setBackgroundResource(R.drawable.button_repeat_off);

                mediaPlayer.setOnCompletionListener(mp -> {

                    mediaPlayer.stop();

                    mediaPlayer.release();

                    position = ((position + 1) % songs.size());
                    collection.putInt("pos", position);
                    collection.putInt("currentpos", 0);
                    collection.commit();

                    configMediaPlayer();

                });

                isRepeat = false;
            }
        });


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


    public void configMediaPlayer() {

        song = songs.get(position).getData();

        Uri u = Uri.parse(song);

        file = new File(u.getPath());

        if (file.exists()) {
            mediaPlayer = MediaPlayer.create(MusicPlayer.this, u);
        } else {
            position = ((position + 1) % songs.size());
            collection.putInt("pos", position);
            collection.putInt("currentpos", 0);
            collection.commit();

            configMediaPlayer();
        }

        sname = songs.get(position).getSongName();

        loadAlbumArt(songs.get(position).getAlbumArt(), songImage);

        artistname = songs.get(position).getArtistName();

        artistName.setText(artistname);

        songName.setText(sname);

        setTotalTime();

        mediaPlayer.start();

        musicSeekbar.setMax(mediaPlayer.getDuration());

        startService(position);

        UpdateSeekbar();

        mediaPlayer.seekTo(mCurrentPosition);


        if (!playing) {
            play.setBackgroundResource(R.drawable.button_play);
            mediaPlayer.pause();
            paused = true;
        }
        if (isMute) {
            mediaPlayer.setVolume(0, 0);
        }

        if (isRepeat) {
            mediaPlayer.setOnCompletionListener(mp -> {

                mediaPlayer.setLooping(true);

                collection.putInt("currentpos", 0);
                collection.commit();
                configMediaPlayer();

            });
        } else {
            mediaPlayer.setOnCompletionListener(mp -> {

                mediaPlayer.stop();

                mediaPlayer.release();

                position = ((position + 1) % songs.size());
                collection.putInt("pos", position);
                collection.putInt("currentpos", 0);
                collection.commit();


                configMediaPlayer();

            });
        }
    }

    public void loadAlbumArt(int albumId, ImageView view) {
        Drawable mDefaultBackground = getResources().getDrawable(R.drawable.player_background);
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri path = ContentUris.withAppendedId(artworkUri, albumId);
        // Glide.with(view.getContext()).load(path).error(mDefaultBackground).into(view);
        Glide.with(getApplicationContext()).load(path).override(300, 350).error(mDefaultBackground).into(view);
    }


    public void startService(int position) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("MusicPlayerPosition", position);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    public void UpdateSeekbar() {


        MusicPlayer.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mCurrentPosition = db.getInt("currentpos", 0);
                if (mediaPlayer != null) {
                    //mCurrentPosition = mediaPlayer.getCurrentPosition();
                    collection.putInt("currentpos", mediaPlayer.getCurrentPosition());
                    collection.commit();
                    musicSeekbar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

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
    }

    public static class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public void onSwipeRight() {

        }

        public void onSwipeLeft() {

        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
    }


   /* @Override
    public void onBackPressed() {

        editor.putInt("pos", position);
        editor.putInt("currentpos", 0);
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    } */
}
