package com.example.user1.musicplayer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageButton;

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
        final ArrayList<Track> tracksList = (ArrayList<Track>) intent.getSerializableExtra("tracksList");


            String action = intent.getAction();

        switch (action) {
            //If the action is 'ACTION_PLAY_TRACK' - i told the app to play a track.
            case ACTION_PLAY_TRACK:
                if (tracksList != null) {
                    //If the music is playing right now - stop, in order to play another track.
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();


                    //The ID of the resource the first track in the tracksList contains
                    final int resId = tracksList.get(0).getID();

                    //Starting a new Track.
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), resId);
                    mediaPlayer.start();


                    //When the current track playing ends - play the next Track if it exists. If it doesn't, do nothing.
                    whenTheTrackEndsPlayNext(tracksList);
                }
                break;
            //If the action is 'ACTION_PAUSE' - i told the app to pause the track.
            case ACTION_PAUSE:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;
            //If the action is 'ACTION_RESUME' - i told the app to resume the track from where it paused.
            case ACTION_RESUME:
                mediaPlayer.start();
                break;
        }



            //I have no idea what this const is.
            return START_NOT_STICKY;

    }

    private void whenTheTrackEndsPlayNext(final ArrayList<Track> tracksList) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                tracksList.remove(0);
                if (!tracksList.isEmpty() && tracksList.size() > 0) {
                    mp.reset();
                    Uri path = Uri.parse("android.resource://com.example.user1.musicplayer/raw/" + tracksList.get(0).getName());
                    try {
                        mp.setDataSource(getApplicationContext(), path);
                        mp.prepare();
                        mp.start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
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
