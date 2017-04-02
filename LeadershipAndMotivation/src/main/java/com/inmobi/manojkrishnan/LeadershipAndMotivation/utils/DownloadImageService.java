package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.app.AlarmManager;
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
import com.inmobi.manojkrishnan.LeadershipAndMotivation.MainActivity;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.QuotesFragment;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private PendingIntent mpendingIntent;

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
        Log.d("testIntentService","image Url is "+imgUrl);
        Log.d("testIntentService","DailyRoutine is "+dailyRoutine);
        Log.d("testIntentService","cacheImageForSecondDay is "+cacheImageForSecondDay);
        mKeyValueStore = KeyValueStore.getInstance(this.getApplicationContext(), "Routine");

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
                    bitmap = target.get(120, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                Log.d("testIntentService", "Image Ready for second Day");
                saveImageToInternalStorage(bitmap,false);
            }
            else
                Log.d("testIntentService", "Image for Next Day routine already downloaded");
        }else {
            Bitmap bitmap = null;
                if(mKeyValueStore.getBoolean("NextRoutineCached",false)) {
                    Log.d("testIntentService", "Image already downloaded");
                    //File swap from nextDayRoutine to dailyRoutine
                    Log.d("testIntentService", "swapping the image to dailyRoutine");
                    Bitmap dailyRoutineBitMap = null;
                    try {
                        File filepath = getFileStreamPath("nextDayRoutine.png");
                        dailyRoutineBitMap = BitmapFactory.decodeFile(filepath.getAbsolutePath());
                        saveImageToInternalStorage(dailyRoutineBitMap, true);
                    } catch (Exception e) {
                        registerNextRoutine();
                        e.printStackTrace();
                    }
                }else {
                    Log.d("testIntentService", "Loading the image for DailyRoutine");
                    FutureTarget<Bitmap> target = Glide.with(this.getApplicationContext())
                        .load(imgUrl)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(500, 400).priority(Priority.IMMEDIATE)
                        .into(500, 400);
                    bitmap = null;
                    try {
                        bitmap = target.get(120, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    Log.d("testIntentService", "Daily Routine Image Downloaded");
                    saveImageToInternalStorage(bitmap,true);
                }

            if(mKeyValueStore.getBoolean("DailyRoutineCached",false)) {
                Log.d("testIntentService", "Showing Notification");
                buildNotification();
            }

        }

    }

    private void buildNotification(){
        if (Build.VERSION.SDK_INT < LOLLIPOP) {
            Context mcontext = ctxt.getApplicationContext();
            notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
            Intent mIntent = new Intent(mcontext, LeadershipAndMotivation.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(mcontext, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext);
            builder.setContentTitle("Quote for the Day");
            builder.setContentText("GoodMorning!!").setLights(Color.GREEN, 300, 300);
            builder.setSmallIcon(R.drawable.leadershiplogo);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);

            notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            mKeyValueStore.putBoolean("availableDailyRoutine", true);
            mKeyValueStore.putBoolean("NextRoutineCached", false);

            Log.d("alarm", "====Notification sent=====");
        } else {
            Context mcontext = ctxt.getApplicationContext();
            notificationManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
            Intent mIntent = new Intent(mcontext, LeadershipAndMotivation.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            mKeyValueStore.putBoolean("availableDailyRoutine", true);
            mKeyValueStore.putBoolean("NextRoutineCached", false);
            Log.d("alarm", "====Notification sent=====");
        }
        registerNextRoutine();
    }

    private void registerNextRoutine() {
        Log.d("alarm", "====Next Routine Notification registered=====");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(ctxt.getApplicationContext(), AlaramReceiver.class);
        myIntent.putExtra("intentFromAlarmManager", true);
        mpendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, myIntent, 0);
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 00);*/
        long dayDelay = 15*60*1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dayDelay+System.currentTimeMillis(), mpendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, dayDelay+System.currentTimeMillis(), mpendingIntent);
    }


    public boolean saveImageToInternalStorage(Bitmap image,boolean dailyRoutine) {
        try {
            if(image == null ) {
                mKeyValueStore.putBoolean("DailyRoutineCached", false);
                mKeyValueStore.putBoolean("NextRoutineCached", false);
                registerNextRoutine();
                return  false;
            }

            FileOutputStream fos = null;
            if(dailyRoutine)
                fos = ctxt.openFileOutput("dailyRoutine.png", ctxt.MODE_PRIVATE);
            else
                fos = ctxt.openFileOutput("nextDayRoutine.png", ctxt.MODE_PRIVATE);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            if(dailyRoutine)
                mKeyValueStore.putBoolean("DailyRoutineCached",true);
            else
                mKeyValueStore.putBoolean("NextRoutineCached",true);
            Log.d("testIntentService","BitMap stored in file");
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            mKeyValueStore.putBoolean("NextRoutineCached",false);
            mKeyValueStore.putBoolean("DailyRoutineCached",false);
            registerNextRoutine();
            return false;
        }
    }
}

