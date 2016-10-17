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
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button playPause;
    Button stop;
    Button skipNext;
    Button skipPrev;
    ListView listView;

    ArrayList<String> rawResourcesNames;
    ArrayList<Track> tracks;

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
        playPause = (Button) findViewById(R.id.playPause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //When pressing the 'Stop' button:
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Creates a new Intent without any extras, in order to let the activity know
                *the destination of the Intent*/
                final Intent intent = new Intent(MainActivity.this, BackgroundMusicService.class);

                //Starting the service with an Intent with no extras in order to tell the service to stop the music.
                new AsyncTask<Intent,Void,Void>(){
                    @Override
                    protected Void doInBackground(Intent... params) {
                        startService(intent);
                        return null;
                    }
                }.execute();
            }
        });

        //When an item from the ListView is pressed:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //The Track that was pressed
                Track track = adapter.getItem(position);

                //A new Intent containing the pressed Track as an extra.
                final Intent intent = new Intent(MainActivity.this, BackgroundMusicService.class);
                intent.putExtra("track", track);

                //Starting the service with an Intent the Track as an extra in order to tell the service to play the Track.
                new AsyncTask<Intent,Void,Void>(){
                    @Override
                    protected Void doInBackground(Intent... params) {
                        startService(intent);
                        return null;
                    }
                }.execute();

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
            try {
                //the name of the current resource
                resourceName = fields[i].getName();

                //if the id of the resource isn't 0 (if it is it brings up problems)
                if(getApplicationContext().getResources().getIdentifier(resourceName, "raw", "com.example.user1.musicplayer")!=0)
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
