package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CurrentPlayingListAdapter extends ArrayAdapter {
    private final Activity activity;
    private ArrayList<String> nameArray;

    public CurrentPlayingListAdapter(Activity activity, ArrayList<String> nameArrayParam) {
        super(activity, R.layout.current_playing_row, nameArrayParam);
        this.activity = activity;
        this.nameArray = nameArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater li = activity.getLayoutInflater();
            view = li.inflate(R.layout.current_playing_row, null);
        }
        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);
        TextView nameTextField = view.findViewById(R.id.txt_current_playing);
        nameTextField.setText(nameArray.get(position));
        if (nameArray.get(position) == null) {
            parentLayout.setVisibility(View.GONE);
        } else {
            parentLayout.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void refreshEvents(ArrayList<String> nameArrayParam) {
        this.nameArray = nameArrayParam;
        notifyDataSetChanged();
    }
}