package com.ensoft.imgurviewer.service.listener;

import android.util.Pair;

import com.google.common.base.Optional;


public class VideoOptions {

    private Optional<Pair<Integer, Integer>> clipRange = Optional.absent();
    private int startTime = 0;

    public void setClipRange(int start, int end) {
        clipRange = Optional.of(Pair.create(start, end));
    }

    public boolean isClipped() {
        return clipRange.isPresent();
    }

    public int getClipStartPosition() {
        return clipRange.get().first;
    }

    public int getClipEndPosition() {
        return clipRange.get().second;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
