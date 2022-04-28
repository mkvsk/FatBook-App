package com.fatbook.fatbookapp.retrofit;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitFactory {

    private static final String HTTP = "http://";
    private static final String ADDRESS = "10.0.2.2:8080";
    private static final String ENDPOINT = "/api/";
    private static final String URL = HTTP + ADDRESS + ENDPOINT;

    public static NetworkInfoService infoServiceClient() {
        return new Retrofit.Builder()
                .baseUrl(RetrofitFactory.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(NetworkInfoService.class);
    }

}
