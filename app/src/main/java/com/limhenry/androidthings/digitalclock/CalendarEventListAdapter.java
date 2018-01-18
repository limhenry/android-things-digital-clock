package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.google.api.services.calendar.model.Event;
//
import java.util.ArrayList;
import java.util.List;

public class CalendarEventListAdapter {
//    public class CalendarEventListAdapter extends ArrayAdapter {
//    private final Activity activity;
//    private List<Event> event_list;
//
//    public CalendarEventListAdapter(Activity activity, List<Event> event_list) {
//        super(activity, R.layout.calendar_event_row, event_list);
//        this.activity = activity;
//        this.event_list = event_list;
//    }
//
//    public View getView(int position, View view, ViewGroup parent) {
//       // if (view == null) {
//            LayoutInflater li = activity.getLayoutInflater();
//            view = li.inflate(R.layout.calendar_event_row, null);
////        }
////        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);
//        TextView nameTextField = view.findViewById(R.id.txt_event_name);
//        nameTextField.setText("meow");
//        if (event_list.get(position) == null) {
//            Log.i("List VIew", event_list.get(position).getSummary());
//        }
////            parentLayout.setVisibility(View.GONE);
////        } else {
////            parentLayout.setVisibility(View.VISIBLE);
////        }
//        return view;
//    }
//
//    public void refreshEvents(List<Event> event_list) {
//        Log.i("Refresh Event", Integer.toString(event_list.size()));
//        this.event_list = event_list;
//        notifyDataSetChanged();
//    }
}