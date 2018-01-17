package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.things.device.ScreenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.limhenry.androidthings.digitalclock.GlideOptions.bitmapTransform;

public class SpotifyPlayerActivity extends Activity {
    private static SpotifyPlayerActivity sInstance;
    private RequestQueue mRequestQueue;
    private String current_playing = "";
    private Boolean isPlaying = false;
    private Handler checkPlayingHandler;
    private Runnable checkPlayingRunnable;
    private Boolean isFirstLoad;

    public static synchronized SpotifyPlayerActivity getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public void closeActivity(View view) {
        checkPlayingHandler.removeCallbacks(checkPlayingRunnable);
        finish();
    }

    public void nextSong(View view) {
        SharedPreferences prefs = getSharedPreferences("Spotify_Token", MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            final String access_token = prefs.getString("access_token", "");
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, "https://api.spotify.com/v1/me/player/next",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getCurrentlyPlaying();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders()  {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + access_token);
                    return params;
                }
            };
            queue.add(postRequest);
        } else {
            Log.d("lol", "oops");
        }
    }

    public void previousSong(View view) {
        SharedPreferences prefs = getSharedPreferences("Spotify_Token", MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            final String access_token = prefs.getString("access_token", "");
            RequestQueue queue = SpotifyPlayerActivity.getInstance().getRequestQueue();

            StringRequest postRequest = new StringRequest(Request.Method.POST, "https://api.spotify.com/v1/me/player/previous",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getCurrentlyPlaying();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders()  {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + access_token);
                    return params;
                }
            };
            queue.add(postRequest);
        } else {
            Log.d("lol", "oops");
        }
    }

    public void playPause(View view) {
        String url;
        if (isPlaying) {
            url = "https://api.spotify.com/v1/me/player/pause";
        } else {
            url = "https://api.spotify.com/v1/me/player/play";
        }

        SharedPreferences prefs = getSharedPreferences("Spotify_Token", MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            final String access_token = prefs.getString("access_token", "");
            RequestQueue queue = SpotifyPlayerActivity.getInstance().getRequestQueue();
            StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            isPlaying = !isPlaying;
                            ImageView img_playpause = findViewById(R.id.img_play_pause);
                            if (isPlaying) {
                                img_playpause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                            } else {
                                img_playpause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders()  {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + access_token);
                    return params;
                }
            };
            queue.add(putRequest);
        }
    }

    public void getCurrentlyPlaying() {
        Log.i("Spotify", "getCurrentlyPlaying()");
        SharedPreferences prefs = getSharedPreferences("Spotify_Token", MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            final String access_token = prefs.getString("access_token", "");
            RequestQueue queue = SpotifyPlayerActivity.getInstance().getRequestQueue();
            StringRequest postRequest = new StringRequest(Request.Method.GET, "https://api.spotify.com/v1/me/player",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject data = new JSONObject(response);

                                isPlaying = data.getBoolean("is_playing");
                                String songID = data.getJSONObject("item").getString("id");
                                String songName = data.getJSONObject("item").getString("name");
                                String albumName = data.getJSONObject("item").getJSONObject("album").getString("name");
                                String artistName = data.getJSONObject("item").getJSONArray("artists").getJSONObject(0).getString("name");
                                String deviceName = data.getJSONObject("device").getString("name");
                                String album_url = data.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url");
                                ImageView img_playpause = findViewById(R.id.img_play_pause);
                                SimpleDateFormat df = new SimpleDateFormat("hh:mm");
                                df.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                                Log.i("Spotify", songName + " " + isPlaying);

                                if (isPlaying) {
                                    img_playpause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                                } else {
                                    img_playpause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                                }

                                TextView txt_deviceName = findViewById(R.id.txt_device_name);
                                txt_deviceName.setText(deviceName);
                                TextView txt_clockText = findViewById(R.id.clockText);
                                txt_clockText.setText(df.format(new Date()));

                                if (isFirstLoad) {
                                    LinearLayout loading_layout = findViewById(R.id.loading_layout);
                                    LinearLayout music_info = findViewById(R.id.music_info);
                                    loading_layout.setVisibility(View.GONE);
                                    music_info.setVisibility(View.VISIBLE);
                                    isFirstLoad = false;
                                }

                                if (!current_playing.equals(songID)) {
                                    current_playing = songID;
                                    TextView txt_songName = findViewById(R.id.textView7);
                                    txt_songName.setText(songName);

                                    TextView albumArtistName = findViewById(R.id.textView8);
                                    albumArtistName.setText(albumName + " | " + artistName);

                                    Glide.with(SpotifyPlayerActivity.this)
                                            .asBitmap()
                                            .load(album_url)
                                            .apply(bitmapTransform(new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.ALL)))
                                            .listener(new RequestListener<Bitmap>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                    return false;
                                                }
                                                @Override
                                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                    Blurry.with(SpotifyPlayerActivity.this)
                                                        .radius(80)
                                                        .async()
                                                        .sampling(1)
                                                        .animate(500)
                                                        .from(resource)
                                                        .into((ImageView) findViewById(R.id.imageView4));
                                                    return false;
                                                }
                                            })
                                            .into((SquareImageView) findViewById(R.id.album_image));
                                }

                                checkPlayingHandler = new Handler();
                                checkPlayingRunnable = new Runnable() {
                                    public void run() {
                                        getCurrentlyPlaying();
                                    }
                                };
                                checkPlayingHandler.postDelayed(checkPlayingRunnable, 5000);


                            } catch (JSONException e) {
                                Log.d("Response", e.toString());
                                getAccessToken();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders()  {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + access_token);
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }

    public void getAccessToken() {
        RequestQueue queue = SpotifyPlayerActivity.getInstance().getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST, "https://accounts.spotify.com/api/token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Log.d("Response", data.getString("access_token"));
                            SharedPreferences.Editor editor = getSharedPreferences("Spotify_Token", MODE_PRIVATE).edit();
                            editor.putString("access_token", data.getString("access_token"));
                            editor.apply();
                            getCurrentlyPlaying();
                        } catch (JSONException e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", getString(R.string.spotify_refresh_token));
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", getString(R.string.spotify_authorization));
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        isFirstLoad = true;

        setContentView(R.layout.spotify_player_activity);
        getCurrentlyPlaying();

        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        TextView txt_clockText = findViewById(R.id.clockText);
        txt_clockText.setText(df.format(new Date()));

        DisplayManager displayManager = (DisplayManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        if (displays.length > 0) {
            int id = displays[0].getDisplayId();
            ScreenManager screenManager = new ScreenManager(id);
            screenManager.setBrightnessMode(ScreenManager.BRIGHTNESS_MODE_MANUAL);
            screenManager.setBrightness(210);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        SquareImageView album_image;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            this.album_image = findViewById(R.id.album_image);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            Blurry.with(SpotifyPlayerActivity.this)
                    .radius(80)
                    .sampling(1)
                    .from(result)
                    .into((ImageView) findViewById(R.id.imageView4));
            album_image.setImageBitmap(result);
        }
    }
}
