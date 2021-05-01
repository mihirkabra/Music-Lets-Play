package com.example.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.FileDescriptor;

import static com.example.music.MusicPlayer.mediaPlayer;
import static com.example.music.MusicPlayer.mySongsAlbumID;
import static com.example.music.MusicPlayer.mySongsArtist;
import static com.example.music.MusicPlayer.mySongsName;
import static com.example.music.MusicPlayer.position;

public class ForegroundService extends Service {


    public static final String CHANNEL_ID = "MusicServiceChannel";
    Notification status;
    private final String LOG_TAG = "NotificationService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bitmap b = getAlbumart(this, Long.valueOf(mySongsAlbumID.get(position)));

        int position = intent.getIntExtra("MusicPlayerPosition", 0);
        createNotificationChannel();

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MusicPlayer.class), PendingIntent.FLAG_UPDATE_CURRENT);


        Intent receiveNext = new Intent(getApplicationContext(), NotificationReceiver.class);
        receiveNext.putExtra("Action", "NEXT_ACTION");
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 1, receiveNext, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent receivePrevious = new Intent(getApplicationContext(), NotificationReceiver.class);
        receivePrevious.putExtra("Action", "PREVIOUS_ACTION");
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this, 2, receivePrevious, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent receivePause = new Intent(getApplicationContext(), NotificationReceiver.class);
        receivePause.putExtra("Action", "PAUSE_ACTION");
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 3, receivePause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent receiveShuffle = new Intent(getApplicationContext(), NotificationReceiver.class);
        receiveShuffle.putExtra("Action", "SHUFFLE_ACTION");
        PendingIntent pendingIntentShuffle = PendingIntent.getBroadcast(this, 4, receiveShuffle, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent receiveReplay = new Intent(getApplicationContext(), NotificationReceiver.class);
        receiveReplay.putExtra("Action", "REPLAY_ACTION");
        PendingIntent pendingIntentReplay = PendingIntent.getBroadcast(this, 5, receiveReplay, PendingIntent.FLAG_UPDATE_CURRENT);



        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentTitle(mySongsName.get(position))
                    .setContentText(mySongsArtist.get(position))
                    .setLargeIcon(b)
                    .setContentIntent(pendingIntent)
                    .setSound(null)
                    .setShowWhen(false)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                )
                    .addAction(R.drawable.button_shuffle_2, "Shuffle", pendingIntentShuffle)
                    .addAction(R.drawable.button_previous, "Previous", pendingIntentPrevious)
                    //.addAction(R.drawable.button_pause, "Pause", pendingIntentPause)
                .addAction(mediaPlayer.isPlaying() ? R.drawable.button_pause
                                : R.drawable.button_play,
                        mediaPlayer.isPlaying() ? "Pause"
                                : "Play",
                        pendingIntentPause)
                    .addAction(R.drawable.button_next, "Next", pendingIntentNext)
                    .addAction(R.drawable.button_replay, "Repeat", pendingIntentReplay)
                    .setSubText(MainActivity.PlaylistNameForSongs.equals("PlaylistName")? "Now Playing" : MainActivity.PlaylistNameForSongs)
                    .setOnlyAlertOnce(true)
                    .setChannelId(CHANNEL_ID)
                    .build();


            startForeground(1, notification);

            if(!mediaPlayer.isPlaying())
            {
                stopForeground(false);
            }
        //stopSelf();

        return START_STICKY ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        stopForeground(true);
        System.exit(0);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            serviceChannel.enableLights(true);
            serviceChannel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public Bitmap getAlbumart(Context context, Long album_id) {
        Bitmap albumArtBitMap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {

            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                albumArtBitMap = BitmapFactory.decodeFileDescriptor(fd, null,
                        options);
                pfd = null;
                fd = null;
            }
        } catch (Error ee) {
        } catch (Exception e) {
        }

        if (null != albumArtBitMap) {
            return albumArtBitMap;
        }
        else {
            albumArtBitMap= BitmapFactory.decodeResource(getResources(), R.drawable.clipart);
            return albumArtBitMap;
        }
    }


}