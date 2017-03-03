package com.inmobi.manojkrishnan.LeadershipAndMotivation.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public final class KeyValueStore {

    private static HashMap<String, KeyValueStore> sKeyValueStores = new HashMap<String, KeyValueStore>();
    private static final Object sKeyValueStoreLock = new Object();

    private SharedPreferences mSharedPref;

    private KeyValueStore(final Context context, final String fileKey) {
        mSharedPref = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE);
    }

    public static String getFullyQualifiedFileKey(String fileKey) {
        return "com.mj.keyValueStore." + fileKey;
    }

    public static KeyValueStore getInstance(Context context, final String fileKey) {
        final String fullyQualifiedFileKey = getFullyQualifiedFileKey(fileKey);
        KeyValueStore kv = sKeyValueStores.get(fullyQualifiedFileKey);
        if (kv != null) {
            return kv;
        } else {
            synchronized (sKeyValueStoreLock) {
                kv = sKeyValueStores.get(fullyQualifiedFileKey);
                if (kv != null) {
                    return kv;
                }

                kv = new KeyValueStore(context, fullyQualifiedFileKey);
                sKeyValueStores.put(fullyQualifiedFileKey, kv);
                return kv;
            }
        }
    }

    public static KeyValueStore getInstance(final String fileKey) {
        return getInstance(ContextHolder.getApplicationContext(), fileKey);
    }

    public void putString(final String key, final String value) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getString(final String key, final String defaultValue) {
        return mSharedPref.getString(key, defaultValue);
    }


    public void putInt(final String key, final int value) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public int getInt(final String key, final int defaultValue) {
        return mSharedPref.getInt(key, defaultValue);
    }

    public void putFloat(final String key, final float value) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putFloat(key, value);
        edit.apply();
    }

    public float getFloat(final String key, final float defaultValue) {
        return mSharedPref.getFloat(key, defaultValue);
    }

    public void putLong(final String key, final long value) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public long getLong(final String key, final long defaultValue) {
        return mSharedPref.getLong(key, defaultValue);
    }

    public void putBoolean(final String key, final boolean value) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        return mSharedPref.getBoolean(key, defaultValue);
    }

    public Map<String, ?> getAll() {
        return mSharedPref.getAll();
    }

    public boolean contains(final String key) {
        return mSharedPref.contains(key);
    }

    public boolean remove(final String key) {
        if (contains(key)) {
            SharedPreferences.Editor edit = mSharedPref.edit();
            edit.remove(key);
            edit.apply();
            return true;
        } else {
            return false;
        }
    }

    public void clearAll() {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.clear();
        edit.apply();
    }
}