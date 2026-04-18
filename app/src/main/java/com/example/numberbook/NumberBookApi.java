package com.example.numberbook;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NumberBookApi {

    @POST("addEntry.php")
    Call<ServerResponse> pushEntry(@Body PhoneEntry entry);

    @GET("fetchEntries.php")
    Call<List<PhoneEntry>> pullAllEntries();

    @GET("findEntry.php")
    Call<List<PhoneEntry>> lookupEntries(@Query("keyword") String keyword);
}