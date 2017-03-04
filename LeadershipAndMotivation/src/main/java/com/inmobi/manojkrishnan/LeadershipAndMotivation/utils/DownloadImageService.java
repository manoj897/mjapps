package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.LeadershipAndMotivation;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.QuotesFragment;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by manoj.krishnan on 01/03/17.
 */

public class DownloadImageService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private String imgUrl;
    private String dailyRoutine;
    private String cacheImageForSecondDay;
    private Context ctxt;
    private KeyValueStore mKeyValueStore;
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    public DownloadImageService() {
        super("DownloadImageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("testIntentService","service started");
        ctxt = this;
        imgUrl = intent.getStringExtra("imageUrl");
        dailyRoutine = intent.getStringExtra("DailyRoutine");
        cacheImageForSecondDay = intent.getStringExtra("CacheImageForSecondDay");
        Log.d("testIntentService","image Url is"+imgUrl);
        Log.d("testIntentService","DailyRoutine is"+dailyRoutine);
        mKeyValueStore = KeyValueStore.getInstance(this.getApplicationContext(), "ImageBitMap");

        if(dailyRoutine.equalsIgnoreCase("false")) {
            Bitmap bitmap = null;
            if (cacheImageForSecondDay.equalsIgnoreCase("false")) {
                Log.d("testIntentService", "Loading the image");
                FutureTarget<Bitmap> target = Glide.with(this.getApplicationContext())
                        .load(imgUrl)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(500, 400).priority(Priority.IMMEDIATE)
                        .into(500, 400);
                bitmap = null;
                try {
                    bitmap = target.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d("testIntentService", "Image Ready");
                saveImageToInternalStorage(bitmap);
                mKeyValueStore.putBoolean("available", true);
            }
            else
                Log.d("testIntentService", "Image already downloaded");
        }else {

            Bitmap bitmap = null;
            if (cacheImageForSecondDay.equalsIgnoreCase("false")) {
                Log.d("testIntentService", "Loading the image");
                FutureTarget<Bitmap> target = Glide.with(this.getApplicationContext())
                        .load(imgUrl)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(500, 400).priority(Priority.IMMEDIATE)
                        .into(500, 400);
                bitmap = null;
                try {
                    bitmap = target.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d("testIntentService", "Image Ready");
                saveImageToInternalStorage(bitmap);
                mKeyValueStore.putBoolean("available", true);
            }else
                Log.d("testIntentService", "Image already downloaded");


            Log.d("testIntentService", "Showing Notification");
            if (Build.VERSION.SDK_INT < LOLLIPOP) {
                Context mcontext = ctxt.getApplicationContext();
                notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
                Intent mIntent = new Intent(mcontext, LeadershipAndMotivation.class);
                pendingIntent = PendingIntent.getActivity(mcontext, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext);
                builder.setContentTitle("Quote for the Day");
                builder.setContentText("GoodMorning!!").setLights(Color.GREEN, 300, 300);
                builder.setSmallIcon(R.drawable.leadershiplogo);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);

                notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                mKeyValueStore.putBoolean("ImageBitMapCached", false);
                Log.d("alarm", "====Notification sent=====");
            } else {
                Context mcontext = ctxt.getApplicationContext();
                notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
                Intent mIntent = new Intent(mcontext, LeadershipAndMotivation.class);
                pendingIntent = PendingIntent.getActivity(mcontext, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext);
                builder.setContentTitle("Quote for the Day");
                builder.setContentText("GoodMorning!!").setLights(Color.GREEN, 300, 300);
                int color = 0xff123456;
                builder.setColor(color);
                builder.setSmallIcon(R.drawable.logonotification);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);

                notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                mKeyValueStore.putBoolean("ImageBitMapCached", false);
                Log.d("alarm", "====Notification sent=====");
            }

        }

    }

    public boolean saveImageToInternalStorage(Bitmap image) {
        try {
            FileOutputStream fos = ctxt.openFileOutput("QuotesCounter.png", ctxt.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            if(dailyRoutine.equalsIgnoreCase("false"))
                mKeyValueStore.putBoolean("ImageBitMapCached",true);
            Log.d("testIntentService","BitMap stored in file");
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            mKeyValueStore.putBoolean("ImageBitMapCached",false);
            return false;
        }
    }
}

