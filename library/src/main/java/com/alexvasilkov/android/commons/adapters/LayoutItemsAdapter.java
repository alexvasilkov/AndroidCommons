package com.alexvasilkov.android.commons.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@link ItemsAdapter} implementation that allows to populate any ViewGroup with views created by
 * this adapter, see {@link #addItemsTo(android.view.ViewGroup)}.<br/>
 * Also you can make use of views recycling, which is enabled by default.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public abstract class LayoutItemsAdapter<T, VH extends ItemsAdapter.ViewHolder>
        extends ItemsAdapter<T, VH> {

    private static final int TAG_TYPE_ID = -10000;

    private final boolean useRecycler;
    private final SparseArray<Queue<View>> recycledViews;

    private ViewGroup container;

    protected LayoutItemsAdapter() {
        this(true);
    }

    protected LayoutItemsAdapter(boolean useRecycler) {
        this.useRecycler = useRecycler;

        if (useRecycler) {
            recycledViews = new SparseArray<>(getViewTypeCount());
        } else {
            recycledViews = null;
        }
    }

    /**
     * Attaches given view group to adapter to be populated with views when
     * {@link #notifyDataSetChanged()} method is called.<br/>
     * If other view group was already attached it will be cleared first.
     */
    public void attachLayout(@Nullable ViewGroup newContainer) {
        recycleAllFrom(container);
        container = newContainer;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        addItemsTo(container);
    }

    /**
     * Removes all child views from given view group and fill it in with items populated from this
     * adapter.
     */
    protected void addItemsTo(@Nullable ViewGroup layout) {
        if (layout == null) {
            return;
        }

        recycleAllFrom(layout);

        for (int pos = 0, size = getCount(); pos < size; pos++) {
            final int viewType = getItemViewType(pos);
            final View view = getView(pos, pollRecycledView(viewType), layout);
            layout.addView(view);
            view.setTag(TAG_TYPE_ID, viewType);
        }
    }

    /**
     * Removing and recycling all child views from given view group
     */
    protected void recycleAllFrom(@Nullable ViewGroup layout) {
        if (!useRecycler || layout == null) {
            return;
        }

        for (int i = 0, size = layout.getChildCount(); i < size; i++) {
            final View view = layout.getChildAt(i);
            final int viewType = (Integer) view.getTag(TAG_TYPE_ID);
            final Queue<View> cache = getViewsCache(viewType);
            if (cache != null) {
                cache.offer(view);
                onRecycleView(view);
            }
        }

        layout.removeAllViews();
    }

    @Nullable
    protected View pollRecycledView(int viewType) {
        final Queue<View> cache = getViewsCache(viewType);
        return cache == null ? null : cache.poll();
    }

    private Queue<View> getViewsCache(int viewType) {
        if (useRecycler) {
            Queue<View> queue = recycledViews.get(viewType);
            if (queue == null) {
                queue = new LinkedList<>();
                recycledViews.put(viewType, queue);
            }
            return queue;
        } else {
            return null;
        }
    }

    /**
     * Called when view is recycled. You can perform view clean up here (i.e. clear all images).
     */
    protected void onRecycleView(@NonNull View view) {
    }

}
