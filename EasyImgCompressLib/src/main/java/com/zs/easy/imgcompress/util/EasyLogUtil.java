package com.zs.easy.imgcompress.util;

import android.util.Log;

public class EasyLogUtil {
    public static final String TAG = "EasyImgCompress";

    public static boolean enableLog = false;

    public static void i(String content) {
        if (enableLog) {
            i(TAG, content);
        }
    }

    public static void i(String tag, String content) {
        if (enableLog) {
            Log.i(tag, content);
        }
    }

    public static void e(String content) {
        if (enableLog) {
            e(TAG, content);
        }
    }

    public static void e(String tag, String content) {
        if (enableLog) {
            Log.e(tag, content);
        }
    }

}
