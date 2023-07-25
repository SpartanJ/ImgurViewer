package com.ensoft.imgurviewer.service.listener;

import android.util.Pair;

import com.google.common.base.Optional;


public class VideoOptions {

    private Optional<Pair<Integer, Integer>> clipRange = Optional.absent();

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

}
