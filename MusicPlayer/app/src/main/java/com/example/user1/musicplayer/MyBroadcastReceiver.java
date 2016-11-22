package com.example.user1.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
        //The action held within the intent that was sent here through broadcast
        String action = intent.getAction();

        switch(action){
            case BackgroundMusicService.ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE:
                isPlaying = intent.getBooleanExtra("isPlaying", isPlaying);
                mainActivity.playPause(isPlaying);
                break;
        }

    }
}
