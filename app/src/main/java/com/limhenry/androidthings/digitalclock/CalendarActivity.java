package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CalendarActivity {
//public class CalendarActivity extends AppCompatActivity {

//    Activity activity;
//    private HttpTransport httpTransport;
//    private JsonFactory jsonFactory;
//    private GoogleCredential credential;
//    private CalendarEventListAdapter calendarEventListAdapter;
//    private ArrayList<Event> event_list;
//
//    public void closeActivity(View view) {
//        finish();
//    }
//
//    private class MakeRequestTask extends AsyncTask<Void, Void, List<Event>> {
//        private Exception mLastError = null;
//
//        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
//                .setApplicationName("applicationName").build();
//
//        @Override
//        protected List<Event> doInBackground(Void... params) {
//            try {
//                return getDataFromApi();
//            } catch (Exception e) {
//                mLastError = e;
//                cancel(true);
//                return null;
//            }
//        }
//
//        private List<Event> getDataFromApi() throws IOException {
//
//            LocalTime midnight = LocalTime.MIDNIGHT;
//            LocalDate today = LocalDate.now(ZoneId.of("Asia/Kuala_Lumpur"));
//            LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
//            LocalDateTime thirdDayMidnight = todayMidnight.plusDays(3);
//
//            Date todayMidnight_date = Date.from(todayMidnight.atZone(ZoneId.systemDefault()).toInstant());
//            Date thirdDayMidnight_date = Date.from(thirdDayMidnight.atZone(ZoneId.systemDefault()).toInstant());
//
//            Log.i("date", todayMidnight_date.toString());
//
//            DateTime todayMidnight_datetime = new DateTime(todayMidnight_date);
//            DateTime thirdDayMidnight_datetime = new DateTime(thirdDayMidnight_date);
//
//            Events events = service.events().list("henry71896@gmail.com")
//                    .setTimeMin(todayMidnight_datetime)
//                    .setTimeMax(thirdDayMidnight_datetime)
//                    .setSingleEvents(true)
//                    .execute();
//            List<Event> items = events.getItems();
//            return items;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            Log.i("Calender", "Loading ...");
//        }
//
//        @Override
//        protected void onPostExecute(List<Event> output) {
//            if (output == null || output.size() == 0) {
//                Log.i("Calender", "No results returned");
//            } else {
//                calendarEventListAdapter.refreshEvents(output);
//                for (Event event : output) {
//                    Log.i("Calender", event.getSummary() + " " + event.getStart().getDateTime() + " " + event.getEnd().getDateTime() + " " + event.getRecurrence());
//                }
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            Log.i("Calender", mLastError.getMessage());
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calendar);
//
//        event_list = new ArrayList<>();
//        calendarEventListAdapter = new CalendarEventListAdapter(this, event_list);
//        ListView listView = (ListView) findViewById(R.id.calendar_event_list_view);
//        listView.setAdapter(calendarEventListAdapter);
//        listView.setEnabled(false);
//        listView.setDivider(null);
//
//        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        TextView txt_clockText = (TextView) findViewById(R.id.clockText);
//        txt_clockText.setText(df.format(new Date()));
//
//        final InputStream stream = getResources().openRawResource(R.raw.credentials);
//
//        try {
//            credential = GoogleCredential.fromStream(stream)
//                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar.readonly"));
//            httpTransport = AndroidHttp.newCompatibleTransport();
//            jsonFactory = JacksonFactory.getDefaultInstance();
//
//            new MakeRequestTask().execute();
//        }
//        catch (IOException e) {
//            Log.e("GoogleCredential", "Failed to obtain access token.", e);
//        }
//
//    }
}
