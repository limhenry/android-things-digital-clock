package com.limhenry.androidthings.digitalclock;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface SpotifyClient {
    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    Call<JsonObject> getAccessToken(
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token,
            @Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @POST("https://api.spotify.com/v1/me/player/previous")
    Call<JsonObject> previousTrack(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @POST("https://api.spotify.com/v1/me/player/next")
    Call<JsonObject> nextTrack(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @GET("https://api.spotify.com/v1/me/player")
    Call<JsonObject> getCurrentPlaying(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @PUT("https://api.spotify.com/v1/me/player/play")
    Call<JsonObject> playTrack(@Header("Authorization") String authorization);

    @Headers("Content-Type: application/json")
    @PUT("https://api.spotify.com/v1/me/player/pause")
    Call<JsonObject> pauseTrack(@Header("Authorization") String authorization);
}
