package com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by manoj.krishnan on 5/24/16.
 */

public final class FeedDataWallpapers {

    public static FeedItem[] mSampleFeedItems;

    public FeedDataWallpapers(int count){
        mSampleFeedItems = new FeedItem[count];
    }

  /*  static {
        mSampleFeedItems[0] = new FeedItem("quotes1","http://motivationpics.s3-ap-southeast-1.amazonaws.com/02Aug_1.jpg");
        mSampleFeedItems[1] = new FeedItem("quotes2","http://motivationpics.s3-ap-southeast-1.amazonaws.com/02Aug_2.jpg");
        mSampleFeedItems[2] = new FeedItem("quotes3","http://motivationpics.s3-ap-southeast-1.amazonaws.com/15Aug_3.jpg");
        mSampleFeedItems[3] = new FeedItem("quotes4","http://motivationpics.s3-ap-southeast-1.amazonaws.com/26Aug_4.jpg");
        mSampleFeedItems[4] = new FeedItem("quotes5","  http://motivationpics.s3-ap-southeast-1.amazonaws.com/26Aug_5.jpg");
        mSampleFeedItems[5] = new FeedItem("quotes6","http://motivationpics.s3-ap-southeast-1.amazonaws.com/28Aug_6.jpg");
    }*/

    public static class FeedItem {
        private String bigImage;
        public FeedItem(String bigImage) {
            this.bigImage = bigImage;
        }

        public String getImage() {
            return bigImage;
        }

    }

    public static ArrayList<FeedItem> generateFeedItems(int count) {

        final ArrayList<FeedItem> feedItems = new ArrayList<>(count);
        Random random = new Random();
        for (int i = 0; i < mSampleFeedItems.length; i++) {
            //feedItems.add(mSampleFeedItems[random.nextInt(mSampleFeedItems.length)]);
            feedItems.add(mSampleFeedItems[i]);
        }
        return feedItems;
    }


}
