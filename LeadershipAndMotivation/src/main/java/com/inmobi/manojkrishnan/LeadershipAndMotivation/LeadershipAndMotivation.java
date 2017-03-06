package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class LeadershipAndMotivation extends AppCompatActivity {
    PendingIntent mpendingIntent;
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

       /* AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(LeadershipAndMotivation.this , AlaramReceiver.class);
        mpendingIntent = PendingIntent.getBroadcast(LeadershipAndMotivation.this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();


        calendar.set(Calendar.HOUR_OF_DAY, 07);
        calendar.set(Calendar.MINUTE,20);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, mpendingIntent);

*/
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_TABS = 4;

        private static final int POSITION_QUOTE_FOR_THE_DAY = 0;

        private static final int POSITION_WALLPAPERS = 1;

        private static final int POSITION_BLOGS = 2;

        private static final int POSITION_ABOUT_ME = 3;




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
