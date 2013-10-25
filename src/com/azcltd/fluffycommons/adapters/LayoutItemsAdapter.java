package com.azcltd.fluffycommons.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@link ItemsAdapter} implementation that allows to populate any ViewGroup with views created by adapter,
 * see {@link #addItemsTo(android.view.ViewGroup)}<br/>
 * Also you can make use of views recycling, see {@link #setUseRecycler(boolean)}
 */
public abstract class LayoutItemsAdapter<T> extends ItemsAdapter<T> {

    private boolean mUseRecycler = false;
    private Queue<View> mRecycledViews = null;
    private ViewGroup mLayout;

    public LayoutItemsAdapter(Context context) {
        super(context);
    }

    /**
     * Sets whether to use views recycling or not. Defaults to not use recycler.
     */
    public void setUseRecycler(boolean use) {
        if (mUseRecycler == use) return;
        mUseRecycler = use;
        if (use) {
            mRecycledViews = new LinkedList<View>();
        } else {
            mRecycledViews.clear();
            mRecycledViews = null;
        }
    }

    /**
     * Attaches given layout group to adapter to be populated with {@link #notifyDataSetChanged()}.<br/>
     * If other layout group was already attached it will be cleared at first.
     */
    public void attachLayout(ViewGroup layout) {
        recycleAllFrom(mLayout);
        mLayout = layout;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        addItemsTo(mLayout);
    }

    /**
     * Removes all child views from given layout group and fill it with items populated with usual adapter's lifecycle
     */
    protected void addItemsTo(ViewGroup layout) {
        if (layout == null) return;

        recycleAllFrom(layout);

        int size = getCount();
        for (int pos = 0; pos < size; pos++) {
            layout.addView(getView(pos, pollRecycledView(), layout));
        }
    }

    /**
     * Removing and recycling all child views from given layout group
     */
    protected void recycleAllFrom(ViewGroup layout) {
        if (!mUseRecycler || layout == null) return;

        int size = layout.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = layout.getChildAt(i);
            mRecycledViews.offer(v);
            onRecycleView(v);
        }

        layout.removeAllViews();
    }

    protected View pollRecycledView() {
        return mUseRecycler ? mRecycledViews.poll() : null;
    }

    /**
     * Called when view is recycled. You can perform view clean up here (i.e. clear all images).
     */
    protected void onRecycleView(View view) {
    }

}
