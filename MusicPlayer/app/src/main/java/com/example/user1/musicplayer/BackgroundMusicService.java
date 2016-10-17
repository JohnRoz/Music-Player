package com.example.user1.musicplayer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by USER1 on 16/10/2016.
 */

public class BackgroundMusicService extends Service {

    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        //creating the MediaPlayer for the service.
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //If the music is playing right now - stop, in order to play another track.
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();

        /**A duplicate of the track sent through the Intent.
        * (Serializable creates a clone of the object that was sent on the other side of the Intent)*/
        Track track = (Track) intent.getSerializableExtra("track");

        /**If the user pressed the 'Stop' button, the track sent is Null. (that makes this clone track Null too).
        * this will stop the music because the music was stopped above and here I use return without starting a new Track.*/
        if(track == null)
            //I have no idea what this const is.
            return START_NOT_STICKY;

        //The ID of the resource the track contains
        final int resId = track.getID();

        //Starting a new Track.
        mediaPlayer = MediaPlayer.create(getApplicationContext(),resId);
        mediaPlayer.start();

        //I have no idea what this const is.
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        //when is destroyed, if the music is playing, stop it.
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
