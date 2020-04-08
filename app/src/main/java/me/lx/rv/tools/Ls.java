package me.lx.rv.tools;

import android.util.Log;

import me.lx.rv.BuildConfig;


public class Ls {
    /**
     * 日志输出级别NONE
     */

    public static final int LEVEL_NONE = 0;

    /**
     * 日志输出级别V
     */

    public static final int LEVEL_VERBOSE = 1;

    /**
     * 日志输出级别D
     */

    public static final int LEVEL_DEBUG = 2;

    /**
     * 日志输出级别I
     */

    public static final int LEVEL_INFO = 3;

    /**
     * 日志输出级别W
     */

    public static final int LEVEL_WARN = 4;

    /**
     * 日志输出级别E
     */

    public static final int LEVEL_ERROR = 5;


    /**
     * 日志输出时的TAG
     */

    public static final String sTag = "rvAdapter";


    /**
     * 是否允许输出log
     * <p/>
     * LEVEL_ERROR 就是打开Log输出
     * LEVEL_NONE  关闭输出
     */

    private static int mDebuggable = BuildConfig.DEBUG ? LEVEL_ERROR : LEVEL_NONE;
    // private static int mDebuggable = LEVEL_NONE;

    /**
     * 用于记时的变量
     */

    //private static long mTimestamp = 0;

    /**
     * 以级别为 d 的形式输出LOG
     */
//    public static void v(String msg) {
//        if (mDebuggable >= LEVEL_VERBOSE) {
//            Log.v(sTag, msg);
//        }
//    }


    /**
     * 以级别为 d 的形式输出LOG
     */

    public static void d(String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {

            Log.d(sTag, msg);
        }
    }


    public static void d(Object obj, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(obj.getClass().getSimpleName() + "--" + sTag, msg);
        }
    }


    /**
     * 以级别为 i 的形式输出LOG
     */

    public static void i(String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(sTag, msg);
        }
    }


    /**
     * 以级别为 w 的形式输出LOG
     */

    public static void w(String msg) {

        if (mDebuggable >= LEVEL_WARN) {

            Log.w(sTag, msg);

        }

    }


    /**
     * 以级别为 w 的形式输出Throwable
     */

//    public static void w(Throwable tr) {
//        if (mDebuggable >= LEVEL_WARN) {
//            Log.w(sTag, "", tr);
//        }
//    }


    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */

//    public static void w(String msg, Throwable tr) {
//        if (mDebuggable >= LEVEL_WARN && null != msg) {
//            Log.w(sTag, msg, tr);
//        }
//    }


    /**
     * 以级别为 e 的形式输出LOG
     */

//    public static void e(String msg) {
//        if (mDebuggable >= LEVEL_ERROR) {
//            Log.e(sTag, msg);
//        }
//    }


    /**
     * 以级别为 e 的形式输出Throwable
     */

//    public static void e(Throwable tr) {
//        if (mDebuggable >= LEVEL_ERROR) {
//            Log.e(sTag, "", tr);
//        }
//    }


    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */

//    public static void e(String msg, Throwable tr) {
//        if (mDebuggable >= LEVEL_ERROR && null != msg) {
//            Log.e(sTag, msg, tr);
//        }
//    }


    /**
     * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg
     */

//    public static void elapsed(String msg) {
//
//        long currentTime = System.currentTimeMillis();
//
//        long elapsedTime = currentTime - mTimestamp;
//
//        mTimestamp = currentTime;
//
//        e("[Elapsed：" + elapsedTime + "]" + msg);
//
//    }


}
