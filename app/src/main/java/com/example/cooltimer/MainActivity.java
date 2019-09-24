package com.example.cooltimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private Button button;
    private boolean isTimerOn;//for stop on the button
    private CountDownTimer countDownTimer;
    private int defaultInterval;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        isTimerOn = false;//Default is off
        setIntervalFromSharedPreferences(sharedPreferences);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                long progressInMillis = progress * 1000;
                updateTimer(progressInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //register MainActivity as ChangeListener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    public void start(View view) {

        //Check, whether the timer is working
        if (!isTimerOn) {//if timer don"t working
            button.setText("Stop");//when click on the button,this value is set
            seekBar.setEnabled(false);//when the timer is working, seekBar is stop
            isTimerOn = true;//Timer is included

            //2)only run the timer if it's off
            //1)create timer, pass the date to seekBar
            countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {//every second update TextView

                    updateTimer(millisUntilFinished);

                }
                @Override
                public void onFinish() {
                    //Link to file SharedPreferences
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    //Checking what is in SharedPreferences file
                   if (sharedPreferences.getBoolean("enable_sounds",true)){

                        String melodyName = sharedPreferences.getString("timer_melody", "bell");
                        if (melodyName.equals("bell")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bellsound);
                            mediaPlayer.start();
                        } else if (melodyName.equals("alarm_siren")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarmsirensound);
                            mediaPlayer.start();
                        } else if (melodyName.equals("bip")) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bipsound);
                            mediaPlayer.start();
                        }
                    }

                    resetTimer();
                }
            };
            countDownTimer.start();
        } else {
            resetTimer();
        }

    /*    //create timer, pass the date to seekBar
        countDownTimer = new CountDownTimer(seekBar.getProgress() * 1000,1000 ) {
            @Override
            public void onTick(long millisUntilFinished) {//every second update TextView

               updateTimer(millisUntilFinished);

            }

            @Override
            public void onFinish() {
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.bellsound);
                mediaPlayer.start();

            }
        };
        countDownTimer.start();*/

    }

    private void updateTimer(long millisUntilFinished) {

        int minutes = (int) millisUntilFinished / 1000 / 60;
        int seconds = (int) millisUntilFinished / 1000 - (minutes * 60);

        String minute;
        String second;

        if (minutes < 10) {
            minute = "0" + minutes;
        } else {
            minute = String.valueOf(minutes);
        }
        if (seconds < 10) {
            second = "0" + seconds;
        } else {
            second = String.valueOf(seconds);
        }
        textView.setText(minute + ":" + second);

    }

    //after the timer is finished, will put it default
    private void resetTimer() {
        countDownTimer.cancel();//stop timer
        setIntervalFromSharedPreferences(sharedPreferences);
        button.setText("Start");
        seekBar.setEnabled(true);
        isTimerOn = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//create timer_menu(settings and about
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//create link with SettingsActivity and AboutActivity
        int id = item.getItemId();//when click causes activity
        if (id == R.id.settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if (id == R.id.about) {

                Intent openAbout = new Intent(this, AboutActivity.class);
                startActivity(openAbout);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        //create auxiliary method for <EditTextPreference, (1)create variable "defaultInterval"
        private void setIntervalFromSharedPreferences (SharedPreferences sharedPreferences){

        /*  try {
              defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval","30"));
          }catch (NumberFormatException nef){
              Toast.makeText(this, "NumberFormatException happens", Toast.LENGTH_LONG).show();
          }catch (Exception e){
              Toast.makeText(this, "Some error happens", Toast.LENGTH_LONG).show();
          }*/



        //take values from sharedPreference and assign defaultInterval
         defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30"));
           //change time in updateTimer with millisec on sec
           long defaultIntervalInMillis = defaultInterval * 1000;
           updateTimer(defaultIntervalInMillis);//check, to time reflected  example 01:26, not 00:156
            seekBar.setProgress(defaultInterval);
    }
       //method for installed default interval right away
        @Override
        public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key){
            if (key.equals("default_interval")) {
                setIntervalFromSharedPreferences(sharedPreferences);
            }
        }


        @Override
        protected void onDestroy () {//shoot register
            super.onDestroy();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }


