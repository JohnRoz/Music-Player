package com.example.user1.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by USER1 on 14/10/2016.
 */

public class Track implements Serializable {
    private static final String ANDROID_RESOURCE_PATH = "android.resource://com.example.user1.musicplayer/raw/";
    public static final String UNKNOWN_INFO = "<Unknown>";
    private static final String MY_PACKAGE = "com.example.user1.musicplayer";
    private static final String RAW = "raw";

    private String fileName;
    private String trackTitle;
    private String artist;
    private String album;
    private String durationInMilliseconds;
    private int durationInSeconds;
    private int ID;

    private boolean isResource;
    private String uriString;


    private String tracksMinutes;//number of minutes in the Track's length.
    private String tracksSeconds;//number of seconds in the Track's length.
    private String tracksMinutesAndSeconds;//This will show the duration time in a [MINUTES:SECONDS] format.

    public Track(String resourceName, Context context){

        //An object to get the data that's 'deep inside' the mp3 file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        //a Uri object to contain the path to the current file.
        //the Uri is used to give the MediaMetadataRetriever the path to the file i want to use it's data.
        Uri uri = Uri.parse(ANDROID_RESOURCE_PATH+resourceName);

        this.uriString = uri.toString();

        try {
            //telling the MediaMetadataRetriever where to take the data from, using context & the Uri
            //Might throw IllegalArgumentException
            mmr.setDataSource(context, uri);}
        catch(IllegalArgumentException ex){throw new RuntimeException();}

        //Setting the name of the Track & the Title of the Track
        fileName = resourceName;

        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
            trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        else
            trackTitle = fileName;


        //Setting the duration time of the Track
        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!=null) {
            //string of the duration time of the track in milliseconds
            durationInMilliseconds = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            //integer of the duration time of the track in SECONDS
            durationInSeconds = (Integer.parseInt(durationInMilliseconds)/1000);

            //number of minutes in the Track's length.
            tracksMinutes = durationInSeconds/60+"";

            //if the number of seconds in the Track's length is a one digit number,
            if((durationInSeconds%60)/10==0)
                //then set it as "0[the number]".
                tracksSeconds = "0"+durationInSeconds%60;
            else
                tracksSeconds = durationInSeconds%60+"";

            tracksMinutesAndSeconds = tracksMinutes + ":" + tracksSeconds;

        }
        else tracksMinutesAndSeconds = UNKNOWN_INFO;

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


        isResource = true;
    }

    //TODO: create another constructor for music files from the memory
    /*public Track(Cursor songCursor){

        String title = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//Title of the file
        String artist = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//Artist of the file
        String album = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//Album of the file
        String duration = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//Duration of the file in milliseconds

        if(title != null)
            this.trackTitle = title;
        else this.trackTitle = UNKNOWN_INFO;

        if(artist != null)
            this.artist = artist;
        else this.artist = UNKNOWN_INFO;

        if(album != null)
            this.album = album;
        else this.album = UNKNOWN_INFO;

        if (duration != null) {
            this.durationInMilliseconds = duration;

            //integer of the duration time of the track in SECONDS
            durationInSeconds = (Integer.parseInt(durationInMilliseconds)/1000);

            //number of minutes in the Track's length.
            tracksMinutes = durationInSeconds/60+"";

            //if the number of seconds in the Track's length is a one digit number,
            if((durationInSeconds%60)/10==0)
                //then set it as "0[the number]".
                tracksSeconds = "0"+durationInSeconds%60;
            else
                tracksSeconds = durationInSeconds%60+"";

            this.tracksMinutesAndSeconds = tracksMinutes + ":" + tracksSeconds;
        }
        else this.tracksMinutesAndSeconds = UNKNOWN_INFO;

        this.ID = 0;
        this.fileName = "";
        isResource = false;

    }*/


    @Override
    public String toString(){
        return trackTitle + "      " + tracksMinutesAndSeconds + "\n" + artist + " - " + album;
    }



    //Getters & setters


    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDurationInSecs() {
        return durationInSeconds;
    }

    public void setDurationInSecs(int durationInSecs) {
        this.durationInSeconds = durationInSecs;
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

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uri) {
        this.uriString = uri;
    }
}
