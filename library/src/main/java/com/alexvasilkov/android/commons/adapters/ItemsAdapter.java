package com.alexvasilkov.android.commons.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * {@link BaseAdapter} implementation to use {@link List} as a source.<br/>
 * {@link #getView(int, android.view.View, android.view.ViewGroup) getView} method is divided into
 * {@link #onCreateHolder(ViewGroup, int)} and {@link #onBindHolder(ViewHolder, int)} methods.
 */

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public abstract class ItemsAdapter<T, VH extends ItemsAdapter.ViewHolder> extends BaseAdapter {

    private static final int TAG_HOLDER_ID = -10001;

    private List<T> items;

    /**
     * Sets list to this adapter and calls {@link #notifyDataSetChanged()} to update underlying
     * {@link android.widget.ListView}.<br/>
     * You can pass {@code null} to clear the adapter.
     */
    public void setItemsList(@Nullable List<T> list) {
        items = list;
        notifyDataSetChanged();
    }

    public List<T> getItemsList() {
        return items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public T getItem(int position) {
        if (items == null || position < 0 || position >= items.size()) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final View getView(int pos, View convertView, ViewGroup parent) {
        final VH holder;
        if (convertView == null) {
            holder = onCreateHolder(parent, getItemViewType(pos));
            holder.itemView.setTag(TAG_HOLDER_ID, holder);
        } else {
            holder = (VH) convertView.getTag(TAG_HOLDER_ID);
        }

        onBindHolder(holder, pos);

        return holder.itemView;
    }

    protected abstract VH onCreateHolder(ViewGroup parent, int viewType);

    protected abstract void onBindHolder(VH viewHolder, int position);


    public static class ViewHolder {
        public final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

}
