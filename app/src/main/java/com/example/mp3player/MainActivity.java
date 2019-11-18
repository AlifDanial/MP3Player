package com.example.mp3player;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;



public class MainActivity extends AppCompatActivity {

    //declare global variables
    MyService mp3Service;
    ImageButton playButton;
    ImageButton pauseButton;
    ListView musicList;
    SeekBar seekBar;
    TextView progressText;
    TextView durationText;
    TextView songName;
    private boolean STOP_FREQ_UPDATE = false;
    boolean isBound = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hide title bar
        getSupportActionBar().hide();

        //reference widget controller objects
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setVisibility(View.INVISIBLE);
        progressText = findViewById(R.id.progressText);
        durationText = findViewById(R.id.durationText);
        songName = findViewById(R.id.songName);
        seekBar = findViewById(R.id.seekBar);
        musicList = findViewById(R.id.listView);
        seekBar.setEnabled(false);

        //initialize class MyService
        Intent intent = new Intent(this, MyService.class);
        if(bindService(intent,myConnection,0)){
            startService(intent);
        }

        //get directory of songs and pass to listview
        File musicDir = new File(Environment.getExternalStorageDirectory().getPath()+ "/Music/");
        File songs[] = musicDir.listFiles();
        musicList.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, songs));

        //load songs when clicked on and play a song at a time
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> CustomAdapter, View myView, int myItemInt, long id) {
                File selectedFromList = (File) (musicList.getItemAtPosition(myItemInt));
                if(mp3Service.mp3player.getState() == MP3Player.MP3PlayerState.PAUSED || mp3Service.mp3player.getState() == MP3Player.MP3PlayerState.STOPPED ){
                    mp3Service.mp3player.load(selectedFromList.getAbsolutePath());
                    DisplayName(selectedFromList);
                    seekBar.postDelayed(updateProgressEverySecond, 1000);
                    seekBar.setEnabled(true);
                }

                if(mp3Service.mp3player.getState()== MP3Player.MP3PlayerState.PLAYING){
                    mp3Service.stop();
                    mp3Service.mp3player.load(selectedFromList.getAbsolutePath());
                    DisplayName(selectedFromList);
                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                    seekBar.postDelayed(updateProgressEverySecond, 1000);
                    seekBar.setEnabled(true);
                }
            }
        });

        //update previous progress of mp3 song and song name on activity restart
        seekBar.postDelayed(updateProgressEverySecond, 1000);
        seekBar.setEnabled(true);
        progressText.setText("00:00");
        durationText.setText("00:00");
        songName.setText("CHOOSE A TRACK");

    }//end onCreate()

    //initialized to bind the activity and the service
    private ServiceConnection myConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyLocalBinder binder = (MyService.MyLocalBinder) service;
            mp3Service = binder.getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    //onclick methods
    public void onClickPlay(View v){
        mp3Service.play();
        seekBar.postDelayed(updateProgressEverySecond, 1000);
        seekBar.setEnabled(true);
        pauseButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);
    }//end onClickPlay

    public void onClickPause(View v){
        mp3Service.pause();
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
    }//end onClickPause

    public void onClickStop(View v){
        mp3Service.stop();
        seekBar.setProgress(0);
        songName.setText("CHOOSE A TRACK");
        progressText.setText("00:00");
        durationText.setText("00:00");
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }//end onClickStop

    //display name method to display name of played song
    public void DisplayName(File Name){
        String sName = Name.getName();
        String fName = removeSuffix(sName,".mp3");
        songName.setText(fName);
    }

    //method to extract file name from file path
    public String extractName(String s){
        String fileName = s.substring(s.lastIndexOf("/") +1, s.length());
        return fileName;
    }

    //method to remove mp3 suffix on song names
    public String removeSuffix(final String s, final String suffix){
        if (s != null && suffix != null && s.endsWith(suffix)){
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    //runnable method to update progress of seekbar, mp3 song progress and obtain duration
    private Runnable updateProgressEverySecond = new Runnable() {
        @Override
        public void run() {
            if (!STOP_FREQ_UPDATE && (mp3Service.getDuration() != 0)) {
                int prog = mp3Service.getProgress() * seekBar.getMax() / mp3Service.getDuration();

                seekBar.setProgress(prog);

                if(mp3Service.mp3player.getState().equals(MP3Player.MP3PlayerState.PLAYING)) {
                    seekBar.postDelayed(updateProgressEverySecond, 1000);
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.INVISIBLE);
                }

                progressText.setText(mp3Service.getFormattedDuration(mp3Service.getProgress()));
                durationText.setText(mp3Service.getFormattedDuration(mp3Service.getDuration()));
                songName.setText(removeSuffix(extractName(mp3Service.getSongName()),".mp3"));


            }
        } // end of method run()
    };


}
