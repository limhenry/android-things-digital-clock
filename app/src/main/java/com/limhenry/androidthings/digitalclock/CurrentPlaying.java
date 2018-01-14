package com.limhenry.androidthings.digitalclock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.io.IOException;
import java.util.ArrayList;

public class CurrentPlaying {

    public static final String CHROMECAST_SIGNATURE = "cast.media.CastMediaRouteProviderService";
    Context context;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private Cast.Listener mCastClientListener;
    private ArrayList<CastDevice> mSelectedDevice = new ArrayList<>();
    private ArrayList<GoogleApiClient> mApiClient = new ArrayList<>();
    private ArrayList<String> current_playing = new ArrayList<>();
    private ArrayList<RemoteMediaPlayer> mRemoteMediaPlayer = new ArrayList<>();
    private CurrentPlayingListAdapter currentPlayingAdapter;
    private MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            if (isCastDevice(route)) {
                if (!route.isSelected()) {
                    mMediaRouter.selectRoute(route);
                }
            }
        }

        @Override
        public void onRouteSelected(MediaRouter router, final MediaRouter.RouteInfo route) {
            if (isCastDevice(route)) {
                mSelectedDevice.add(CastDevice.getFromBundle(route.getExtras()));
                int mSelectedDeviceSize = mSelectedDevice.size();

                mCastClientListener = new Cast.Listener() {
                    @Override
                    public void onApplicationStatusChanged() {
                        Log.i("MediaRouter", "Cast.Listener.onApplicationStatusChanged()" + route.getName());
                    }

                    @Override
                    public void onApplicationMetadataChanged(ApplicationMetadata applicationMetadata) {
                        Log.i("MediaRouter", "Cast.Listener.onApplicationMetadataChanged()" + route.getName());
                        if (applicationMetadata != null) {
                            for (int i = 0; i < mSelectedDevice.size(); i++) {
                                if (mSelectedDevice.get(i).getFriendlyName().equals(route.getName())) {
                                    final int position = i;
                                    final GoogleApiClient mApiClientItem = mApiClient.get(position);

                                    LaunchOptions launchOptions = new LaunchOptions.Builder().setRelaunchIfRunning(false).build();
                                    Cast.CastApi.launchApplication(mApiClientItem, applicationMetadata.getApplicationId(), launchOptions).setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(@NonNull Cast.ApplicationConnectionResult applicationConnectionResult) {
                                            while (mRemoteMediaPlayer.size() <= position)
                                                mRemoteMediaPlayer.add(null);
                                            mRemoteMediaPlayer.set(position, new RemoteMediaPlayer());
                                            final RemoteMediaPlayer mRemoteMediaPlayerItem = mRemoteMediaPlayer.get(position);
                                            mRemoteMediaPlayerItem.setOnStatusUpdatedListener(new RemoteMediaPlayer.OnStatusUpdatedListener() {
                                                @Override
                                                public void onStatusUpdated() {
                                                    if (mRemoteMediaPlayerItem.getMediaInfo() != null) {
                                                        if (mRemoteMediaPlayerItem.getMediaInfo().getMetadata() != null) {
                                                            if (mRemoteMediaPlayerItem.getMediaStatus().getPlayerState() == 2) {
                                                                while (current_playing.size() <= position)
                                                                    current_playing.add(null);
                                                                MediaMetadata music_meta = mRemoteMediaPlayerItem.getMediaInfo().getMetadata();
                                                                current_playing.set(position, music_meta.getString(MediaMetadata.KEY_TITLE) + " | " + music_meta.getString(MediaMetadata.KEY_ARTIST));
                                                                currentPlayingAdapter.refreshEvents(current_playing);
                                                            } else {
                                                                while (current_playing.size() <= position)
                                                                    current_playing.add(null);
                                                                current_playing.set(position, null);
                                                                currentPlayingAdapter.refreshEvents(current_playing);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            try {
                                                Cast.CastApi.setMessageReceivedCallbacks(mApiClientItem, mRemoteMediaPlayerItem.getNamespace(), mRemoteMediaPlayerItem);
                                            } catch (IOException e) {
                                                Log.e("MediaRouter", "Exception while creating media channel ", e);
                                            } catch (NullPointerException e) {
                                                Log.e("MediaRouter", "Something wasn't reinitialized for reconnectChannels", e);
                                            }

                                            mRemoteMediaPlayer.get(position).requestStatus(mApiClientItem).setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                                                @Override
                                                public void onResult(@NonNull RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                                                    Log.i("MediaRouter", "requestStatus() " + mediaChannelResult);
                                                }
                                            });

                                            try {
                                                Cast.CastApi.requestStatus(mApiClientItem);
                                            } catch (IOException e) {
                                                Log.e("MediaRouter", "Couldn't request status", e);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onApplicationDisconnected(int i) {
                        Log.i("MediaRouter", "Cast.Listener.onApplicationDisconnected(" + i + ") " + route.getName());
                    }

                    @Override
                    public void onActiveInputStateChanged(int i) {
                        Log.i("MediaRouter", "Cast.Listener.onActiveInputStateChanged(" + i + ") " + route.getName());
                    }

                    @Override
                    public void onStandbyStateChanged(int i) {
                        Log.i("MediaRouter", "Cast.Listener.onStandbyStateChanged(" + i + ") " + route.getName());
                    }

                    @Override
                    public void onVolumeChanged() {
                        Log.i("MediaRouter", "Cast.Listener.onVolumeChanged() " + route.getName());
                    }
                };

                Cast.CastOptions.Builder apiOptionsBuilder = new Cast.CastOptions.Builder(mSelectedDevice.get(mSelectedDeviceSize - 1), mCastClientListener);

                GoogleApiClient gApiClient = new GoogleApiClient.Builder(context)
                        .addApi(Cast.API, apiOptionsBuilder.build())
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                Toast toast = Toast.makeText(context, "GoogleApiClient.onConnected()" + route.getName(), Toast.LENGTH_SHORT);
                                toast.show();
                                Log.i("MediaRouter", "GoogleApiClient.onConnected()" + route.getName());
                                Log.i("MediaRouter", "Bundle " + bundle);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.i("MediaRouter", "GoogleApiClient.onConnectionSuspended(" + i + ")");
                            }
                        })
                        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Toast toast = Toast.makeText(context, "GoogleApiClient.onConnectionFailed()" + connectionResult, Toast.LENGTH_SHORT);
                                toast.show();
                                Log.i("MediaRouter", "GoogleApiClient.onConnectionFailed()" + connectionResult);
                            }
                        })
                        .build();

                mApiClient.add(mSelectedDeviceSize - 1, gApiClient);
                mApiClient.get(mSelectedDeviceSize - 1).connect();
            }
        }
    };

    public CurrentPlaying(Context context) {
        this.context = context;

        mMediaRouter = MediaRouter.getInstance(MainActivity.getContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);

        getActiveMediaRoute();

        currentPlayingAdapter = new CurrentPlayingListAdapter((Activity) context, current_playing);
        ListView listView = ((Activity) context).findViewById(R.id.current_playing_list_view);
        listView.setAdapter(currentPlayingAdapter);
        listView.setEnabled(false);
        listView.setDivider(null);
    }

    private boolean isCastDevice(MediaRouter.RouteInfo routeInfo) {
        return routeInfo.getId().contains(CHROMECAST_SIGNATURE);
    }

    @UiThread
    private MediaRouter.RouteInfo getActiveChromecastRoute() {
        for (MediaRouter.RouteInfo route : mMediaRouter.getRoutes()) {
            if (isCastDevice(route)) {
                if (route.getConnectionState() == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED) {
                    return route;
                }
            }
        }
        return null;
    }

    @UiThread
    private boolean isChromecastActive() {
        return getActiveChromecastRoute() != null;
    }

    @UiThread
    private MediaRouter.RouteInfo getActiveMediaRoute() {
        if (isChromecastActive()) {
            MediaRouter.RouteInfo route = getActiveChromecastRoute();
            if (route != null) {
                Toast toast = Toast.makeText(context, "Found: " + route.getName(), Toast.LENGTH_SHORT);
                toast.show();
                if (!route.isSelected()) {
                    mMediaRouter.selectRoute(route);
                }
            } else if (mSelectedDevice != null) {
                mSelectedDevice = null;
            }
            return route;
        }
        return null;
    }

    public void refreshMediaRoute() {
        getActiveMediaRoute();
    }
}
