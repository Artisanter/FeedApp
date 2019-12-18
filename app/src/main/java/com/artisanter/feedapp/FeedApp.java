package com.artisanter.feedapp;

import android.app.Application;

import com.squareup.picasso.Picasso;

public class FeedApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setPicasso();
    }

    void setPicasso(){
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new CustomCache(1024 * 1024 * 100))
                .build();
        if(android.os.Debug.isDebuggerConnected())
            picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }
}
