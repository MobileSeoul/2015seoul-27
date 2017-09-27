package com.seoul.publicbooksearcher.presentation.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookTitleAutoCompleteTextViewAdapter extends ArrayAdapter {

    private final static Filter NULL_FILTER = new Filter() {
        @Override
        protected void publishResults (CharSequence constraint, Filter.FilterResults results){
        }
        @Override
        protected Filter.FilterResults performFiltering (CharSequence constraint){
            Log.i("Filter", "Filter:" + constraint + " thread: " + Thread.currentThread());
            return null;
        }
    };

    public BookTitleAutoCompleteTextViewAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
    }

    public void setTitles(List titles){
        clear();
        addAll(titles);
    }

    @Override
    public Filter getFilter() {
        return NULL_FILTER;
    }
}
