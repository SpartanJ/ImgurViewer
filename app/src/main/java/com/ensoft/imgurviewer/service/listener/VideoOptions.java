package com.ensoft.imgurviewer.service.listener;

import android.net.Uri;
import android.util.Pair;


public class VideoOptions {

    private Pair<Integer, Integer> clipRange = null;
    private int startTime = 0;

    private Uri audioTrack = null;

    public void setClipRange(int start, int end) {
        clipRange = Pair.create(start, end);
    }

    public boolean isClipped() {
        return clipRange != null;
    }


    public int getClipStartPosition() {
        return clipRange.first;
    }

    public int getClipEndPosition() {
        return clipRange.second;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setExternalAudioTrack(Uri uri) {
        audioTrack = uri;
    }

    public boolean hasExternalAudioTrack() {
        return this.audioTrack != null;
    }

    public Uri getExternalAudioTrack() {
        return audioTrack;
    }

}
