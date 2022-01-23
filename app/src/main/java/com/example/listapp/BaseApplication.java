package com.example.listapp;

import android.app.Application;

import com.downloader.PRDownloader;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PRDownloader.initialize(getApplicationContext());
    }
}
