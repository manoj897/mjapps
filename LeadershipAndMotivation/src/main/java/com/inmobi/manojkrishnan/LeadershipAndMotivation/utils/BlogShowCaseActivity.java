package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.BlogsFragment;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.R;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkHandler;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkResponse;
import com.squareup.picasso.Picasso;

public class BlogShowCaseActivity extends AppCompatActivity {
private TextView mTextView;
    private ImageView mImageView;
    private NetworkResponse mNetworkResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        mTextView = (TextView)findViewById(R.id.txtView);
        mImageView = (ImageView)findViewById(R.id.imgView);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Intent i = getIntent();
        blogData blogData = (blogData)i.getSerializableExtra("BlogItem");
        //mTextView.setText(blogData.getContent());
        final String blogContentUrl = blogData.getContent();
        //Picasso.with(this).load(blogData.getImage()).resize(300,300).into(mImageView);
        Glide.with(this.getApplicationContext())
                .load(blogData.getImage()).thumbnail(0.2f).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(500,400)
                .into(mImageView);




        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler hdlr = new NetworkHandler();
                mNetworkResponse = hdlr.connect(blogContentUrl);
                if(mNetworkResponse != null)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(mNetworkResponse.getResponse());
                        }
                    });
            }
        });
        t.start();

    }
}
