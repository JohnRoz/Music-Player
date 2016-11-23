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
    public static final String UNKNOWN_INFO = "<Unknown>";
    private final String ANDROID_RESOURCE_PATH = "android.resource://com.example.user1.musicplayer/raw/";
    private final String MY_PACKAGE = "com.example.user1.musicplayer";
    private final String RAW = "raw";
    private final int artistsMaxNameLength = 25;

    private String trackTitle;
    private String artist;
    private String album;
    private String durationInMilliseconds;
    private int durationInSeconds;

    private String uriString;


    private String tracksMinutes;//Number of minutes in the Track's length.
    private String tracksSeconds;//Number of seconds in the Track's length.
    private String tracksMinutesAndSeconds;//This will show the duration time in a [MINUTES:SECONDS] format.

    public Track(String resourceName, Context context){

        //An object to get the data that's 'deep inside' the audio file.
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        //A URI object to contain the path to the current file.
        //The URI is used to give the MediaMetadataRetriever the path to the file i want to use its data.
        Uri uri = Uri.parse(ANDROID_RESOURCE_PATH+resourceName);

        this.uriString = uri.toString();

        try {
            //Telling the MediaMetadataRetriever where to take the data from, using context & the URI from before.
            //Might throw IllegalArgumentException.
            mmr.setDataSource(context, uri);}
        catch(IllegalArgumentException ex){throw new RuntimeException();}

        //KEYS.
        String titleKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String durationKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artistKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String albumKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);


        if(titleKey != null)
            trackTitle = titleKey;
        else
            trackTitle = resourceName;


        //Setting the duration time of the Track.
        if(durationKey!=null) {
            //String of the duration time of the track in milliseconds.
            this.durationInMilliseconds = durationKey;

            //Integer of the duration time of the track in SECONDS.
            this.durationInSeconds = Integer.parseInt(durationInMilliseconds)/1000;

            //Number of minutes in the Track's length.
            tracksMinutes = durationInSeconds/60+"";

            //If the number of seconds in the Track's length is a one digit number,
            if((durationInSeconds%60)/10==0)
                //Then set it as "0[the number]".
                tracksSeconds = "0"+durationInSeconds%60;
            else
                tracksSeconds = durationInSeconds%60+"";

            tracksMinutesAndSeconds = tracksMinutes + ":" + tracksSeconds;

        }
        else tracksMinutesAndSeconds = UNKNOWN_INFO;

        //Setting the artist of the Track.
        if(artistKey != null)
            artist = artistKey;
        else
            artist = UNKNOWN_INFO;

        //If the artist's name(s) is equal to or longer than 25 characters,
        // just show the first 25 characters, and add "..."in the end.
        if(artist.length()>=artistsMaxNameLength)
            artist=artist.substring(0,artistsMaxNameLength) + "...";

        //Setting the album of the Track.
        if(albumKey != null)
            album = albumKey;
        else
            album = UNKNOWN_INFO;

    }

    public Track(Context context, String uriString){
        Uri uri = Uri.parse(uriString);

        this.uriString = uriString;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        try{
        mmr.setDataSource(context,uri);}
        catch(IllegalArgumentException ex){throw new RuntimeException();}

        //KEYS
        String titleKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String durationKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artistKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String albumKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        if(titleKey != null)
            this.trackTitle = titleKey;
        else this.trackTitle = UNKNOWN_INFO;

        //Setting the duration time of the Track.
        if(durationKey!=null) {
            //String of the duration time of the track in milliseconds.
            this.durationInMilliseconds = durationKey;

            //Integer of the duration time of the track in SECONDS.
            this.durationInSeconds = Integer.parseInt(durationInMilliseconds)/1000;

            //Number of minutes in the Track's length.
            tracksMinutes = durationInSeconds/60+"";

            //If the number of seconds in the Track's length is a one digit number,
            if((durationInSeconds%60)/10==0)
                //Then set it as "0[the number]".
                tracksSeconds = "0"+durationInSeconds%60;
            else
                tracksSeconds = durationInSeconds%60+"";

            tracksMinutesAndSeconds = tracksMinutes + ":" + tracksSeconds;

        }
        else tracksMinutesAndSeconds = UNKNOWN_INFO;

        //Setting the artist of the Track.
        if(artistKey != null)
            artist = artistKey;
        else
            artist = UNKNOWN_INFO;

        //If the artist's name(s) is equal to or longer than 25 characters,
        // just show the first 25 characters, and add "..."in the end.
        if(artist.length()>=artistsMaxNameLength)
            artist=artist.substring(0,artistsMaxNameLength) + "...";

        //Setting the album of the Track.
        if(albumKey != null)
            album = albumKey;
        else
            album = UNKNOWN_INFO;
    }

    @Override
    public String toString() {
        return trackTitle + "      " + tracksMinutesAndSeconds + "\n" + artist + " - " + album;
    }

    //Getters & setters
    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
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

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uri) {
        this.uriString = uri;
    }
}
