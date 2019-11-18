package com.example.mp3player;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;

import androidx.core.app.NotificationCompat;

import static com.example.mp3player.MyNotification.CHANNEL_ID;


public class MyService extends Service {

    //initialize objects
    MP3Player mp3player = new MP3Player();
    private final IBinder myBinder = new MyLocalBinder();


    //executes when startservice is called and starts the service and foreground notification
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //if a song is playing or paused, notification will display song played or paused
        if(mp3player.getState() == MP3Player.MP3PlayerState.PLAYING || mp3player.getState() == MP3Player.MP3PlayerState.PAUSED ){
        String songName = removeSuffix(extractName(getSongName()),".mp3");

            Intent notificationIntent = new Intent(this,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("MP3Player")
                    .setContentText(songName)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);

        }

        //if no song is playing, notification will simply display "app running in foreground"
        else {
            String songName = "App Running in Foreground";

            Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MP3Player")
                .setContentText(songName)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        }

        return START_NOT_STICKY;
    } // end of method onStartCommand()

    //Binding methods
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    //method to extract file name from path
    public String extractName(String s){
        String fileName = s.substring(s.lastIndexOf("/") +1, s.length());
        return fileName;
    }

    // method to remove suffix of file name
    public String removeSuffix(final String s, final String suffix){
        if (s != null && suffix != null && s.endsWith(suffix)){
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    //mp3player playback methods
    public void play() { mp3player.play(); }

    public void pause() { mp3player.pause(); }

    public void stop(){
        mp3player.stop();
    }

    public int getDuration(){
        return mp3player.getDuration();
    }

    public int getProgress(){
        return mp3player.getProgress();
    }

    public String getSongName(){
        return mp3player.getFilePath();
    }

    //method to obtain remaining time of song when played
    public String getFormattedDuration(long msec) {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int) (msec / (1000 * 60 * 60));
        int minutes = (int) (msec % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((msec % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) { finalTimerString = hours + ":"; }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) { secondsString = "0" + seconds; }
        else { secondsString = "" + seconds; }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    //method to stop the service
    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
