package com.example.user1.musicplayer;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;

/**
 * Created by USER1 on 14/10/2016.
 */

public class Track implements Serializable {
    private String name;
    private String artist;
    private String album;
    private int duration;
    private int ID;

    private String durationInMilliseconds;
    private String trackTitle;


    public Track(String resourceName, Context context){

        //An object to get the data that's 'deep inside' the mp3 file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        //a Uri object to contain the path to the current file.
        //the Uri is used to give the MediaMetadataRetriever the path to the file i want to use it's data.
        Uri path = Uri.parse("android.resource://com.example.user1.musicplayer/raw/"+resourceName);

        try {
            //telling the MediaMetadataRetriever where to take the data from, using context & the Uri
            //Might throw IllegalArgumentException
            mmr.setDataSource(context, path);}
        catch(IllegalArgumentException ex){ex.printStackTrace();}

        //Setting the name of the Track
        name = resourceName;
        trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);


        //Setting the duration time of the Track
        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!=null) {
            //string of the duration time of the track in milliseconds
            durationInMilliseconds = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            //integer of the duration time of the track in SECONDS
            duration = (Integer.parseInt(durationInMilliseconds)/1000);

        }

        //Setting the artist of the Track
        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) != null)
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        else
            artist = "<Unknown>";

        //Setting the album of the Track
        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) != null)
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        else
            album = "<Unknown>";

        //Setting the ID of the current resource file
        ID = context.getResources().getIdentifier(resourceName, "raw", "com.example.user1.musicplayer");
    }


    @Override
    public String toString(){
        //if the duration time of the track's info is found in the mp3 file.
        // (if the METADATA_KEY_DURATION returns something that isn't null).
        //  meaning that the var 'duration' holds the length of the track in SECONDS.
        //This will show the duration time in a [MINUTES:SECONDS] format.
        if(durationInMilliseconds != null) {

            //if the METADATA_KEY_TITLE returns something that isn't null
            if (trackTitle != null)
                return trackTitle + "      " + duration / 60/*MINUTES*/ + ":" + duration % 60/*SECONDS*/ + "\n" + artist + " - " + album;

            //if it returns Null, just use the name of the resource as it is
            return name + "      " + duration / 60/*MINUTES*/ + ":" + duration % 60/*SECONDS*/ + "\n" + artist + " - " + album;
        }
        return name + "      "+ "<Unknown>" + "\n" + artist + " - " + album;
    }



    //Getters & setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationInSecs() {
        return duration;
    }

    public void setDurationInSecs(int durationInSecs) {
        this.duration = durationInSecs;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
