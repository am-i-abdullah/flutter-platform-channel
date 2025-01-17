package com.netum.device.utils;


import android.util.Log;

public final class BleLog {

    public static boolean isPrint = true;
    private static final String defaultTag = "NetumSDK";

    public static void d(String msg) {
        if (isPrint && msg != null)
            Log.d(defaultTag, msg);
    }

    public static void i(String msg) {
        if (isPrint && msg != null)
            Log.i(defaultTag, msg);
    }

    public static void w(String msg) {
        if (isPrint && msg != null)
            Log.w(defaultTag, msg);
    }

    public static void e(String msg) {
        if (isPrint && msg != null)
            Log.e(defaultTag, msg);
    }

}
