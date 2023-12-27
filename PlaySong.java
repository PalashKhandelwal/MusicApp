package com.example.imusic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PlaySong extends AppCompatActivity {
    @Override
            protected void onDestroy(){
                super.onDestroy();
                mediaPlayer.stop();
                mediaPlayer.release();
                updateSeek.interrupt();
            }

    private final Handler handler = new Handler();
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer != null){
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                startTimeTextView.setText(getFormattedTime(currentPosition));
            }
            handler.postDelayed(this, 1000);
        }
        private String getFormattedTime(int timeInMilliseconds){
            int minutes = timeInMilliseconds / 60000;
            int seconds = (timeInMilliseconds % 60000) / 1000;
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }

    };

    TextView textView, startTimeTextView, endTimeTextView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    MediaPlayer mediaPlayer;
    String textContent;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null && bundle.containsKey("songList")){
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        }else{
            Toast.makeText(this, "No songs found.", Toast.LENGTH_SHORT).show();
        }


        textContent = intent.getStringExtra("currentSong");
        if (textContent != null) {
            textView.setText(textContent);
            textView.setSelected(true);
        } else {
            Toast.makeText(this, "Current Song: " + null, Toast.LENGTH_SHORT).show();
            // Handle the case where "CurrentSong" is null.
            // You can set a default text or show an error message.
        }
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        handler.post(updateSeekBar);
        int minutes = mediaPlayer.getDuration() / 60000;
        int seconds = (mediaPlayer.getDuration() % 60000) / 1000;
        String totalTime = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        endTimeTextView.setText(totalTime);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int minutes = progress / 60000;
                    int seconds = (progress%60000) / 1000;
                    String currentTime = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);

                    //setting the starting time
                    startTimeTextView.setText(currentTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                mediaPlayer.seekTo(seekBar.getProgress());
                }
                assert mediaPlayer != null;
                int minutes = mediaPlayer.getDuration() / 60000;
                int seconds = (mediaPlayer.getDuration() % 60000) / 1000;
                String totalTime = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
                //setting the end time on textview
                endTimeTextView.setText(totalTime);
            }

        });
        updateSeek = new Thread(){
            @Override
                    public void run(){
                        int currentPosition = 0;
                        try{
                            while(currentPosition<mediaPlayer.getDuration()){
                                currentPosition = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(currentPosition);
                                sleep(800);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                }
            }
        };
        updateSeek.start();
        play.setOnClickListener(view -> {
            if(mediaPlayer.isPlaying()){
                play.setImageResource(R.drawable.play);
                mediaPlayer.pause();
            }else {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        });
        previous.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position!=0){
                position = position-1;
            }
            else{
                position = songs.size() - 1;
            }
            Uri uri1 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);
            mediaPlayer.start();
            resetSeekbar();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).getName();
            textView.setText(textContent);
            int minutes1 = mediaPlayer.getDuration() / 60000;
            int seconds1 = (mediaPlayer.getDuration() % 60000) / 1000;
            String totalTime1 = String.format(Locale.getDefault(), "%d:%02d", minutes1, seconds1);
            endTimeTextView.setText(totalTime1);
        });

        next.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position!=songs.size()-1){
                position = position+1;
            }
            else{
                position = 0;
            }
            Uri uri12 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri12);
            mediaPlayer.start();
            resetSeekbar();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).getName();
            textView.setText(textContent);
            int minutes12 = mediaPlayer.getDuration() / 60000;
            int seconds12 = (mediaPlayer.getDuration() % 60000) / 1000;
            String totalTime12 = String.format(Locale.getDefault(), "%d:%02d", minutes12, seconds12);
            endTimeTextView.setText(totalTime12);
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.stop();
            mp.release();
            if(position!=songs.size()-1){
                position = position+1;
            }
            else{
                position = 0;
            }
            Uri uri13 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri13);
            mediaPlayer.start();
            resetSeekbar();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).getName();
            textView.setText(textContent);
        });

    }
    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onBackPressed();
    }

    private void resetSeekbar(){
        seekBar.setProgress(0);
    }
}