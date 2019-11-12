package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static MP3Player mp3player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp3player = new MP3Player();
        mp3player.load("/sdcard/Download/a-ha - Take On Me .mp3");
        mp3player.play();
    }
}
