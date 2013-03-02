package com.m6.gocook.util.log;

import android.util.Log;

/**
 * 应用日志输出控制类，调试使用
 */
public class Logger {

    /*系统日志Tag*/
    private static final String TAG = "GoCook";
    /*系统日志调试开关*/
    public static final boolean DEBUG = true;
    
    /**
     * 默认tag v级别日志输出
     * @param message
     */
    public static void v(String message) {
        if(DEBUG) Log.v(TAG, message);
    }

    /**
     * 默认tag d级别日志输出
     * @param message
     */
    public static void d(String message) {
        if(DEBUG) Log.d(TAG, message);
    }
    /**
     * 默认tag i级别日志输出，比较常用
     * @param message
     */
    public static void i(String message) {
        if(DEBUG) Log.i(TAG, message);
    }
    /**
     * 默认tag w——警告日志输出
     * @param message
     */
    public static void w(String message) {
        if(DEBUG) Log.w(TAG, message);
    }
    
    /**
     * 默认tag 警告跟踪调用日志输出
     * @param message
     * @param tr
     */
    public static void w(String message, Throwable tr) {
        if(DEBUG) Log.w(TAG, message, tr);
    }

    /**
     * 默认tag 错误日志输出，用于try-catch异常输出
     * @param message
     */
    public static void e(String message) {
        if(DEBUG) Log.e(TAG, message);
    }

    /*以下处理类同于上面的那些函数，只不过是tag是自定义*/
    
    public static void v(String TAG, String message) {
        if(DEBUG) Log.v(TAG, message);
    }

    public static void d(String TAG, String message) {
        if(DEBUG) Log.d(TAG, message);
    }

    public static void i(String TAG, String message) {
        if(DEBUG) Log.i(TAG, message);
    }

    public static void w(String TAG, String message) {
        if(DEBUG) Log.w(TAG, message);
    }
    
    public static void w(String TAG, String message, Throwable tr) {
        if(DEBUG) Log.w(TAG, message, tr);
    }

    public static void e(String TAG, String message) {
        if(DEBUG) Log.e(TAG, message);
    }
    
    public static void e(String TAG, String message, Throwable tr) {
        if(DEBUG) Log.e(TAG, message, tr);
    }

    
}
