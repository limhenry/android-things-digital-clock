package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwabenaberko.openweathermaplib.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;

import java.util.HashMap;

public class OWMWeather {
    private HashMap<String, Integer> ID_MAP;
    private OpenWeatherMapHelper helper;
    Context context;

    public OWMWeather(Context context){
        this.context = context;

        initWeatherIcon();
        helper = new OpenWeatherMapHelper();
        helper.setApiKey(context.getString(R.string.owm_api_key));
        helper.setUnits(Units.METRIC);
    }

    public void initWeatherIcon() {
        ID_MAP = new HashMap<>();

        ID_MAP.put("01d", R.drawable.weather_01);
        ID_MAP.put("01n", R.drawable.weather_01n);
        ID_MAP.put("02d", R.drawable.weather_02);
        ID_MAP.put("02n", R.drawable.weather_02n);
        ID_MAP.put("03d", R.drawable.weather_03);
        ID_MAP.put("03n", R.drawable.weather_03n);
        ID_MAP.put("04d", R.drawable.weather_04);
        ID_MAP.put("04n", R.drawable.weather_04n);
        ID_MAP.put("09d", R.drawable.weather_09);
        ID_MAP.put("09n", R.drawable.weather_09);
        ID_MAP.put("10d", R.drawable.weather_10);
        ID_MAP.put("10n", R.drawable.weather_10n);
        ID_MAP.put("11d", R.drawable.weather_11);
        ID_MAP.put("11n", R.drawable.weather_11);
        ID_MAP.put("13d", R.drawable.weather_13);
        ID_MAP.put("13n", R.drawable.weather_13);
        ID_MAP.put("50d", R.drawable.weather_50);
        ID_MAP.put("50n", R.drawable.weather_50);
        ID_MAP.put("-1", R.drawable.weather_none_available);
    }

    public void setWeatherIcon(String iconID) {
        ImageView img_weather_icon = ((Activity)context).findViewById(R.id.img_weather_icon);
        img_weather_icon.setBackgroundResource(ID_MAP.get(iconID));
    }

    public void setWeatherTemp(Double currentWeather) {
        TextView txt_weather = ((Activity)context).findViewById(R.id.txt_weather);
        txt_weather.setText(String.format("%.0f", currentWeather) + " °C");
    }

    public void getWeather() {
        helper.getCurrentWeatherByCityName("Cyberjaya", new OpenWeatherMapHelper.CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                setWeatherIcon(currentWeather.getWeatherArray().get(0).getIcon());
                setWeatherTemp(currentWeather.getMain().getTemp());
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable.getMessage().contains("Failed to connect to")) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            getWeather();
                        }
                    }, 5000);
                }
            }
        });
    }
}
