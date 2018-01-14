package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CastControlActivity extends Activity {

    public void closeActivity(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_control);

        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        TextView txt_clockText = findViewById(R.id.clockText);
        txt_clockText.setText(df.format(new Date()));
    }
}
