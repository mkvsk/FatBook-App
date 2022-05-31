package com.fatbook.fatbookapp.retrofit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitFactory {

    private static final String HTTP = "http://";
    private static final String ADDRESS = "10.0.2.2:8080/";
//    private static final String ADDRESS = "192.168.0.120:8080";
    private static final String ENDPOINT = "api/";
    private static final String URL_ENDPOINT = HTTP + ADDRESS + ENDPOINT;
    public static final String URL = HTTP + ADDRESS;

    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static NetworkInfoService apiServiceClient() {
        return new Retrofit.Builder()
                .baseUrl(RetrofitFactory.URL_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(NetworkInfoService.class);
    }

}
