package com.rpos.pos.domain.utils;

import com.rpos.pos.Config;
import com.rpos.pos.data.remote.api.ApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final String BASE_URL = Config.BASE_URL;
    public static final int CONNECTION_PROBLEM = -1;
    public static final String NO_INTERNET_CONNECTION = "No internet connection";
    private static final String SERVER_NOT_REACHABLE = "Server not reachable, please try again later.";
    private static NetworkManager mManager;
    private ApiService mApiInterface;

    public NetworkManager()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(10, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(new NullOnEmptyConverterFactory()).
                addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).build();
        mApiInterface = retrofit.create(ApiService.class);
    }

    public static NetworkManager getInstance() {
        if (mManager == null) {
            mManager = new NetworkManager();
        }
        return mManager;
    }
}
