package com.inmobi.manojkrishnan.LeadershipAndMotivation.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by nitin.singh on 02/12/14.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static boolean isNetworkAvailable(Context ctxt) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctxt.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && !isDeviceModeIdle(ctxt);
    }

    private static boolean isDeviceModeIdle(Context ctxt) {
        boolean isDeviceIdle = false;
        PowerManager powerManager = (PowerManager) ctxt.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isDeviceIdle = powerManager.isDeviceIdleMode();
        }
        return isDeviceIdle;

    }
    public static String encodeMapAndConvertToDelimitedString(Map<String, ? extends Object> map,
                                                              String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(String.format(Locale.US, "%s=%s", urlEncodeString(key), urlEncodeString(map.get(key).toString())));
        }
        return sb.toString();
    }

    public static String urlEncodeString(String value) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static Map<String, String> getEncodedMap(Map<String, ? extends Object> map) {
        Map<String, String> encodedMap = new HashMap<String, String>();
        for (String key : map.keySet()) {
            encodedMap.put(urlEncodeString(key), urlEncodeString(map.get(key).toString()));
        }
        return encodedMap;
    }

    public static void sanitizeMap(Map<String, String> map) {
        if (map != null) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            Map<String, String> sanitizedEntries = new HashMap<>();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (entry.getValue() == null || entry.getValue().trim().length() == 0 || entry.getKey() == null || entry.getKey().trim().length() == 0) {
                    iterator.remove();
                } else {
                    // Remove any unnecessary spaces from params
                    if (!entry.getKey().equals(entry.getKey().trim())) {
                        iterator.remove();
                        sanitizedEntries.put(entry.getKey().trim(), entry.getValue().trim());
                    } else {
                        sanitizedEntries.put(entry.getKey(), entry.getValue().trim());
                    }
                }
            }

            map.putAll(sanitizedEntries);
        }
    }

    public static String substituteMacros(String url, Map<String, String> macroSubstitutions) {
        //Replace the macros before firing the URL
        if(macroSubstitutions != null && macroSubstitutions.size() > 0) {
            final Iterator<Map.Entry<String, String>> iterator = macroSubstitutions.entrySet().iterator();
            while(iterator.hasNext()) {
                final Map.Entry<String, String> entry = iterator.next();
                url = url.replace(entry.getKey(), entry.getValue());
            }
        }
        return url;
    }


    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {

                byteArrayOutputStream.write(buffer, 0, n);
                Log.d("test", "Read the content");
            }
            return byteArrayOutputStream.toByteArray();
        } finally {
            byteArrayOutputStream.close();
        }
    }

    public static void closeSilently(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (IOException e) {
            Log.d("test", "Failed to close closable", e);
        }
    }
}
