package com.example.user1.musicplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//Importing static constants from BackgroundMusicService.
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_RESUME_AFTER_CALL;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_INIT_SERVICE;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_PLAY_TRACK;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_PAUSE;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_RESUME;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_SKIP_NEXT;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_SKIP_PREV;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE;
import static com.example.user1.musicplayer.BackgroundMusicService.ACTION_PAUSE_WHEN_CALLING;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.playPause)
    ImageButton playPause;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.skipNext)
    Button skipNext;
    @BindView(R.id.skipPrev)
    Button skipPrev;
    @BindView(R.id.listView)
    ListView listView;

    private ArrayList<Track> tracks;
    private MyBroadcastReceiver receiver;

    public static final String position = "position";
    public static final String isPlaying = "isPlaying";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);//ButterKnife is awesome!

        //Initiating the PhoneStateListener.
        initMyPhoneStateListener();

        //Initiating the BroadcastReceiver.
        registerBroadcastReceiver();

        //Initiating 'tracks' as a new, empty ArrayList.
        tracks = new ArrayList<>();

        //Initiating rawResourcesNames as a list of the names of the resources in 'raw' folder (the sinner function).
        ArrayList<String> rawResourcesNames = getRawResourcesNames();
        for (String name : rawResourcesNames)
            tracks.add(new Track(name, this));

        ArrayAdapter<Track> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tracks);
        listView.setAdapter(adapter);

        //This is for Level 4 - an audio file outside of the app so the app offers itself to play it.
        initIntentWithAudioFile(adapter);

        //Initiate the Service. give it the list of the Tracks.
        initService();

        //When pressing the 'PlayPause' button.
        initPlayPauseButton();

        //When pressing the 'SkipNext' button.
        initSkipNextButton();

        //When pressing the 'SkipPrev' button.
        initSkipPrevButton();

        //When pressing the 'Stop' button.
        initStopButton();

        //When pressing an item from the ListView.
        initListViewItems();
    }

    /**
     * This is for Level 4 - an audio file outside of the app so the app offers itself to play it.
     * @param adapter An ArrayAdapter to set the adapter for the ListView
     */
    private void initIntentWithAudioFile(ArrayAdapter<Track> adapter) {
        if (getIntent().getData() != null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();

            //Add the selected file to the ArrayList as a new Track.
            Track newTrack = new Track(this, uri.toString());
            tracks.add(newTrack);
            adapter.notifyDataSetChanged();

            //Re-initiate the Service. give it the list of the Tracks in order to update it.
            initService();

            //Changing the playPause button from 'Play Mode' to 'Pause Mode'.
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);

            Intent intent = new Intent(MainActivity.this, BackgroundMusicService.class);
            intent.setAction(ACTION_PLAY_TRACK);
            intent.putExtra(position, tracks.size() - 1);
            startService(intent);
        }
    }

    //just some minor initiate functions to make the onCreate() easier to read.
    private void initService() {
        //Initiate the Service. give it the list of the Tracks.
        Intent initServiceIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
        initServiceIntent.putExtra("tracks", tracks);
        initServiceIntent.setAction(ACTION_INIT_SERVICE);
        startService(initServiceIntent);
    }

    private void registerBroadcastReceiver() {
        receiver = new MyBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE);
        intentFilter.addAction(ACTION_PAUSE_WHEN_CALLING);
        intentFilter.addAction(ACTION_RESUME_AFTER_CALL);
        registerReceiver(receiver, intentFilter);
    }

    private void initMyPhoneStateListener() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tManager.listen(new MyPhoneStateListener(this), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initListViewItems() {
        //When an item from the ListView is pressed:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Changing the playPause button from 'Play Mode' to 'Pause Mode'.
                playPause.setImageResource(R.drawable.ic_pause_black_24dp);

                Intent playTrackIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                playTrackIntent.putExtra(MainActivity.position, position);
                playTrackIntent.setAction(ACTION_PLAY_TRACK);

                //Starting the service with 'playTrackIntent' in order to tell the service to play the Tracks in the current position.
                startService(playTrackIntent);

            }
        });
    }

    private void initSkipPrevButton() {
        //When pressing the Skip Prev button.
        skipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipByAction(ACTION_SKIP_PREV);
            }
        });
    }

    private void initSkipNextButton() {
        //When pressing the Skip Next button.
        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipByAction(ACTION_SKIP_NEXT);
            }
        });
    }

    private void skipByAction(String action) {
        if (tracks != null && !tracks.isEmpty()) {
            //Changing the playPause button from 'Play Mode' to 'Pause Mode'.
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);

            final Intent skipNextIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
            skipNextIntent.setAction(action);

            startService(skipNextIntent);
        }
    }

    private void initStopButton() {
        //When pressing the 'Stop' button.
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, BackgroundMusicService.class);

                //Stop the Service.
                stopService(stopIntent);

                //Switch the pause button to 'play'.
                playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });
    }

    private void initPlayPauseButton() {
        //When pressing the Play/Pause button.
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent checkIfPlayingIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                checkIfPlayingIntent.setAction(ACTION_BROADCAST_IF_PLAYING_FOR_PLAYPAUSE);
                startService(checkIfPlayingIntent);

            }
        });
    }

    /**
     * This function is used in the onReceive() func in MyBroadcastReceiver.
     * If isPlaying is true, this function will pause the music.
     * If isPlaying is false, this function will resume the music.
     *
     * @param isPlaying is true if the MediaPlayer is playing, false otherwise.
     */
    public void playPause(boolean isPlaying) {
        //Pause
        pause(isPlaying);

        //Resume
        resume(isPlaying);

    }

    /**
     * This function is used in the onReceive() func in MyBroadcastReceiver.
     * If isPlaying is true, this function will pause the music.
     *
     * @param isPlaying is true if the MediaPlayer is playing, false otherwise.
     */
    public void pause(boolean isPlaying) {
        //Pause
        if (isPlaying) {
            final Intent pauseIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            pauseIntent.setAction(ACTION_PAUSE);
            startService(pauseIntent);
        }

    }

    /**
     * This function is used in the onReceive() func in MyBroadcastReceiver.
     * If isPlaying is false, this function will resume the music.
     *
     * @param isPlaying is true if the MediaPlayer is playing, false otherwise.
     */
    public void resume(boolean isPlaying) {
        //Resume
        if (!isPlaying) {
            final Intent resumeIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);
            resumeIntent.setAction(ACTION_RESUME);
            startService(resumeIntent);
        }
    }


    /**
     * The sinner function (but it's a necessary sin).
     *
     * @return The ArrayList of the names of the resources in R.raw .
     */
    private ArrayList<String> getRawResourcesNames() {

        //an ArrayList to contain the names of the resources of raw.
        //This is the ArrayList to be returned.
        ArrayList<String> rawResourcesNames = new ArrayList<>();

        // (I THINK! = NOT SURE) this is an array of copies of the resources of the raw class
        final Field[] fields = R.raw.class.getDeclaredFields();

        //the loop runs as the length of the 'fields' array -
        // meaning, as the number of the resources raw class has in it
        for (Field field : fields) {
            final String resourceName;
            //the name of the current resource
            resourceName = field.getName();

            //if the id of the resource isn't 0 (if it is it brings up problems)
            if (getApplicationContext().getResources().getIdentifier(resourceName, "raw", "com.example.user1.musicplayer") != 0)
                //the loop adds each name of each of the resources of raw to an ArrayList
                rawResourcesNames.add(resourceName);


        }

        //The list of names of the resources of the raw class is being returned
        return rawResourcesNames;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
