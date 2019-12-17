package com.artisanter.feedapp;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.LruCache;

public class CustomCache extends LruCache {
    public CustomCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public void set(String key, Bitmap bitmap) {
        super.set(key, bitmap);
    }
}
