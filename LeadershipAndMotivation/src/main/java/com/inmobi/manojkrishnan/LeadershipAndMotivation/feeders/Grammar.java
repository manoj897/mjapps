package com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders;

import android.content.Context;
import android.util.Log;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkHandler;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkResponse;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.Parser;

/**
 * Created by manoj.krishnan on 6/2/16.
 */
public class Grammar {

    private static NetworkResponse mResponse;
    private static KeyValueStore mKeyValueStore;
    static Context ctxt;

    public Grammar(Context ctxt)
    {
        this.ctxt = ctxt;
    }
    public static String getWallpaperGrammar(){
        if(mResponse!=null)
            return mResponse.getResponse();
        else
            return null;
    }
    public static void setwallpaperGrammar(){
        mKeyValueStore = KeyValueStore.getInstance(ctxt.getApplicationContext(), "QuotesCounter");
        if(mKeyValueStore.getInt("counter",0) == 0) {
            mKeyValueStore.putInt("counter", 1);
            Log.d("Grammar", "count when first time " + mKeyValueStore.getInt("counter", 1));

        }

        Thread WallpaperGrammar = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler hdlr = new NetworkHandler();
                mResponse = hdlr.connect("http://motivationpics.s3-ap-southeast-1.amazonaws.com/grammar/wallpapergrammar.txt");
                if(mResponse != null)
                {
                    Parser.parseWallpaperGrammar(ctxt,mResponse.getResponse(), mKeyValueStore.getInt("counter", 1));
                }
            }
        });
        WallpaperGrammar.start();
    }
}
