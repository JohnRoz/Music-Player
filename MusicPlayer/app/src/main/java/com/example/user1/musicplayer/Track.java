package com.example.user1.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by USER1 on 14/10/2016.
 */

public class Track implements Serializable {
    public static final String ANDROID_RESOURCE_PATH = "android.resource://com.example.user1.musicplayer/raw/";
    public static final String UNKNOWN_INFO = "<Unknown>";
    public static final String MY_PACKAGE = "com.example.user1.musicplayer";
    public static final String RAW = "raw";

    private String name;
    private String artist;
    private String album;
    private int duration;
    private int ID;

    private String durationInMilliseconds;
    private String trackTitle;


    private String tracksMinutes;//number of minutes in the Track's length.
    private String tracksSeconds;//number of seconds in the Track's length.

    public Track(String resourceName, Context context){

        //An object to get the data that's 'deep inside' the mp3 file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        //a Uri object to contain the path to the current file.
        //the Uri is used to give the MediaMetadataRetriever the path to the file i want to use it's data.
        Uri path = Uri.parse(ANDROID_RESOURCE_PATH+resourceName);

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

            //number of minutes in the Track's length.
            tracksMinutes = duration/60+"";

            //if the number of seconds in the Track's length is a one digit number,
            if((duration%60)/10==0)
                //then set it as "0[the number]".
                tracksSeconds = "0"+duration%60;
            else
                tracksSeconds = duration%60+"";

        }

        //Setting the artist of the Track
        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null)
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        else
            artist = UNKNOWN_INFO;

        //if the artist's name(s) is equal to or longer than 25 characters,
        // just show the first 25 characters, and add "..."in the end
        if(artist.length()>=25)
            artist=artist.substring(0,25) + "...";

        //Setting the album of the Track
        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) != null)
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        else
            album = UNKNOWN_INFO;

        //Setting the ID of the current resource file
        ID = context.getResources().getIdentifier(resourceName, RAW, MY_PACKAGE);
    }

    //TODO: create another constructor for music files from the memory
    public Track(){}


    @Override
    public String toString(){
        //if the duration time of the track's info is found in the mp3 file.
        // (if the METADATA_KEY_DURATION returns something that isn't null).
        //  meaning that the var 'duration' holds the length of the track in SECONDS.
        //This will show the duration time in a [MINUTES:SECONDS] format.
        if(durationInMilliseconds != null) {

            //if the METADATA_KEY_TITLE returns something that isn't null
            if (trackTitle != null)
                return trackTitle + "      " + tracksMinutes + ":" + tracksSeconds + "\n" + artist + " - " + album;

            //if it returns Null, just use the name of the resource as it is
            return name + "      " + tracksMinutes + ":" + tracksSeconds + "\n" + artist + " - " + album;
        }
        return name + "      "+ UNKNOWN_INFO + "\n" + artist + " - " + album;
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
