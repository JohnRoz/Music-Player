package com.example.user1.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button playPause;
    Button pause;
    Button skipNext;
    Button skipPrev;
    ListView listView;

    ArrayList<String> rawResourcesNames;
    ArrayList<Track> tracks;

    ArrayAdapter<Track> adapter;

    MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tracks = new ArrayList<>();

        rawResourcesNames = getRawResourcesNames();
        for (int i = 0; i<rawResourcesNames.size(); i++){
            tracks.add(new Track(rawResourcesNames.get(i)));
        }
        /*for(int i = 0; i < 3; i++)
            tracks.add(new Track());*/

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tracks);

        listView.setAdapter(adapter);



        playPause = (Button) findViewById(R.id.playPause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bohemian_rhapsody);
                mediaPlayer.start();
            }
        });

        pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.pause();
            }
        });



        /*MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource("res\\raw\\bohemian_rhapsody.mp3");

        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));*/


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
            try {
                //the name of the current resource
                resourceName = fields[i].getName();

                Uri uri = Uri.parse("android.resource://com.example.user1.musicplayer/raw/"+resourceName);

                //if(uri.getPath().endsWith(".mp3"))
                //the loop adds each name of each of the resources of raw to an ArrayList
                rawResourcesNames.add(resourceName);
                }

            catch (Exception e) {
                continue;
            }


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
