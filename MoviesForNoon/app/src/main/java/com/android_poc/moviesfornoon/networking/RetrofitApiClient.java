package com.android_poc.moviesfornoon.networking;

import com.android_poc.moviesfornoon.utils.AppConstants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitApiClient {
    private static volatile Retrofit mInstance = null;

    private RetrofitApiClient(){

    }

    public static Retrofit getInstance(){
        if (mInstance == null){
            synchronized (RetrofitApiClient.class){
                mInstance = new retrofit2.Retrofit.Builder()
                        .baseUrl(AppConstants.Companion.getBASE_URL())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpLoggingInterceptor())
                        .build();
            }

        }
        return mInstance;
    }

    private static OkHttpClient httpLoggingInterceptor() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("apikey", AppConstants.Companion.getAPI_KEY())
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        return okHttpClientBuilder.build();
    }
}
