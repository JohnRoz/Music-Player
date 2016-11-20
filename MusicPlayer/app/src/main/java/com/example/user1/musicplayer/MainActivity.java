package com.example.user1.musicplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.playPause) ImageButton playPause;
    @BindView(R.id.stop) Button stop;
    @BindView(R.id.skipNext)Button skipNext;
    @BindView(R.id.skipPrev)Button skipPrev;
    @BindView(R.id.listView)ListView listView;

    private ArrayList<String> rawResourcesNames;
    private ArrayList<Track> tracks;

    private ArrayAdapter<Track> adapter;

    private MyBroadcastReceiver receiver;

    //private ArrayList<Track> tracksFromMemory; //This is for the external music files

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);//ButterKnife is awesome!


    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundMusicService.ACTION_CHECK_IF_PLAYING);
        registerReceiver(receiver, intentFilter);

        updateIsMusicPlaying();
        Toast.makeText(getApplicationContext(),"onStart, is music playing: "+receiver.isMusicPlaying,Toast.LENGTH_SHORT).show();
        if(receiver.isMusicPlaying)
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);


        //Setting 'tracks' as a new, empty ArrayList.
        tracks = new ArrayList<>();

        //Setting rawResourcesNames as a list of the names of the resources in 'raw' folder.
        rawResourcesNames = getRawResourcesNames();
        for (String name : rawResourcesNames)
            tracks.add(new Track(name, this));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tracks);
        listView.setAdapter(adapter);

        //When pressing the 'PlayPause' button:
        initPlayPauseButton();

        //When pressing the 'SkipNext' button
        initSkipNextButton();

        //When pressing the 'SkipPrev' button
        initSkipPrevButton();

        //When pressing the 'Stop' button
        initStopButton();

        //When pressing an item from the ListView
        onListViewItemClick();
    }

    private void onListViewItemClick() {
        //When an item from the ListView is pressed:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //changing the playPause button from 'Play Mode' to 'Pause Mode'
                playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                playPause.setTag(R.drawable.ic_pause_black_24dp);

                Intent playTrackIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                playTrackIntent.putExtra("tracks", tracks);
                playTrackIntent.putExtra("position",position);
                playTrackIntent.setAction("ACTION_PLAY_TRACK");

                //Starting the service with 'playTrackIntent' in order to tell the service to play the Tracks in the current position.
                startService(playTrackIntent);

            }
        });
    }

    private void initSkipPrevButton() {
        //When pressing the Skip Prev button
        skipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracks != null && !tracks.isEmpty()) {

                    //If the tag of the playPause is not defined, it means no song was chosen & no song is playing at the moment
                    //Therefore - do nothing.
                    if(playPause.getTag()==null)
                        return;

                    //changing the playPause button from 'Play Mode' to 'Pause Mode'
                    if (Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_play_arrow_black_24dp){
                        playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                        playPause.setTag(R.drawable.ic_pause_black_24dp);
                    }

                    final Intent skipPrevIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                    skipPrevIntent.setAction("ACTION_SKIP_PREV");

                    //The intent needs to have the tracks as an extra in order to set it in the service.
                    //The service needs to have the tracks in order to know what is the previous track.
                    skipPrevIntent.putExtra("tracks", tracks);

                    startService(skipPrevIntent);
                }
            }
        });
    }

    private void initSkipNextButton() {
        //When pressing the Skip Next button
        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracks != null && !tracks.isEmpty()) {

                    //If the tag of the playPause is not defined, it means no song was chosen & no song is playing at the moment
                    //Therefore - do nothing.
                    if(playPause.getTag()==null)
                        return;

                    //changing the playPause button from 'Play Mode' to 'Pause Mode'
                    if (Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_play_arrow_black_24dp){
                        playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                        playPause.setTag(R.drawable.ic_pause_black_24dp);
                    }

                    final Intent skipNextIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                    skipNextIntent.setAction("ACTION_SKIP_NEXT");

                    //The intent needs to have the tracks list as an extra in order to set it in the service.
                    //The service needs to have the tracksList in order to know what is the next track.
                    skipNextIntent.putExtra("tracks", tracks);

                    startService(skipNextIntent);
                }
            }
        });
    }

    private void initStopButton() {
        //When pressing the 'Stop' button
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, BackgroundMusicService.class);

                //Stop the Service.
                stopService(stopIntent);

                //If the tag of the playPause is not defined, it means no song was chosen & no song is playing at the moment
                //Therefore - do nothing.
                if(playPause.getTag()==null)
                    return;

                //switch the pause button to 'play'
                if(Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_pause_black_24dp) {
                    playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }

                //set the Tag to be NULL
                playPause.setTag(null);




            }
        });
    }

    private void initPlayPauseButton() {
        //When pressing the Play/Pause button:
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent playPauseIntent = new Intent(MainActivity.this, BackgroundMusicService.class);

                updateIsMusicPlaying();

                //TODO: NOTICE THAT THE CODE ENTERS THIS BLOCK *BEFORE* UPDATING 'receiver.isMusicPlaying'
                //YOU NEED TO FIGURE OUT WHY!
                if(receiver.isMusicPlaying){
                    Toast.makeText(getApplicationContext(),"setting ACTION_PAUSE", Toast.LENGTH_SHORT).show();
                    playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playPauseIntent.setAction("ACTION_PAUSE");
                }
                else{
                    Toast.makeText(getApplicationContext(),"setting ACTION_RESUME", Toast.LENGTH_SHORT).show();
                    playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                    playPauseIntent.setAction("ACTION_RESUME");
                }


                startService(playPauseIntent);


            }
        });
    }

    private void updateIsMusicPlaying() {
        //updating the receiver.isMusicPlaying
        final Intent checkIfPlayingIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
        checkIfPlayingIntent.setAction(BackgroundMusicService.ACTION_CHECK_IF_PLAYING);
        startService(checkIfPlayingIntent);
    }

    //The sinner function (but it's a necessary sin).
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
/*
    public void getTracksfromMemory(){

        tracksFromMemory = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst())
        {
            tracksFromMemory.add(new Track(songCursor));
             while(songCursor.moveToNext())
                 tracksFromMemory.add(new Track(songCursor));

            songCursor.close();
        }
    }
    */

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
