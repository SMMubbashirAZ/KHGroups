package com.blazeminds.pos.adapter;

/*
  Created by Blazeminds on 4/19/2018.
 */


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"ALL","unchecked"})
public class FilterWithSpaceAdapter<T> extends BaseAdapter implements
        Filterable {
    
    private final Object mLock = new Object();
    
    private List<T> mObjects;
    
    private int mResource;
    
    private int mDropDownResource;
    
    private int mFieldId = 0;
    
    private boolean mNotifyOnChange = true;
    
    private Context mContext;
    
    private ArrayList<T> mOriginalValues;
    private ArrayFilter mFilter;
    
    private LayoutInflater mInflater;
    
    public FilterWithSpaceAdapter(Context context, int textViewResourceId) {
        init(context, textViewResourceId, 0, new ArrayList<T>());
    }
    
    public FilterWithSpaceAdapter(Context context, int resource,
                                  int textViewResourceId) {
        init(context, resource, textViewResourceId, new ArrayList<T>());
    }
    
    public FilterWithSpaceAdapter(Context context, int textViewResourceId,
                                  T[] objects) {
        init(context, textViewResourceId, 0, Arrays.asList(objects));
    }
    
    public FilterWithSpaceAdapter(Context context, int resource,
                                  int textViewResourceId, T[] objects) {
        init(context, resource, textViewResourceId, Arrays.asList(objects));
    }
    
    public FilterWithSpaceAdapter(Context context, int textViewResourceId,
                                  List<T> objects) {
        init(context, textViewResourceId, 0, objects);
    }
    
    
    public FilterWithSpaceAdapter(Context context, int resource,
                                  int textViewResourceId, List<T> objects) {
        init(context, resource, textViewResourceId, objects);
    }
    
    public static FilterWithSpaceAdapter<CharSequence> createFromResource(
            Context context, int textArrayResId, int textViewResId) {
        CharSequence[] strings = context.getResources().getTextArray(
                textArrayResId);
        return new FilterWithSpaceAdapter<>(context, textViewResId,
                strings);
    }
    
    public void add(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            } else {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    
    public void addAll(Collection<? extends T> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    
    @SafeVarargs
    public final void addAll(T... items) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.addAll(mOriginalValues, items);
            } else {
                Collections.addAll(mObjects, items);
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    public void insert(T object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            } else {
                mObjects.add(index, object);
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    public void remove(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            } else {
                mObjects.remove(object);
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }
    
    
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }
    
    private void init(Context context, int resource, int textViewResourceId,
                      List<T> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = mDropDownResource = resource;
        mObjects = objects;
        mFieldId = textViewResourceId;
    }
    
    
    public Context getContext() {
        return mContext;
    }
    
    public int getCount() {
        return mObjects.size();
    }
    
    
    public T getItem(int position) {
        return mObjects.get(position);
    }
    
    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }
    
    private View createViewFromResource(int position, View convertView,
                                        ViewGroup parent, int resource) {
        View view;
        TextView text;
        
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        
        try {
            if (mFieldId == 0) {
                // If no custom field is assigned, assume the whole resource is
                // a TextView
                text = (TextView) view;
            } else {
                // Otherwise, find the TextView field within the layout
                text = view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter",
                    "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }
        
        T item = getItem(position);
        if (item instanceof CharSequence) {
            text.setText((CharSequence) item);
        } else {
            text.setText(item.toString());
        }
        
        return view;
    }
    
    
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent,
                mDropDownResource);
    }
    
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }
    
    
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }
            
            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                
                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }
                
                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<>();
                
                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.toString().toLowerCase();
                    
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                        // see if it contains the constraint somewhere in the
                        // value element
                    } else if (valueText.contains(prefixString)) {
                        newValues.add(value);
                    }
                }
                
                results.values = newValues;
                results.count = newValues.size();
            }
            
            return results;
        }
        
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
