package com.example.user1.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_BROADCAST_IF_PLAYING_FOR_PAUSE;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_BROADCAST_IF_PLAYING_FOR_RESUME;

/**
 * Created by USER1 on 19/11/2016.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private MainActivity mainActivity;

    boolean isPlaying;

    public MyBroadcastReceiver(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //The action held within the intent that was sent here through broadcast.
        String action = intent.getAction();

        switch(action){
            case ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE:
                isPlaying = intent.getBooleanExtra("isPlaying", isPlaying);
                mainActivity.playPause(isPlaying);
                break;

            //When i'm being called (MyPhoneStateListener).
            case ACTION_BROADCAST_IF_PLAYING_FOR_PAUSE:
                isPlaying = intent.getBooleanExtra("isPlaying", isPlaying);
                mainActivity.pause(isPlaying);
                break;

            //When call has just ended/rejected/no one is calling me (MyPhoneStateListener).
            case ACTION_BROADCAST_IF_PLAYING_FOR_RESUME:
                isPlaying = intent.getBooleanExtra("isPlaying", isPlaying);
                mainActivity.resume(isPlaying);
                break;

        }

    }
}
