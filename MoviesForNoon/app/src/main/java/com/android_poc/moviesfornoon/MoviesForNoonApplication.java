package com.android_poc.moviesfornoon;

import android.app.Application;
import android.util.Log;
import com.android_poc.moviesfornoon.utils.AppConstants;

public class MoviesForNoonApplication extends Application {
    private static MoviesForNoonApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MoviesForNoonApplication getInstance() {
        return mInstance;
    }

}
