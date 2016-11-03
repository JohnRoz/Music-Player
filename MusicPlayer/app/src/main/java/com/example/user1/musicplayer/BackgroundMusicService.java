package com.example.user1.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by USER1 on 16/10/2016.
 */

public class BackgroundMusicService extends Service {

    MediaPlayer mediaPlayer;
    public final String ACTION_PAUSE = "ACTION_PAUSE";
    public final String ACTION_RESUME = "ACTION_RESUME";
    public final String ACTION_PLAY_TRACK = "ACTION_PLAY_TRACK";
    public final String ACTION_SKIP_NEXT = "ACTION_SKIP_NEXT";
    public final String ACTION_SKIP_PREV = "ACTION_SKIP_PREV";

    private int currentTrackPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        //creating the MediaPlayer for the service.
        mediaPlayer = new MediaPlayer();
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
                if(mediaPlayer.getCurrentPosition()<1500)
                    currentTrackPosition--;
                skip(tracks);
        }

        //I have no idea what this const is.
        return START_NOT_STICKY;
    }

    private void playTrack(final ArrayList<Track> tracks) {
        if (!tracks.isEmpty() && tracks.size() > 0 && currentTrackPosition >= 0 && currentTrackPosition < tracks.size()) {
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
    //TODO: I need to change iit so it's generic for resources & FILES!
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
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();

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
