package com.mridx.radioprogram.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.mridx.radioprogram.R;

import java.util.concurrent.TimeUnit;

import static com.mridx.radioprogram.activity.HomeUI.mediaHelper;


public class DetailUI extends AppCompatActivity {


    private Toolbar toolbar;

    private AppCompatImageView playPause, playPrev, playNext;

    private String url = "http://www.hubharp.com/web_sound/BachGavotte.mp3";

    private boolean ok;
    private int currentPos;

    private ProgressBar progressBar;
    private AppCompatSeekBar seekBar;

    private AppCompatTextView startingPoint, endingPoint;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view_ui);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.mainProg);

        playPause = findViewById(R.id.playPause);
        playNext = findViewById(R.id.playNext);
        playPrev = findViewById(R.id.playPrev);
        seekBar = findViewById(R.id.seekbar);
        startingPoint = findViewById(R.id.startingPoint);
        endingPoint = findViewById(R.id.endingPoint);

        playPause.setOnClickListener(v -> startPlayOrPause());
        final View.OnClickListener onClickListener = v -> Toast.makeText(this, "Haven't added any callback !", Toast.LENGTH_SHORT).show();
        playPrev.setOnClickListener(onClickListener);
        playNext.setOnClickListener(onClickListener);
        handler = new Handler();
        setupUI();

        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaHelper.getCurrentProgress());
                String time = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mediaHelper.getCurrentProgress()),
                        TimeUnit.MILLISECONDS.toSeconds(mediaHelper.getCurrentProgress()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaHelper.getCurrentProgress()))
                );
                startingPoint.setText(time);
                startHandler();
            }
        };


        mediaHelper.setOnStartPlaying(b -> setupUI());
        mediaHelper.setOnPreparedStream(b -> syncSeekbar());
    }

    private void syncSeekbar() {
        seekBar.setMax(mediaHelper.getFileDuration());
        Log.d("kaku", "syncSeekbar: " + mediaHelper.getFileDuration());

        String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mediaHelper.getFileDuration()),
                TimeUnit.MILLISECONDS.toSeconds(mediaHelper.getFileDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaHelper.getFileDuration()))
        );
        endingPoint.setText(time);

        startHandler();
    }

    private void startHandler() {
        handler.postDelayed(runnable, 1000);
    }

    private void pauseHandler() {
        handler.removeCallbacks(runnable);
    }

    private void setupUI() {

        if (mediaHelper.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_pause);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            startHandler();
            syncSeekbar();
            seekBar.setProgress(mediaHelper.getCurrentProgress());
        } else {
            playPause.setImageResource(R.drawable.ic_play);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }
        if (!playPause.isEnabled()) {
            playPause.setEnabled(true);
        }


    }

    private void setPlayStop() {
        playPause.setImageResource(R.drawable.ic_play);
        progressBar.setVisibility(View.GONE);
    }

    private void setPlayControl() {
        playPause.setImageResource(R.drawable.ic_pause);
        progressBar.setVisibility(View.GONE);
    }

    private void startPlayOrPause() {
        if (mediaHelper.isPlaying()) {
            mediaHelper.pause();

        } else if (mediaHelper.canResume()) {
            mediaHelper.resume();
            startHandler();
        } else {
            mediaHelper.init(url);
            mediaHelper.prepare();
            setLoading();
        }
    }

    private void setLoading() {
        playPause.setEnabled(false);
        playPause.setImageResource(R.drawable.red_prog);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void completed() {
        playPause.setImageResource(R.drawable.ic_play);
    }

    public void start() {
        playPause.setImageResource(R.drawable.ic_pause);
    }
}
