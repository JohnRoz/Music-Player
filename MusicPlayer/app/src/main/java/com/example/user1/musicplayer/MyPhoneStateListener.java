package com.example.user1.musicplayer;

import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by USER1 on 23/11/2016.
 */

public class MyPhoneStateListener extends PhoneStateListener {

    private MainActivity mainActivity;

    public MyPhoneStateListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state) {
            //When phone is ringing.
            case TelephonyManager.CALL_STATE_RINGING:
                pauseForCalls();
                break;

            //When in the middle of a phone call or when dialing.
            case TelephonyManager.CALL_STATE_OFFHOOK:
                pauseForCalls();
                break;

            //When the call is ended / rejected / no one is calling me.
            case TelephonyManager.CALL_STATE_IDLE:
                resumeAfterCalls();
                break;
        }

    }

    private void pauseForCalls() {
        final Intent checkIfPlayingIntent = new Intent(mainActivity, BackgroundMusicService.class);
        checkIfPlayingIntent.setAction(BackgroundMusicService.ACTION_PAUSE_WHEN_CALLING);
        mainActivity.startService(checkIfPlayingIntent);
    }

    private void resumeAfterCalls() {
        final Intent checkIfPlayingIntent = new Intent(mainActivity, BackgroundMusicService.class);
        checkIfPlayingIntent.setAction(BackgroundMusicService.ACTION_RESUME_AFTER_CALL);
        mainActivity.startService(checkIfPlayingIntent);
    }


}