package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.BlogsFragment;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.R;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkHandler;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkResponse;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class BlogShowCaseActivity extends AppCompatActivity {
private TextView mTextView;
    private ImageView mImageView;
    private NetworkResponse mNetworkResponse;
    private boolean didBlogContentAlreadyCached = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        mTextView = (TextView)findViewById(R.id.txtView);
        mImageView = (ImageView)findViewById(R.id.imgView);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ctl.setTitle(i.getExtras().getString("BlogTitle"));


        blogData blogData = (blogData)i.getSerializableExtra("BlogItem");
        final String blogContentUrl = blogData.getContent();
        String blogImageFileName= i.getExtras().getString("BlogImage");
        final String blogContentFileName = i.getExtras().getString("BlogContent");



        //Loading Image
        Bitmap actualImage = null;
        try {
            File filePath = this.getFileStreamPath(blogImageFileName);
            FileInputStream fi = new FileInputStream(filePath);
            actualImage = BitmapFactory.decodeStream(fi);
            fi.close();
        } catch (Exception ex) {
            Log.e("getThumbnail() on internal storage", ex.getMessage());
        }
        if(actualImage != null){
            mImageView.setImageBitmap(actualImage);
            Log.d("BlogsShowCaseActivity","using downloaded bitmap");
        }else if(NetworkUtils.isNetworkAvailable(this)){
            Glide.with(this.getApplicationContext())
                    .load(blogData.getImage()).thumbnail(0.2f).diskCacheStrategy(DiskCacheStrategy.RESULT).override(500,550)
                    .into(mImageView);
            Log.d("BlogsShowCaseActivity","Downloading Image");
        }else{
            Toast.makeText(this, "Please connect to network to Read the full blog!", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        String blogContent = null;
        try {
            File filePath = this.getFileStreamPath(blogContentFileName);
            FileInputStream fi = new FileInputStream(filePath);
            if ( fi != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fi);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString+"\n");
                }
                fi.close();
                blogContent = stringBuilder.toString();
                Log.d("BlogsShowCaseActivity","using downloaded Blog's content");
            }
            fi.close();

        } catch (Exception ex) {
            Log.e("getThumbnail() on internal storage", ex.getMessage());
        }
        if(blogContent != null){
            mTextView.setText(blogContent);
        }else if(NetworkUtils.isNetworkAvailable(this)){
            Log.d("BlogsShowCaseActivity","Downloading Content");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHandler hdlr = new NetworkHandler();
                    mNetworkResponse = hdlr.connect(blogContentUrl);
                    if (mNetworkResponse != null)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText(mNetworkResponse.getResponse());
                                try {
                                    FileOutputStream fos = BlogShowCaseActivity.this.openFileOutput(blogContentFileName, BlogShowCaseActivity.this.MODE_PRIVATE);
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
                                    outputStreamWriter.write(mNetworkResponse.getResponse());
                                    outputStreamWriter.close();
                                    Log.d("BlogsShowCaseActivity","Content Cached");
                                } catch (Exception e) {
                                    Log.e("Content not downloaded completely", e.getMessage());

                                }
                            }
                        });
                }
            });
            t.start();
        }else{
            Toast.makeText(this, "Please connect to network to Read the full blog!", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
}
