package com.alexvasilkov.android.commons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Simple {@link BaseAdapter} implementation to use with any {@link List}.<br/>
 * {@link #getView(int, android.view.View, android.view.ViewGroup) getView} method is divided into
 * {@link #createView(Object, int, android.view.ViewGroup, android.view.LayoutInflater) createView} and
 * {@link #bindView(Object, int, android.view.View) bindView} methods.
 */
public abstract class ItemsAdapter<T> extends BaseAdapter {

    private List<T> mItemsList;
    private final WeakReference<Context> contextRef;
    private final LayoutInflater layoutInflater;

    public ItemsAdapter(Context context) {
        contextRef = new WeakReference<Context>(context);
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Sets list to this adapter and calls {@link #notifyDataSetChanged()} to update underlying {@link android.widget.ListView}.<br/>
     * You can pass {@code null} to clear the adapter
     */
    public void setItemsList(List<T> list) {
        mItemsList = list;
        notifyDataSetChanged();
    }

    public List<T> getItemsList() {
        return mItemsList;
    }

    protected Context getContext() {
        return contextRef.get();
    }

    protected LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    @Override
    public int getCount() {
        return mItemsList == null ? 0 : mItemsList.size();
    }

    @Override
    public T getItem(int position) {
        if (mItemsList == null || position < 0 || position >= mItemsList.size()) return null;
        return mItemsList.get(position);
    }

    /**
     * Default implementation of this adapter uses pos as id. So we can find item by it's id with no problems.
     */
    public T getItem(long id) {
        return getItem((int) id);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        T item = mItemsList.get(pos);
        if (convertView == null) convertView = createView(item, pos, parent, layoutInflater);
        bindView(item, pos, convertView);
        return convertView;
    }

    protected abstract View createView(T item, int pos, ViewGroup parent, LayoutInflater inflater);

    protected abstract void bindView(T item, int pos, View convertView);

}
