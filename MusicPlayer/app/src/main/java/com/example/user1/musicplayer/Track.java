package com.example.user1.musicplayer;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;

/**
 * Created by USER1 on 14/10/2016.
 */

public class Track {
    private String name;
    private int durationInSecs;
    private String artist;
    private String album;
    private String duration;
    private int ID;


    public Track(String resourceName, Context context){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource("C:\\Users\\USER1\\Documents\\GitHub\\Music-Player\\MusicPlayer\\app\\src\\main\\res\\raw\\"
                    + resourceName + ".mp3");
        }
        catch(IllegalArgumentException ex){ex.printStackTrace();}

        name = resourceName;

        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!=null)
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        else
            duration = "<Unknown>";

        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) != null)
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        else
            artist = "<Unknown>";

        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) != null)
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        else
            album = "<Unknown";

        ID = context.getResources().getIdentifier(resourceName, "raw", "com.example.user1.musicplayer");
    }

    public Track(){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try{mmr.setDataSource("C:\\Users\\USER1\\Documents\\GitHub\\Music-Player\\MusicPlayer\\app\\src\\main\\res\\raw\\antonio_vivaldi_winter.mp3");}
        catch(IllegalArgumentException ex){ex.printStackTrace();}

        name = "antonio_vivaldi_winter";
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }

    @Override
    public String toString(){
        return name + "      "+ duration + "\n" + artist + " - " + album;
    }



    //Getters & setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationInSecs() {
        return durationInSecs;
    }

    public void setDurationInSecs(int durationInSecs) {
        this.durationInSecs = durationInSecs;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
