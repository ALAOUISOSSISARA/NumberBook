package com.example.numberbook;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";
    private static Retrofit instance;

    // Private constructor — prevents direct instantiation
    private ApiClient() {}

    public static NumberBookApi getApi() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance.create(NumberBookApi.class);
    }
}