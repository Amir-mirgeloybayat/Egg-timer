package com.example.eggtimer;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button timerButton;
    ImageView eggImage;
    SeekBar timerSeekBar;
    TextView timerText;
    CountDownTimer countDownTimer;
    MediaPlayer mediaPlayer;
    final int MAX_TIME = 720; //in seconds (12 minutes)
    final int SEEK_BAR_STEP = 5;
    boolean timerActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.egg_alarm);
        timerButton = findViewById(R.id.timer_button);
        eggImage = findViewById(R.id.egg_picture);
        timerSeekBar = findViewById(R.id.timer_seekBar);
        timerText = findViewById(R.id.timer_text);

        //We like allow the Seek bar to traverse in units of 5 seconds. [0 - 144 instead of 0 - 720]
        //because changing the seek bar by each second can be overwhelming to the user.
        timerSeekBar.setMax(MAX_TIME / SEEK_BAR_STEP);
        timerSeekBar.setProgress(0);
        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress is [0,144] but seconds need a multiplier of 5 to be accurate.
                updateTimerText(progress * SEEK_BAR_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void launchTimer(View view) {
        if (timerActive) {
            resetTimer();
        } else {
            timerActive = true;
            timerButton.setText("STOP");
            //we don't want the user to change the seekbar if the timer's running
            timerSeekBar.setEnabled(false);
            int timeRemained = timerSeekBar.getProgress() * SEEK_BAR_STEP;
            //an egg image will rotate 360 degrees each second.
            eggImage.animate().rotation(timeRemained * 360).setDuration(timeRemained * 1000).start();
            //Arguments need to be in milliseconds ( *1000 & /1000 )
            countDownTimer = new CountDownTimer(timeRemained * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //millisUntilFinished is in ms so needs to be converted to seconds
                    updateTimerText((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    mediaPlayer.start();
                    resetTimer();
                }
            }.start();
        }
    }

    //Helper method to show the time in 00:00 format
    private void updateTimerText(int secondsLeft) {
        int minute = secondsLeft / 60;
        int second = secondsLeft % 60;
        String minuteString, secondString;
        //Strings get updated each second by the CountDownTimer
        minuteString = (minute < 10) ? "0" + minute : Integer.toString(minute);
        secondString = (second < 10) ? "0" + second : Integer.toString(second);
        
        timerText.setText(minuteString + ":" + secondString);
    }

    //Helper method for resetting the timer. either after it's done or after user stops it.
    private void resetTimer() {
        //the image will have a separate animation with the alarm's duration to let the user know
        //that the timer is done.
        eggImage.animate().setDuration(mediaPlayer.getDuration()).rotation(-360);
        //timer gets canceled
        countDownTimer.cancel();
        timerSeekBar.setEnabled(true);
        timerSeekBar.setProgress(0);
        timerText.setText("00:00");
        timerButton.setText("START");
        timerActive = false;

        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();

    }
}