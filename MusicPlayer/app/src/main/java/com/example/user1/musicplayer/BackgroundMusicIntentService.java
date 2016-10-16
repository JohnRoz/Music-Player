package com.example.user1.musicplayer;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by USER1 on 16/10/2016.
 */

public class BackgroundMusicIntentService extends IntentService {

    public BackgroundMusicIntentService() {
        super("BackgroundMusicIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
