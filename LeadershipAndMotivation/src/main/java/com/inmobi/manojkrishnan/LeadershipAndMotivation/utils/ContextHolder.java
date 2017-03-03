package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.content.Context;

/**
 * Created by manoj.krishnan on 6/1/16.
 */
public final class ContextHolder {
    private static Context ctxtinst;
    public static void init(Context ctxt)
    {
        ctxtinst = ctxt;
    }
    public static Context getApplicationContext(){
        return ctxtinst.getApplicationContext();
    }
}
