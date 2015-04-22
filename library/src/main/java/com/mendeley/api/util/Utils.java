package com.mendeley.api.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * General utilities class
 */
public class Utils {
	public static SimpleDateFormat dateFormat =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        final ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo();
    }

    public static boolean isOnline(Context context) {
        final NetworkInfo activeNetwork = getNetworkInfo(context);
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
    }
}
