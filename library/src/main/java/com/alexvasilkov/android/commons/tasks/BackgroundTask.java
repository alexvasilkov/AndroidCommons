package com.alexvasilkov.android.commons.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Helper class that will call regular {@link android.os.AsyncTask AsyncTask} and store caller (parent object) into
 * {@link java.lang.ref.WeakReference WeakReference}. All derived inner classes should be marked as static in most cases
 * to not leak any {@link android.app.Activity Activities} or {@link android.app.Fragment Fragments}
 */
public abstract class BackgroundTask<P> {

    private final WeakReference<P> mParentRef;
    private boolean mIsSkipCallbacksIfParentIsNull;
    private ATask mTask;

    /**
     * @param parent Parent object (i.e. {@link android.app.Activity Activity} or
     *               {@link android.app.Fragment Fragment}) to be passed to task's lyfecycle
     *               methods ({@link #onTaskStarted(Object) onTaskStarted},
     *               {@link #onTaskSuccess(Object) onTaskSuccess},
     *               {@link #onTaskFail(Object, Exception) onTaskFail}
     *               or {@link #onTaskEnded(Object) onTaskEnded})
     */
    public BackgroundTask(P parent) {
        mParentRef = new WeakReference<P>(parent);
        mIsSkipCallbacksIfParentIsNull = (parent != null); // only skipping callbacks if origin parent is null
    }

    protected void onTaskStarted(P parent) {
    }

    protected abstract void doTask() throws Exception;

    /**
     * @param skip Parent object is saved into {@link java.lang.ref.WeakReference
     *             WeakReference} so it can be erased and we won't
     *             be able to pass it to task's lyfecycle methods. Default behavior is to skip
     *             calling these methods if parent object is <code>null</code> to prevent NPE,
     *             but you can change this if you must be sure that all callback methods are
     *             always called.
     */
    protected void setSkipCallbacksIfParentIsNull(boolean skip) {
        mIsSkipCallbacksIfParentIsNull = skip;
    }

    /**
     * Calls {@link android.os.AsyncTask#publishProgress(Object[]) AsyncTask.publishProgress()} method.
     * Corresponding callback method is {@link #onTaskProgress(Object) onTaskProgress}.<br/>
     * Should only be called from {@link #doTask() doTask} method.
     */
    protected void publishTaskProgress() {
        if (mTask != null) mTask.publishTaskProgress();
    }

    protected void onTaskProgress(P parent) {
    }

    protected void onTaskSuccess(P parent) {
    }

    protected void onTaskFail(P parent, Exception e) {
    }

    protected void onTaskEnded(P parent) {
    }

    private P getParent() {
        return mParentRef.get();
    }

    public void exec() {
        mTask = new ATask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        } else {
            mTask.execute((Void) null);
        }
    }

    public void cancel(boolean mayInterruptIfRunning) {
        if (mTask != null) mTask.cancel(mayInterruptIfRunning);
    }

    private class ATask extends AsyncTask<Void, Void, Void> {

        private Exception mException;

        @Override
        protected final void onPreExecute() {
            super.onPreExecute();
            P parent = getParent();
            if (parent == null && mIsSkipCallbacksIfParentIsNull) {
                cancel(false);
            } else {
                onTaskStarted(parent);
            }
        }

        @Override
        protected final Void doInBackground(Void... urls) {
            try {
                doTask();
            } catch (Exception e) {
                mException = e;
                Log.e("BackgroundTask", "Exception", e);
            }
            return null;
        }

        private void publishTaskProgress() {
            publishProgress();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            P parent = getParent();
            if (parent == null && mIsSkipCallbacksIfParentIsNull) {
                return;
            }

            onTaskProgress(parent);
        }

        @Override
        protected final void onPostExecute(Void result) {
            P parent = getParent();
            if (parent == null && mIsSkipCallbacksIfParentIsNull) {
                return;
            }

            if (mException == null) {
                onTaskSuccess(parent);
            } else {
                onTaskFail(parent, mException);
            }
            onTaskEnded(parent);
        }

        /**
         * Always make call to super.onCancelled(), then overriding this method!
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();

            P parent = getParent();
            if (parent != null || !mIsSkipCallbacksIfParentIsNull) {
                onTaskEnded(parent);
            }
        }

    }

}
