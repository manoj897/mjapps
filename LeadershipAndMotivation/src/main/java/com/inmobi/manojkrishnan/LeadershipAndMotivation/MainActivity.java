package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkHandler;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkResponse;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.AlaramReceiver;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.ContextHolder;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.Parser;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.Setup;
import com.squareup.picasso.Picasso;
import com.ugurtekbas.fadingindicatorlibrary.FadingIndicator;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private HttpURLConnection mHttpUrlConnection;
    private NetworkResponse mResponse;
    private volatile static Picasso sPicasso;
    private ImageView img;
    IntroductionPagerAdapter mIntroductionPagerAdapter;
    PendingIntent mpendingIntent;
    private KeyValueStore mKeyValueStore;
    private KeyValueStore mKeyValueStore4BlogsInit;
    private KeyValueStore mKeyValueStore4WallpaperInit;
    private boolean mintialize=false;
    private boolean mBlogintialize=false;
    private boolean mWallpaperintialize=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new Setup();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Stetho.initializeWithDefaults(this);

//        img = (ImageView)findViewById(R.id.testImage);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/

        ContextHolder.init(this);


//        connectImage();
        FadingIndicator indicator = (FadingIndicator) findViewById(R.id.circleIndicator);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new IntroductionPagerAdapter(this, populateContentPositions()));
        indicator.setViewPager(viewPager);
        indicator.setFillColor(Color.GRAY);
        indicator.setStrokeColor(Color.WHITE);
        indicator.setRadius(20f);


        Button btn = (Button)findViewById(R.id.startOffTitle);
        btn.setOnClickListener(this);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(MainActivity.this, "Please exit and connect to network to get started",
                    Toast.LENGTH_SHORT).show();
            mintialize = false;
            return;
        }

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(MainActivity.this , AlaramReceiver.class);
        myIntent.putExtra("intentFromAlarmManager",true);
        mpendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();


        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE,31);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, mpendingIntent);



        mKeyValueStore = KeyValueStore.getInstance(MainActivity.this.getApplicationContext(), "QuotesCounter");
        if(mKeyValueStore.getInt("counter",0) == 0) {
            mKeyValueStore.putInt("counter", 1);
            Log.d("MainActivity", "count when first time  " + mKeyValueStore.getInt("counter", 1));

        }

        //initialize Blogs
        mKeyValueStore4BlogsInit = KeyValueStore.getInstance(MainActivity.this.getApplicationContext(), "BlogsInit");
        if(mKeyValueStore4BlogsInit.getBoolean("init",false) &&
                (null != mKeyValueStore4BlogsInit.getString("blogsGrammar",null)))
            mBlogintialize = true;
        else {
            Thread blogGrammar = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHandler hdlr = new NetworkHandler();
                    mResponse = hdlr.connect("http://motivationblogs.s3-ap-southeast-1.amazonaws.com/blogsGrammar.txt");
                    if (mResponse != null) {
                        Log.d("testParsing", "Going to parse blogs");
                        Parser.parseBlogGrammar(mResponse.getResponse());
                        mBlogintialize = true;
                        mKeyValueStore4BlogsInit.putBoolean("init",true);
                        mKeyValueStore4BlogsInit.putString("blogsGrammar",mResponse.getResponse());
                    } else {
                        Log.d("testParsing", "Response is null");
                        mBlogintialize = false;
                        mKeyValueStore4BlogsInit.putBoolean("init",false);
                    }
                }
            });
            blogGrammar.start();
        }


        mKeyValueStore = KeyValueStore.getInstance(MainActivity.this.getApplicationContext(), "QuotesCounter");
        if(mKeyValueStore.getInt("counter",0) == 0) {
            mKeyValueStore.putInt("counter", 1);
            Log.d("MainActivity", "count when first time  " + mKeyValueStore.getInt("counter", 1));

        }
        //Initialize Wallpaper
        mKeyValueStore4WallpaperInit = KeyValueStore.getInstance(MainActivity.this.getApplicationContext(), "WallpaperInit");
        if(mKeyValueStore4WallpaperInit.getBoolean("init",false) &&
                (null != mKeyValueStore4WallpaperInit.getString("wallpaperGrammar",null)))
            mWallpaperintialize = true;
        else {
            Thread WallpaperGrammar = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHandler hdlr = new NetworkHandler();
                    mResponse = hdlr.connect("http://motivationpics.s3-ap-southeast-1.amazonaws.com/grammar/wallpapergrammar.txt");
                    if (mResponse != null) {
                        Parser.parseWallpaperGrammar(MainActivity.this, mResponse.getResponse(), mKeyValueStore.getInt("counter", 1));
                        mKeyValueStore4WallpaperInit.putBoolean("init",true);
                        mKeyValueStore4WallpaperInit.putString("wallpaperGrammar",mResponse.getResponse());
                        mWallpaperintialize = true;
                    } else {
                        mKeyValueStore4WallpaperInit.putBoolean("init",false);
                        mWallpaperintialize = false;
                    }
                }
            });
            WallpaperGrammar.start();
        }
        mintialize = true;
    }




    @Override
    public void onClick(View v) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(MainActivity.this, "Please exit and connect to network to get started",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(mintialize && mBlogintialize && mWallpaperintialize) {
            Intent inst = new Intent(this, LeadershipAndMotivation.class);
            startActivity(inst);
        }else
            Toast.makeText(MainActivity.this, "App is not initialized, please wait or exit and launch again!!",
                    Toast.LENGTH_SHORT).show();

    }


    static class IntroductionFeeds {
        public String title;

        public int big_image;

        public IntroductionFeeds(String title,   int big_image) {
            this.title = title;
            this.big_image = big_image;

        }
    }
    private final IntroductionFeeds[] mIntroductionFeeds = new IntroductionFeeds[3];

    private ArrayList<IntroductionFeeds> populateContentPositions() {
        ArrayList<IntroductionFeeds> introList = new ArrayList<>();
        mIntroductionFeeds[0] = new IntroductionFeeds("HOLA, We're gonna MOTIVATE you 365 Days!",R.drawable.motivation2);
        mIntroductionFeeds[1] = new IntroductionFeeds("YOU are gonna be Challenged to be all that you CAN be.",R.drawable.motivation3);
        mIntroductionFeeds[2] = new IntroductionFeeds("I Love it. MOTIVATE ME!",R.drawable.motivation1);

        introList.add(mIntroductionFeeds[0]);
        introList.add(mIntroductionFeeds[1]);
        introList.add(mIntroductionFeeds[2]);

    return introList;
    }

        public  class IntroductionPagerAdapter extends PagerAdapter {

            public ArrayList<IntroductionFeeds> introListInst;
            Context ctxt;
            class ContentViewHolder {
                TextView title;
                Button startOffTitle;
                ImageView big_image;

            }

            public IntroductionPagerAdapter(Context ctxt, ArrayList<IntroductionFeeds> introListInst){
                this.introListInst = introListInst;
                this.ctxt = ctxt;
            }




        @Override
        public int getCount() {
            return introListInst.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
             String title;
             String startOffTitle;
             String big_image;
            ContentViewHolder viewHolder = new ContentViewHolder();
            LayoutInflater inflater = LayoutInflater.from(ctxt);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.listitem, container, false);

            viewHolder.title = (TextView)layout.findViewById(R.id.title);
            viewHolder.startOffTitle = (Button)layout.findViewById(R.id.startOffTitle);
            viewHolder.big_image = (ImageView)layout.findViewById(R.id.big_image);
            layout.setTag(viewHolder);
            ContentViewHolder holder = (ContentViewHolder) layout.getTag();
            holder.title.setText(introListInst.get(position).title);
            holder.big_image.setImageDrawable(getResources().getDrawable(introListInst.get(position).big_image));
            //sPicasso.load(getResources().getIdentifier(user.thumb_image, "drawable", getActivity().getPackageName())).into(holder.big_image);

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }



    }

}
