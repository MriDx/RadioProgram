package com.mridx.radioprogram.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MediaHelper {

    private Context context;
    private MediaPlayer mediaPlayer;
    private boolean resume;

    private int currentPos;

    OnStartPlaying onStartPlaying;

    public interface OnStartPlaying {
        void OnStartPlaying(boolean b);

    }

    public void setOnStartPlaying(OnStartPlaying onStartPlaying) {
        this.onStartPlaying = onStartPlaying;
    }
    OnPreparedStream onPreparedStream;

    public interface  OnPreparedStream {
        void OnPreparedStream(boolean b);
    }

    public void setOnPreparedStream(OnPreparedStream onPreparedStream) {
        this.onPreparedStream = onPreparedStream;
    }

    public MediaHelper() {
    }

    public void init(String url) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepare() {
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
            if (onStartPlaying != null) {
                onStartPlaying.OnStartPlaying(true);
                onPreparedStream.OnPreparedStream(true);
            }
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.d("kaku", "prepare: Error code : " + what);
            return false;
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.reset();
            if (onStartPlaying != null) {
                onStartPlaying.OnStartPlaying(false);
            }
        });
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void reset() {
        mediaPlayer.reset();
        resume = false;
    }

    public void resume() {
        if (canResume()) {
            mediaPlayer.seekTo(currentPos);
            mediaPlayer.start();
            if (onStartPlaying != null) {
                onStartPlaying.OnStartPlaying(true);
            }
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (onStartPlaying != null) {
                onStartPlaying.OnStartPlaying(false);
            }
            this.currentPos = mediaPlayer.getCurrentPosition();
            resume = true;
        }
    }

    public boolean canResume() {
        return resume;
    }

    public int getFileDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentProgress() {
        return mediaPlayer.getCurrentPosition();
    }


}
