package com.example.user1.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by USER1 on 19/11/2016.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    public boolean isMusicPlaying;//var to check if the music is now playing in the BackgroundMusicService
    @Override
    public void onReceive(Context context, Intent intent) {
        //The action held within the intent that was sent here through broadcast
        String action = intent.getAction();

        switch(action){
            case BackgroundMusicService.ACTION_CHECK_IF_PLAYING:
                isMusicPlaying = intent.getBooleanExtra("isPlaying", isMusicPlaying);
                Toast.makeText(context,"MBR, isMusicPlaying: "+isMusicPlaying, Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
