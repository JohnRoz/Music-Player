package com.example.user1.musicplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageButton playPause;
    Button stop;
    Button skipNext;
    Button skipPrev;
    ListView listView;

    ArrayList<String> rawResourcesNames;
    ArrayList<Track> tracks;
    ArrayList<Track> tracksList;

    ArrayAdapter<Track> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tracks = new ArrayList<>();

        rawResourcesNames = getRawResourcesNames();
        for (int i = 0; i<rawResourcesNames.size(); i++){
            tracks.add(new Track(rawResourcesNames.get(i), this));
        }

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tracks);

        listView.setAdapter(adapter);


        //When pressing the Play/Pause button:
        playPause = (ImageButton) findViewById(R.id.playPause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent playPauseIntent = new Intent(MainActivity.this, BackgroundMusicService.class);

                //changing the playPause button from 'Pause Mode' to 'Play Mode'
                //Telling the service to PAUSE PLAYING
                if(Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_pause_black_24dp) {
                    playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playPause.setTag(R.drawable.ic_play_arrow_black_24dp);
                    playPauseIntent.setAction("ACTION_PAUSE");
                }

                //changing the playPause button from 'Play Mode' to 'Pause Mode'
                //Telling the service to RESUME PLAYING
                else if(Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_play_arrow_black_24dp){
                    playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                    playPause.setTag(R.drawable.ic_pause_black_24dp);
                    playPauseIntent.setAction("ACTION_RESUME");
                }

                startService(playPauseIntent);


            }
        });

        //When pressing the Skip Next button
        skipNext = (Button) findViewById(R.id.skipNext);
        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracksList != null && !tracksList.isEmpty()) {

                    //changing the playPause button from 'Play Mode' to 'Pause Mode'
                    if (Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_play_arrow_black_24dp){
                        playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                        playPause.setTag(R.drawable.ic_pause_black_24dp);
                    }

                    final Intent skipNextIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                    skipNextIntent.setAction("ACTION_SKIP_NEXT");

                    //The intent needs to have the tracks list as an extra in order to set the tracksList in the service.
                    //The service needs to have the tracksList in order to know what is the next track.

                    //***im removing the first track in the traksList & tell the service to play it***
                    tracksList.remove(0);
                    skipNextIntent.putExtra("tracksList", tracksList);

                    startService(skipNextIntent);
                }
            }
        });


        //When pressing the 'Stop' button:
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, BackgroundMusicService.class);

                //Stop the Service.
                stopService(stopIntent);

                //switch the pause button to 'play'
                if(Integer.parseInt(playPause.getTag().toString()) == R.drawable.ic_pause_black_24dp) {
                    playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playPause.setTag(R.drawable.ic_play_arrow_black_24dp);
                }


            }
        });

        //***TODO: I need to change the system so the service gets the ArrayList 'tracks' & a position,
        // TODO: and starts playing from that position onward. This is in order to allow the user to use the 'skipBack' button.

        //When an item from the ListView is pressed:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //changing the playPause button from 'Play Mode' to 'Pause Mode'
                playPause.setImageResource(R.drawable.ic_pause_black_24dp);
                playPause.setTag(R.drawable.ic_pause_black_24dp);


                //A list of tracks containing all the tracks in the 'tracks' ArrayList, from the current position till the end.
                tracksList = new ArrayList<>();
                for(int i = position; i < tracks.size(); i++)
                    tracksList.add(tracks.get(i));

                //Define 'serviceIntent' as a new Intent containing the list of all
                //Tracks from the current position till the end as an extra.


                Intent playTrackIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                playTrackIntent.putExtra("tracksList", tracksList);
                playTrackIntent.setAction("ACTION_PLAY_TRACK");

                //Starting the service with 'serviceIntent' in order to tell the service to play the Tracks in the tracksList.
                startService(playTrackIntent);

            }
        });

    }

    private ArrayList<String> getRawResourcesNames() {

        //an ArrayList to contain the names of the resources of raw.
        //This is the ArrayList to be returned.
        ArrayList<String> rawResourcesNames = new ArrayList<>();

        // (I THINK! = NOT SURE) this is an array of copies of the resources of the raw class
        final Field[] fields = R.raw.class.getDeclaredFields();

        //the loop runs as the length of the 'fields' array -
        // meaning, as the number of the resources raw class has in it
        for (int i = 0; i < fields.length; i++) {
            final String resourceName;
                //the name of the current resource
                resourceName = fields[i].getName();

                //if the id of the resource isn't 0 (if it is it brings up problems)
                if(getApplicationContext().getResources().getIdentifier(resourceName, "raw", "com.example.user1.musicplayer")!=0)
                    //the loop adds each name of each of the resources of raw to an ArrayList
                    rawResourcesNames.add(resourceName);


        }

        //The list of names of the resources of the raw class is being returned
        return rawResourcesNames;
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
