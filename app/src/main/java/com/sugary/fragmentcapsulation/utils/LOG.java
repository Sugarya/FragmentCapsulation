package com.sugary.fragmentcapsulation.utils;

import android.util.Log;

/**
 * Created by Ethan on 17/3/22.
 * 日志输出，高灵活性
 */
public class LOG {

    private static boolean isDebug = true;

    public static void d(String tag,String msg){
        if(isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag,String msg){
        if(isDebug) {
            Log.e(tag, msg);
        }
    }
}
