package com.limhenry.androidthings.digitalclock;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SpotifyAccountsClient {
    @FormUrlEncoded
    @POST("/api/token")
    Call<JsonObject> getAccessToken(
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token,
            @Header("Authorization") String authorization);
}
