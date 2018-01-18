package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SpotifyPlayerActivity extends Activity {
    private static SpotifyPlayerActivity sInstance;
    private String current_playing = "";
    private Boolean isPlaying = false;
    private Handler checkPlayingHandler;
    private Runnable checkPlayingRunnable;
    private Boolean isFirstLoad;
    private SpotifyClient spotifyService;
    private String access_token;

    public static synchronized SpotifyPlayerActivity getInstance() {
        return sInstance;
    }

    public void closeActivity(View view) {
        clearCheckPlayingHandler();
        finish();
    }

    public void nextSong(View view) {
        clearCheckPlayingHandler();

        Callback callback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    setCheckPlayingHandler();
                } catch (Exception e) {
                    Log.i("Spotify", "Err: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        };

        spotifyService.nextTrack(access_token).enqueue(callback);
    }

    public void previousSong(View view) {
        clearCheckPlayingHandler();

        Callback callback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    setCheckPlayingHandler();
                } catch (Exception e) {
                    Log.i("Spotify", "Err: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        };

        spotifyService.previousTrack(access_token).enqueue(callback);
    }


    public void playPause(View view) {
        clearCheckPlayingHandler();

        Callback callback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    ImageView img_playpause = findViewById(R.id.img_play_pause);
                    if (!isPlaying) {
                        img_playpause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    } else {
                        img_playpause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    }
                    setCheckPlayingHandler();
                } catch (Exception e) {
                    Log.i("Spotify", "Err: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        };

        if (isPlaying) {
            spotifyService.pauseTrack(access_token).enqueue(callback);
        } else {
            spotifyService.playTrack(access_token).enqueue(callback);
        }
    }

    public void clearCheckPlayingHandler() {
        if (checkPlayingRunnable != null) {
            checkPlayingHandler.removeCallbacks(checkPlayingRunnable);
        }
    }

    public void setCheckPlayingHandler() {
        checkPlayingHandler = new Handler();
        checkPlayingRunnable = new Runnable() {
            public void run() {
                getCurrentlyPlaying();
            }
        };
        checkPlayingHandler.postDelayed(checkPlayingRunnable, 1000);
    }

    public void getCurrentlyPlaying() {
        clearCheckPlayingHandler();

        spotifyService.getCurrentPlaying(access_token).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject data = new JSONObject(response.body().toString());

                    if (isFirstLoad) {
                        LinearLayout loading_layout = findViewById(R.id.loading_layout);
                        LinearLayout music_info = findViewById(R.id.music_info);
                        loading_layout.setVisibility(View.GONE);
                        music_info.setVisibility(View.VISIBLE);
                        isFirstLoad = false;
                    }

                    isPlaying = data.getBoolean("is_playing");
                    String songID = data.getJSONObject("item").getString("id");
                    String songName = data.getJSONObject("item").getString("name");
                    String albumName = data.getJSONObject("item").getJSONObject("album").getString("name");
                    String artistName = data.getJSONObject("item").getJSONArray("artists").getJSONObject(0).getString("name");
                    String deviceName = data.getJSONObject("device").getString("name");
                    String album_url = data.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url");

                    TextView txt_deviceName = findViewById(R.id.txt_device_name);
                    txt_deviceName.setText(deviceName);
                    SimpleDateFormat df = new SimpleDateFormat("hh:mm");
                    df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    TextView txt_clockText = findViewById(R.id.clockText);
                    txt_clockText.setText(df.format(new Date()));

                    ImageView img_playpause = findViewById(R.id.img_play_pause);

                    if (isPlaying) {
                        img_playpause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    } else {
                        img_playpause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
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

                    setCheckPlayingHandler();
                }
                catch (Exception e){
                    Log.i("Spotify", "Err: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Spotify", "Failed: " + t.getMessage());
            }
        });
    }

    public void getAccessToken() {
        String refresh_token = getString(R.string.spotify_refresh_token);
        String authorization = getString(R.string.spotify_authorization);
        spotifyService.getAccessToken("refresh_token", refresh_token, authorization).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    access_token = "Bearer " + response.body().get("access_token").getAsString();
                    Log.i("Spotify", "Token: " + access_token);
                    getCurrentlyPlaying();
                } catch (Exception e) {
                    Log.i("Spotify", "Err: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spotify_player_activity);

        sInstance = this;
        isFirstLoad = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Retrofit spotifyRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.spotify.com")
                .build();

        spotifyService = spotifyRetrofit.create(SpotifyClient.class);

        getAccessToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
