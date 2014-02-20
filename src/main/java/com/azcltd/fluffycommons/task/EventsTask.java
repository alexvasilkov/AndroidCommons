package com.azcltd.fluffycommons.task;

import com.azcltd.fluffyevents.EventsBus;

public abstract class EventsTask extends BackgroundTask<Void> {

    private boolean mIsLoading;
    private boolean mIsLoaded;

    public EventsTask() {
        super(null);
    }

    protected abstract int getLoadingEventId();

    protected abstract int getLoadedEventId();

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

    protected void onTaskLoaded() {
        mIsLoaded = true;
    }

    @Override
    protected final void onTaskSuccess(Void parent) {
        super.onTaskSuccess(parent);
        onTaskLoaded();
    }

    @Override
    protected void onTaskEnded(Void parent) {
        super.onTaskEnded(parent);

        EventsBus.removeSticky(getLoadingEventId());
        mIsLoading = false;

        if (isTaskLoaded()) EventsBus.send(getLoadedEventId());
    }

}
