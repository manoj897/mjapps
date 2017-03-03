package com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by manoj.krishnan on 5/25/16.
 */
public class FeedDataBlogs {
    public static FeedItem[] mSampleFeedItems;
    public FeedDataBlogs(int count) {
        mSampleFeedItems = new FeedItem[count];
    }

    static {
        /*mSampleFeedItems[0] = new FeedItem("quotes1","Three ways to be on top of your work - 3D's");
        mSampleFeedItems[1] = new FeedItem("quotes2", "Five disfunctions of a team");
        mSampleFeedItems[2] = new FeedItem("quotes3","push yourself to the next level");
        mSampleFeedItems[3] = new FeedItem("quotes4","How to Lead the Leaders");
        mSampleFeedItems[4] = new FeedItem("quotes5","How to be an Entreprenuer");
        mSampleFeedItems[5] = new FeedItem("quotes6","7F needed for your life");*/
    }

    public static class FeedItem {

        private String bigImage;

        private String thumbnail;

        private String content;

        private String title;

        public FeedItem(String bigImage, String content, String title,String thumbnail) {
            this.bigImage = bigImage;
            this.content = content;
            this.title = title;
            this.thumbnail = thumbnail;
        }



        public String getBigImage() {
            return bigImage;
        }

        public String gettitle(){
            return title;
        }

        public String getContent() { return content;}

        public String getThumbnail(){ return thumbnail;}



    }

    public static ArrayList<FeedItem> generateFeedItems() {
        int count = 10;
        final ArrayList<FeedItem> feedItems = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < mSampleFeedItems.length; i++) {
            feedItems.add(mSampleFeedItems[i]);
        }
        return feedItems;
    }


}


