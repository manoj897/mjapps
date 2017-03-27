package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.system.Os;
import android.util.Log;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.LeadershipAndMotivation;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.MainActivity;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class AlaramReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private KeyValueStore mKeyValueStore;
    private KeyValueStore mKeyValueStoreBitMap;
    private PendingIntent mpendingIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

       /* Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);*/
        /*long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, LeadershipAndMotivation.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.share)
                .setContentTitle("Alaram Fired")
                .setContentText("Events To be Performed").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(Integer.parseInt(String.valueOf(System.currentTimeMillis())), mNotifyBuilder.build());
*/


/*
            if( (intent.getExtras() != null) && (intent.getExtras().get("intentFromAlarmManager") != null)){
                //if image downloaded then
                //  show Notification
                //  Reset the CacheImageforSecondDay
                //else
                //  increment the counter
                //  Download image
                //  and Show Notification
                //  Reset the CacheImageforSecondDay
                //put in preference that dailyRoutine trigerred
            }else{

                //  If (Connected)
                //     if image downloaded then
                //        do nothing
                //      else
                //          Increment the counter
                //          Download the image
                //          set the CacheImageforSecondDay
                //  else
                //      Do Nothing
            }*/

        Log.d("alarm","====Broadcast Received=====");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("alarm","====Notifications scheduled after reboot of Device=====");
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent myIntent = new Intent(context.getApplicationContext(), AlaramReceiver.class);
            myIntent.putExtra("intentFromAlarmManager", true);
            mpendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, myIntent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY,07);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            long dayDelay = 24*60*60*1000;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dayDelay+calendar.getTimeInMillis(), mpendingIntent);
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mpendingIntent);
        }
        mKeyValueStore = KeyValueStore.getInstance(context.getApplicationContext(), "QuotesCounter");
        mKeyValueStoreBitMap = KeyValueStore.getInstance(context.getApplicationContext(), "Routine");

        Intent msgIntent = new Intent(context, DownloadImageService.class);
        if( (intent.getExtras() != null) && (intent.getExtras().get("intentFromAlarmManager") != null)) {
            //Increment the counter
            Log.d("BroadCastReceiver","Intent from AlarmManager");
            if(mKeyValueStore.getInt("counter",0) == 0) {
                mKeyValueStore.putInt("counter", 1);
                mKeyValueStore.putLong("timeStamp", System.currentTimeMillis());
            }
            else if(mKeyValueStore.getInt("counter",0) > 0) {
                mKeyValueStore.putInt("counter", mKeyValueStore.getInt("counter", 0) + 1);
            }

            msgIntent.putExtra("CacheImageForSecondDay","false");
            msgIntent.putExtra("DailyRoutine","true");
            msgIntent.putExtra("imageUrl", "http://motivationpics.s3-ap-southeast-1.amazonaws.com/" + mKeyValueStore.getInt("counter", 1) + ".jpg");
            context.startService(msgIntent);
        }else {
            Log.d("BroadCastReceiver","Connectivity change");
            boolean isServiceEnabled = false;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    isServiceEnabled = true;
                }
            }
            if(isServiceEnabled) {
                Log.d("BroadCastReceiver", "Caching the second day Image");
                if (mKeyValueStoreBitMap.getBoolean("NextRoutineCached", false))
                    msgIntent.putExtra("CacheImageForSecondDay", "true");
                else
                    msgIntent.putExtra("CacheImageForSecondDay", "false");
                msgIntent.putExtra("DailyRoutine", "false");
                msgIntent.putExtra("imageUrl", "http://motivationpics.s3-ap-southeast-1.amazonaws.com/" + (mKeyValueStore.getInt("counter", 1) + 1) + ".jpg");
                context.startService(msgIntent);
            }
        }





    }

}

class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, LeadershipAndMotivation.class);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Bananas");
        builder.setContentText("get your bananas");
        builder.setSmallIcon(R.drawable.share);
        builder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}