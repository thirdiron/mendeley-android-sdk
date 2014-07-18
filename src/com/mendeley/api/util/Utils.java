package com.mendeley.api.util;

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
}
