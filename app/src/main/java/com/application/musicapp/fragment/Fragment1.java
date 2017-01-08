package com.application.musicapp.fragment;
//
///**
// * Created by Taoufik on 25-12-2016.
// */
//
//import android.app.Fragment;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.MediaController;
//
//import com.application.musicapp.R;
//
//import java.util.ArrayList;
//
//public class Fragment1 extends Fragment {
//    public Fragment1() {
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment1, container, false);
//        return rootView;
//    }
//}



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.application.musicapp.MusicController;
import com.application.musicapp.MusicService;
import com.application.musicapp.R;
import com.application.musicapp.Song;
import com.application.musicapp.SongAdapter;

import android.app.Fragment;

public class Fragment1 extends Fragment implements MediaController.MediaPlayerControl {

    Menu menu;
    View view;
    public ArrayList<Song> songList;
    private ListView songListView;
    public MusicService musicSrv;
    private Intent playIntent;
    public boolean musicBound=false;

    public MusicController controller;
    private boolean paused=false, playbackPaused=false;

    // Required empty public constructor
    public Fragment1() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment1, container, false);

        songListView = (ListView) view.findViewById(R.id.song_list);        // Get reference of ListView in fragment_music layout
        //songList = new ArrayList<Song>();                                   // Create a new ArrayList of Song objects
        //songList = ((MainActivity) getActivity()).songList;                 // Get songs from MainActivity

        //Setting the songAdapter to the list View
        SongAdapter songAdt = new SongAdapter(getActivity(), songList);
        songListView.setAdapter(songAdt);

        //Play the song when list item is clicked
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                songPicked(view);
            }
        });

        setController();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
    }

    public void songPicked(View view){

        if(playbackPaused){
            setController();
            playbackPaused=false;
        }

        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();

        controller.show();
    }

    @Override
    public void onStart() {
        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused){
            //setController();
            paused=false;
        }
    }

    @Override
    public void onStop() {
        controller.hide();
        super.onStop();
    }

/*---- ServiceConnection for binding MusicService to MainActivity  ----*/

    public ServiceConnection musicConnection = new ServiceConnection(){

        //Gets executed when connection to service is made
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();      //get service
            Log.d("check", "Service connected");

            musicSrv.setList(songList);         //pass Song list to the service
            musicSrv.controller = controller;
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

/*---- MediaPlayerControl Implemented Methods ----*/

    @Override
    public void start() {
        playbackPaused=false;
        musicSrv.go();
        controller.show(0);
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicSrv.pausePlayer();
        //controller.show(0);
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && isPlaying())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && isPlaying())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    //set the controller up
    private void setController(){
        if(controller == null)
            controller = new MusicController(getActivity());
        else
            controller.invalidate();

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(view.findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    //play next
    private void playNext(){
        musicSrv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

}

