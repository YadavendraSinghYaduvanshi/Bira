package com.cpm.bira.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cpm.bira.R;

public class SpinnerAdapterView extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    String[] spinner_item;

    public SpinnerAdapterView(Context context, String[] spinner_item) {
        this.context = context;
        inflter = (LayoutInflater.from(context));
        this.spinner_item = spinner_item;
    }

    @Override
    public int getCount() {
        return spinner_item.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_item, null);
        TextView names = (TextView) view.findViewById(R.id.spinner_txt);
        names.setText(spinner_item[i]);
        return view;
    }
}