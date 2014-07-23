package com.alexvasilkov.android.commons.tasks;

import android.os.Bundle;
import com.azcltd.fluffyevents.EventsBus;

@Deprecated
public abstract class EventsTask extends BackgroundTask<Void> {

    private boolean mIsLoading;
    private boolean mIsLoaded;

    public EventsTask() {
        super(null);
    }

    protected abstract int getLoadingEventId();

    protected abstract int getLoadedEventId();

    protected Bundle getLoadedEventBundle() {
        return null;
    }

    @Override
    public void exec() {
        if (mIsLoading) return;

        if (isTaskLoaded()) {
            EventsBus.send(getLoadedEventId());
        } else {
            mIsLoading = true;
            EventsBus.sendSticky(getLoadingEventId());
            super.exec();
        }
    }

    protected boolean isTaskLoaded() {
        return mIsLoaded;
    }

    @Override
    protected void onTaskSuccess(Void parent) {
        super.onTaskSuccess(parent);
        mIsLoaded = true;
    }

    @Override
    protected void onTaskEnded(Void parent) {
        super.onTaskEnded(parent);

        EventsBus.removeSticky(getLoadingEventId());
        mIsLoading = false;

        if (isTaskLoaded()) EventsBus.send(getLoadedEventId(), getLoadedEventBundle());
    }

}
