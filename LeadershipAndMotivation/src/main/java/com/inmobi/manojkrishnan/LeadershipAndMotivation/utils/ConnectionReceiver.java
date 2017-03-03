package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by manoj.krishnan on 22/02/17.
 */

public class ConnectionReceiver extends BroadcastReceiver{
    private KeyValueStore mKeyValueStore;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        if (isConnected) {
            Log.i("NET", "connected - " + isConnected);
            mKeyValueStore = KeyValueStore.getInstance(context.getApplicationContext(), "QuotesCounter");

            if(mKeyValueStore.getInt("counter",0) > 0) {

                Glide.with(context.getApplicationContext())
                        .load("http://motivationpics.s3-ap-southeast-1.amazonaws.com/" + (mKeyValueStore.getInt("counter",1)+1) + ".jpg")
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(500, 400).priority(Priority.IMMEDIATE);
            }
        }
        else Log.i("NET", "not connected - " +isConnected);



    }
}
