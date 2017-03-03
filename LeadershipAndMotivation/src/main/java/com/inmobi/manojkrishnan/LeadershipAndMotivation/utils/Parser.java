package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.content.Context;
import android.util.Log;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.FeedDataBlogs;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.FeedDataWallpapers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by manoj.krishnan on 6/1/16.
 */
public class Parser {

    public static void parseBlogGrammar(String content){
        int count;
        String contentUrl;
        String imageUrl;
        String title;
        try {
            Log.d("testParsing", "started parcing");
            JSONObject inst = new JSONObject(content);
            count = inst.getInt("count");
            JSONArray items = inst.getJSONArray("item");
            FeedDataBlogs blogs = new FeedDataBlogs(count);

            Log.d("testParsing", "No.of items"+count);
            for(int i = 0;i<count;i++) {

                contentUrl = items.getJSONObject(i).getString("contentUrl");
                imageUrl = items.getJSONObject(i).getString("imageUrl");
                title = items.getJSONObject(i).getString("title");
                blogs.mSampleFeedItems[i] = new FeedDataBlogs.FeedItem(imageUrl,contentUrl,title,imageUrl);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void  parseWallpaperGrammar(Context context,String content,int count){

        String imageUrl;
        int serverCount;
        KeyValueStore mKeyValueStore;
        try {
            if(count!=0) {
                Log.d("testParsing", "=====started parcing");
                JSONObject inst = new JSONObject(content);
                serverCount = inst.getInt("count");
                if(count >= serverCount)
                {
                    //Reset the count
                    mKeyValueStore = KeyValueStore.getInstance(context.getApplicationContext(), "QuotesCounter");
                        mKeyValueStore.putInt("counter", 1);
                    Log.d("Parser", "count during reset " + mKeyValueStore.getInt("counter", 1));

                }
                JSONArray items = inst.getJSONArray("item");
                FeedDataWallpapers wallpapers = new FeedDataWallpapers(count);
                Log.d("testParsing", "=====Got the item Array");
                for (int i = 0; i < count; i++) {
                    imageUrl = items.getJSONObject(i).getString("imageUrl");
                    wallpapers.mSampleFeedItems[i] = new FeedDataWallpapers.FeedItem(imageUrl);
                    Log.d("testParsing", "=====looping thru items and the image Url is"+imageUrl);
                }
            }else
                Log.d("testParsing", "===No items to parse====");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
