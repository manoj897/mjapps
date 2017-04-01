package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;

import java.io.File;
import java.io.FileInputStream;


public class LeadershipAndMotivation extends AppCompatActivity {
    PendingIntent mpendingIntent;
    private KeyValueStore mKeyValueStoreImage;
    private KeyValueStore mKeyValueStore;
    private Bitmap quotesBitMap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_in_one_motivation);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mKeyValueStore = KeyValueStore.getInstance(LeadershipAndMotivation.this.getApplicationContext(), "QuotesCounter");
        mKeyValueStoreImage = KeyValueStore.getInstance(LeadershipAndMotivation.this.getApplicationContext(), "Routine");



        if (mKeyValueStore.getInt("counter", 0) == 0) {
            mKeyValueStore.putInt("counter", 1);
            Log.d("QuotesFragment", "count when first time " + mKeyValueStore.getInt("counter", 1));

        }
        Log.d("testQuotes", "counter value == " + String.valueOf(mKeyValueStore.getInt("counter", 1)));

        if(!mKeyValueStoreImage.getBoolean("DailyRoutineCached",false)) {//If file is not present in Cache
            SimpleTarget target2 = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    mKeyValueStoreImage.putBoolean("availableDailyRoutine",true);
                }
            };

            Glide.with(this.getApplicationContext())
                    .load("http://motivationpics.s3-ap-southeast-1.amazonaws.com/" + mKeyValueStore.getInt("counter", 1) + ".jpg")
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(500, 400).priority(Priority.IMMEDIATE)
                    .into(target2);


        }else {//If File is present in cache
            Log.d("testQuotes","Image already downloaded");
        }
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_TABS = 3;

        private static final int POSITION_QUOTE_FOR_THE_DAY = 0;

        private static final int POSITION_WALLPAPERS = 3;

        private static final int POSITION_BLOGS = 1;

        private static final int POSITION_ABOUT_ME = 2;




        public PagerAdapter(FragmentManager fm, TabLayout tabLayout) {
            super(fm);
            for (int position = 0; position < NUM_TABS; position++) {
                tabLayout.addTab(tabLayout.newTab().setText(getTitle(position)));
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POSITION_QUOTE_FOR_THE_DAY:
                    return new QuotesFragment();

                case POSITION_WALLPAPERS:
                    return new WallpaperFragment();

                case POSITION_BLOGS:
                    return new BlogsFragment();

                case POSITION_ABOUT_ME:
                    return new AboutMeFragment();

                default:
                    throw new IllegalArgumentException("No fragment for position:" + position);
            }
        }

        String getTitle(int position) {
            switch (position) {
                case POSITION_QUOTE_FOR_THE_DAY:
                    return "Today's Motivation";

                case POSITION_WALLPAPERS:
                    return "Motivational Quotes";

                case POSITION_BLOGS:
                    return "Leadership Thoughts";

                case POSITION_ABOUT_ME:
                    return "About Us";

                default:
                    throw new IllegalArgumentException("No Title for position:" + position);
            }
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }
    }

    public static class NonSwipeableViewPager extends ViewPager {

        public NonSwipeableViewPager(Context context) {
            super(context);
        }

        public NonSwipeableViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }
    }

    public static class DummySectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text));
            return rootView;
        }
    }




}
