package com.example.user1.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by USER1 on 16/10/2016.
 */

public class BackgroundMusicService extends Service {

    private MediaPlayer mediaPlayer;

    //Actions.
    public static final String ACTION_INIT_SERVICE = "com.example.user1.musicplayer.action.INIT_SERVICE";
    public static final String ACTION_PAUSE = "com.example.user1.musicplayer.action.PAUSE";
    public static final String ACTION_RESUME = "com.example.user1.musicplayer.action.RESUME";
    public static final String ACTION_PLAY_TRACK = "com.example.user1.musicplayer.action.PLAY_TRACK";
    public static final String ACTION_SKIP_NEXT = "com.example.user1.musicplayer.action.SKIP_NEXT";
    public static final String ACTION_SKIP_PREV = "com.example.user1.musicplayer.action.SKIP_PREV";
    public static final String ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE = "com.example.user1.musicplayer.action.BROADCAST_IF_PLAYING_FOR_PLAYPAUSE";
    public static final String ACTION_PAUSE_WHEN_CALLING = "com.example.user1.musicplayer.action.PAUSE_WHEN_CALLING";
    public static final String ACTION_RESUME_AFTER_CALL = "com.example.user1.musicplayer.action.RESUME_AFTER_CALL";

    private ArrayList<Track> tracks;
    private int currentTrackPosition;

    private final int MILLISECONDS_TO_REPEAT = 2000;
    private final int NOTIFICATION_ID = 1;


    @Override
    public void onCreate() {
        super.onCreate();
        //Creating the MediaPlayer for the service.
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentTrackPosition = intent.getIntExtra(MainActivity.position, currentTrackPosition);
        String action = intent.getAction();

        //Set the notification and start foreground.
        initNotification();

        switch (action) {
            case ACTION_INIT_SERVICE:
                tracks = (ArrayList<Track>) intent.getSerializableExtra("tracks");
                break;

            //If the action is 'ACTION_PLAY_TRACK' - I told the app to play a track.
            case ACTION_PLAY_TRACK:
                playTrack();
                break;

            //If the action is 'ACTION_PAUSE' - I told the app to pause the track.
            case ACTION_PAUSE:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;

            //If the action is 'ACTION_RESUME' - I told the app to resume the track from where it paused.
            case ACTION_RESUME:
                mediaPlayer.start();
                break;

            //If the action is 'ACTION_SKIP_NEXT' - I added 1 to 'currentTrackPosition' and restarted the mediaPlayer.
            case ACTION_SKIP_NEXT:
                currentTrackPosition++;
                skip();
                break;

            //If the action is 'ACTION_SKIP_PREV' - I subtracted 1 from 'currentTrackPosition' and restarted the mediaPlayer.
            case ACTION_SKIP_PREV:
                /*If the currentPosition of the music in the Track is less than MILLISECONDS_TO_REPEAT, skip back.
                *If the currentPosition of the music in the Track is more (or equal to) MILLISECONDS_TO_REPEAT,
                go back to the beginning of the Track.*/
                if (mediaPlayer.getCurrentPosition() < MILLISECONDS_TO_REPEAT)
                    currentTrackPosition--;
                skip();
                break;

            //Here I send a broadcast with the intent containing mediaPlayer.isPlaying().
            case ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE:
                broadcastIsPlayingForAction(ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE);
                break;

            //Here I send a broadcast with the intent containing mediaPlayer.isPlaying().
            case ACTION_PAUSE_WHEN_CALLING:
                broadcastIsPlayingForAction(ACTION_PAUSE_WHEN_CALLING);
                break;

            //Here I send a broadcast with the intent containing mediaPlayer.isPlaying().
            case ACTION_RESUME_AFTER_CALL:
                broadcastIsPlayingForAction(ACTION_RESUME_AFTER_CALL);
                break;
        }

        //I have no idea what this const is.
        return START_NOT_STICKY;
    }

    private void broadcastIsPlayingForAction(String action) {
        //Intent to send the broadcast.
        Intent broadcastIsPlayingIntent = new Intent();

        //Set the action of the intent to the action i got as a parameter, so the receiver knows what to do.
        broadcastIsPlayingIntent.setAction(action);

        //Put mediaPlayer.isPlaying() inside the intent as an extra.
        broadcastIsPlayingIntent.putExtra(MainActivity.isPlaying, mediaPlayer.isPlaying());

        //Send the broadcast.
        sendBroadcast(broadcastIsPlayingIntent);
    }

    private void initNotification() {
        String trackTitle = Track.UNKNOWN_INFO;
        String trackAlbum = Track.UNKNOWN_INFO;
        if (tracks != null && !tracks.isEmpty() && currentTrackPosition >= 0 && currentTrackPosition < tracks.size()) {
            trackTitle = tracks.get(currentTrackPosition).getTrackTitle();
            trackAlbum = tracks.get(currentTrackPosition).getAlbum();
        }


        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, BackgroundMusicService.class);
        previousIntent.setAction(ACTION_SKIP_PREV);
        PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, BackgroundMusicService.class);
        playIntent.setAction(ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, BackgroundMusicService.class);
        nextIntent.setAction(ACTION_SKIP_NEXT);
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.music_icon_1);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(trackTitle)
                .setContentText(trackAlbum)
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingNotificationIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_skip_previous_black_24dp, "", pendingPreviousIntent)
                .addAction(R.drawable.ic_play_pause_black_24dp, "", pendingPlayIntent)
                .addAction(R.drawable.ic_skip_next_black_24dp, "", pendingNextIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void playTrack() {
        if (tracks != null && !tracks.isEmpty() && currentTrackPosition >= 0 && currentTrackPosition < tracks.size()) {
            //If the music is playing right now - stop, in order to play another track.
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

            //The URI of the file the track in the currentTrackPosition in 'tracks' contains.
            final Uri uri = Uri.parse(tracks.get(currentTrackPosition).getUriString());

            //Resetting the MediaPlayer to the Idle state so an IllegalStateException will not be thrown when I use setDataSource().
            mediaPlayer.reset();

            //Setting the data source to the file in the Track that was pressed.
            //This may throw IllegalStateException if the MediaPlayer wasn't properly reset to the Idle state.
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepare();
            } catch (IOException ex) {
                throw new RuntimeException();
            }

            //Starting the new Track.
            mediaPlayer.start();

            //When the current track ends playing - play the next Track if it exists. If it doesn't, do nothing.
            whenTheTrackEndsPlayNext();
        } else
            stopSelf();
    }

    //This starts when a track ends.
    private void whenTheTrackEndsPlayNext() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Add 1 to the current track position and play the track in that position.
                currentTrackPosition++;
                skip();
            }
        });
    }

    private void skip() {
        //If the music is playing right now - stop.
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();

        //Checking: 'tracks' exists, & that 'currentTrackPosition' is in a legal range.
        if (tracks != null && !tracks.isEmpty() && currentTrackPosition >= 0 && currentTrackPosition < tracks.size()) {

            //Resetting the MediaPlayer to the Idle state so an IllegalStateException will not be thrown when I use setDataSource().
            mediaPlayer.reset();

            //Set the media player a new source which is the track in the tracks list, in the currentTrackPosition.
            try {
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(tracks.get(currentTrackPosition).getUriString()));
                mediaPlayer.prepare();
            } catch (IOException ex) {
                throw new RuntimeException();
            }

            mediaPlayer.start();

            initNotification();
        } else if (tracks != null && currentTrackPosition > tracks.size()) {
            currentTrackPosition--;
        } else if (tracks != null && currentTrackPosition < 0)
            currentTrackPosition++;
    }

    @Override
    public void onDestroy() {
        //When the service is destroyed, if the music is playing - stop it.
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.release();
        mediaPlayer = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
