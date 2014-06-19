package com.alexvasilkov.android.commons.adapters;

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

    public static final int TAG_TYPE_ID = -10000;

    private boolean mUseRecycler = false;
    private Queue<View>[] mRecycledViews = null;
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
            mRecycledViews = new Queue[getViewTypeCount()];
            for (int i = 0; i < getViewTypeCount(); i++) {
                mRecycledViews[i] = new LinkedList<View>();
            }
        } else {
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
            int type = getItemViewType(pos);
            View view = getView(pos, pollRecycledView(type), layout);
            view.setTag(TAG_TYPE_ID, type);
            layout.addView(view);
        }
    }

    /**
     * Removing and recycling all child views from given layout group
     */
    protected void recycleAllFrom(ViewGroup layout) {
        if (!mUseRecycler || layout == null) return;

        int size = layout.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = layout.getChildAt(i);
            int type = (Integer) view.getTag(TAG_TYPE_ID);
            mRecycledViews[type].offer(view);
            onRecycleView(view);
        }

        layout.removeAllViews();
    }

    protected View pollRecycledView(int itemType) {
        return mUseRecycler ? mRecycledViews[itemType].poll() : null;
    }

    /**
     * Called when view is recycled. You can perform view clean up here (i.e. clear all images).
     */
    protected void onRecycleView(View view) {
    }

}
