package com.application.musicapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.musicapp.adapter.SlidingMenuAdapter;
import com.application.musicapp.fragment.Fragment1;
import com.application.musicapp.fragment.Fragment2;
import com.application.musicapp.fragment.Fragment3;
import com.application.musicapp.model.ItemSlideMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaplayer;
    Button mPlay;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mSong = mRootRef.child("Song").child("SongDWURl");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mMediaplayer = new MediaPlayer();
        mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        MusicFromDownload();
    }

    public void MusicFromDownload(){
        mSong.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               String url = dataSnapshot.getValue(String.class);
               MediaPlayer mPlayer = new MediaPlayer();
               mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
               try {
                   mPlayer.setDataSource(url.toString());
               } catch (IllegalArgumentException e) {
                   Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
               } catch (SecurityException e) {
                   Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
               } catch (IllegalStateException e) {
                   Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
               } catch (IOException e) {
                   e.printStackTrace();
               }
               try {
                   mPlayer.prepare();
               } catch (IllegalStateException e) {
                   Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
               } catch (IOException e) {
                   Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
               }
               mPlayer.start();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }

       });

    }


    private void fetchAudioUrlFromFirebase() {
        mSong.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try {
                    // Download url of file
                    mMediaplayer.setDataSource(value);
                    // wait for media player to get prepare
                    mMediaplayer.setOnPreparedListener(MainActivity.this);
                    mMediaplayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FirebaseStorage storage = FirebaseStorage.getInstance();
// Create a storage reference from our app
        String text = "gs://musicapp-ed23e.appspot.com/stars_not-for-profit-use.mp3";
        StorageReference storageRef = storage.getReferenceFromUrl(text);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    final String url = uri.toString();
                    mMediaplayer.setDataSource(url);
                    // wait for media player to get prepare
                    mMediaplayer.setOnPreparedListener(MainActivity.this);
                    mMediaplayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
