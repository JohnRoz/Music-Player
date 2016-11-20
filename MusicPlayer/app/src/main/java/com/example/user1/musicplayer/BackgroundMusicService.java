package com.example.user1.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by USER1 on 16/10/2016.
 */

public class BackgroundMusicService extends Service {

    MediaPlayer mediaPlayer;
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";
    public static final String ACTION_PLAY_TRACK = "ACTION_PLAY_TRACK";
    public static final String ACTION_SKIP_NEXT = "ACTION_SKIP_NEXT";
    public static final String ACTION_SKIP_PREV = "ACTION_SKIP_PREV";
    public static final String ACTION_CHECK_IF_PLAYING="ACTION_CHECK_IF_PLAYING";

    private int currentTrackPosition;

    private final int MILLISECONDS_TO_REPEAT = 2000;
    private final int NOTIFICATION_ID=1;



    public static boolean isMusicPlaying;//A static boolean var to tell me if the music is playing or not right now


    @Override
    public void onCreate() {
        super.onCreate();
        //creating the MediaPlayer for the service.
        mediaPlayer = new MediaPlayer();

        //set the notification and start foreground
        initNotification();
    }

    private void initNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, BackgroundMusicService.class);
        previousIntent.setAction(ACTION_SKIP_PREV);
        PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, BackgroundMusicService.class);
        playIntent.setAction(ACTION_RESUME);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, BackgroundMusicService.class);
        nextIntent.setAction(ACTION_SKIP_NEXT);
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.music_icon_1);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Music Player")
                .setContentText("Music")
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingNotificationIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous", pendingPreviousIntent)
                .addAction(R.drawable.ic_play_arrow_black_24dp, "Play", pendingPlayIntent)
                .addAction(R.drawable.ic_skip_next_black_24dp, "Next", pendingNextIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //A duplicate of the tracksList sent through the Intent.
        // (Serializable creates a clone of the object that was sent on the other side of the Intent)
        final ArrayList<Track> tracks = (ArrayList<Track>) intent.getSerializableExtra("tracks");
        currentTrackPosition = intent.getIntExtra("position", currentTrackPosition);
        String action = intent.getAction();

        switch (action) {
            //If the action is 'ACTION_PLAY_TRACK' - I told the app to play a track.
            case ACTION_PLAY_TRACK:
                playTrack(tracks);
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
                skip(tracks);
                break;

            //If the action is 'ACTION_SKIP_PREV' - I subtracted 1 from 'currentTrackPosition' and restarted the mediaPlayer.
            case ACTION_SKIP_PREV:
                if(mediaPlayer.getCurrentPosition()< MILLISECONDS_TO_REPEAT)
                    currentTrackPosition--;
                skip(tracks);
                break;

            case ACTION_CHECK_IF_PLAYING:
                broadcastIsPlaying();
                break;
        }

        //I have no idea what this const is.
        return START_NOT_STICKY;
    }

    private void broadcastIsPlaying() {
        //Intent to send the broadcast
        Intent broadcastIsPlayingIntent = new Intent();

        //Set the action of the intent to ACTION_CHECK_IF_PLAYING, so the receiver knows he should catch it
        broadcastIsPlayingIntent.setAction(ACTION_CHECK_IF_PLAYING);

        //Put the info you want to transfer inside the intent as an extra
        broadcastIsPlayingIntent.putExtra("isPlaying", mediaPlayer.isPlaying());

        //send the broadcast
        sendBroadcast(broadcastIsPlayingIntent);
    }

    //TODO: Notice that the function is ONLY for *resources*. (see line: tracks.get(currentTrackPosition).**getID()**)
    //TODO: I need to change it so it's generic for resources & FILES!
    private void playTrack(final ArrayList<Track> tracks) {
        if (tracks!=null && !tracks.isEmpty() && tracks.size() > 0 && currentTrackPosition >= 0 && currentTrackPosition < tracks.size()) {
            //If the music is playing right now - stop, in order to play another track.
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

            //The ID of the resource the track in the currentTrackPosition in 'tracks' contains
            final int resId = tracks.get(currentTrackPosition).getID();

            //Starting a new Track.
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resId);
            mediaPlayer.start();

            //When the current track playing ends - play the next Track if it exists. If it doesn't, do nothing.
            whenTheTrackEndsPlayNext(tracks);
        }
        else
            stopSelf();
    }

    //This starts when a track ends.
    private void whenTheTrackEndsPlayNext(final ArrayList<Track> tracks) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //add 1 to the current track position and play the track in that position
                currentTrackPosition++;
                skip(tracks);
            }
        });
    }

    //TODO: Notice that the function is ONLY for *resources*. (see line: tracks.get(currentTrackPosition).**getID()**)
    //TODO: I need to change it so it's generic for resources & FILES!
    private void skip(final ArrayList<Track> tracks){
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();

        //Checking: 'tracks' exists, & that 'currentTrackPosition' is in a legal range
        if (!tracks.isEmpty() && tracks.size() > 0 && currentTrackPosition >= 0 && currentTrackPosition < tracks.size()) {
            //Set the media player a new source which is the track in the tracks list, in the currentTrackPosition.
            mediaPlayer = MediaPlayer.create(getApplicationContext(), tracks.get(currentTrackPosition).getID());
            mediaPlayer.start();
        }
        else
            stopSelf();
    }


    @Override
    public void onDestroy() {
        //when is destroyed, if the music is playing, stop it.
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            //isMusicPlaying = mediaPlayer.isPlaying();
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
