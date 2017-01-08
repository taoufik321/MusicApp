package com.application.musicapp;

/**
 * Created by oussama on 8-1-2017.
 */

public class Song {

    private String SongName;
    private String SongDWUrl;
    private String Category;


    public Song(String songName, String songDWUrl, String category) {
        SongName = songName;
        SongDWUrl = songDWUrl;
        Category = category;
    }


    public String getSongName() {
        return SongName;
    }

    public String getSongDWUrl() {
        return SongDWUrl;
    }

    public String getCategory() {
        return Category;
    }

    public Song(){

    }
}
