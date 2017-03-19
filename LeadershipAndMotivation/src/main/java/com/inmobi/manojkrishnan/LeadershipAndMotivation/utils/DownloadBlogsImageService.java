package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

/**
 * Created by manoj.krishnan on 18/03/17.
 */

public class DownloadBlogsImageService extends IntentService{
    private Context ctxt;

    public DownloadBlogsImageService() {
        super("DownloadBlogsImageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bitmap bitmap = null;
        ctxt = this;
        if(intent.getExtras().getInt("blogImagesCounter",-1)!= -1 &&
                intent.getExtras().getString("blogImageUrl",null)!=null) {
            int count = intent.getExtras().getInt("blogImagesCounter");
            String imgUrl = intent.getExtras().getString("blogImageUrl");
            Log.d("testIntentService", "blogspics started downloading the img - "+imgUrl);
            FutureTarget<Bitmap> target = Glide.with(this.getApplicationContext())
                    .load(imgUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(500, 550).priority(Priority.IMMEDIATE)
                    .into(500, 550);
            try {
                bitmap = target.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("testIntentService", "Image Ready");
            saveImageToInternalStorage(bitmap, count);
        }else if(intent.getExtras().getBoolean("complete",false)){
            Log.d("testIntentService", "Final Intent and hence writing into file");
            try {
                FileOutputStream fos = ctxt.openFileOutput("blogspicsdownloaded.txt", ctxt.MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
                outputStreamWriter.write("true");
                outputStreamWriter.close();
            } catch (Exception e) {
                Log.e("Blog Images not downloaded completely", e.getMessage());

            }
        }

    }
    public boolean saveImageToInternalStorage(Bitmap image,int counter) {
        try {
            FileOutputStream fos = ctxt.openFileOutput("blogspics"+counter+".png", ctxt.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Log.d("testIntentService", "blogspics"+counter + " bitmap return to a file");
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }
}
