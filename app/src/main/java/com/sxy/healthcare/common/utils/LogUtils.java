package com.sxy.healthcare.common.utils;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * @Description: 日志工具类
 * @author ※简单※
 * @date  2016-11-10 15:40
 */

public class LogUtils {
	

    /** debug开关. */
	public static boolean DEBUG = true;
	
	/** info开关. */
	public static boolean INFO = true;
	
	/** error开关. */
	public static boolean ERROE = true;
	
	/** 起始执行时间. */
	public static long startLogTimeInMillis = 0;

	/**
	 * debug日志
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg){
		if(DEBUG)
		Log.d(tag, msg);
	}
	
	
	/**
	 * debug日志
	 * @param clazz
	 * @param msg
	 */
	public static void d(Class<?> clazz, String msg){
		String tag = clazz.getSimpleName();
		d(tag, msg);
	}
	
	/**
	 * debug日志
	 * @param context
	 * @param msg
	 */
	public static void d(Context context, String msg){
		String tag=context.getClass().getSimpleName();
		d(tag, msg);
	}

	/**
	 * debug日志
	 * @param context
	 * @param format
	 * @param args
	 */
	public static void d(Context context, String format, Object... args) {
		String tag = context.getClass().getSimpleName();
        d(tag, buildMessage(format, args));
    }


	/**
	 * debug日志
	 * @param clazz
	 * @param format
	 * @param args
	 */
	public static void d(Class<?> clazz, String format, Object... args) {
		String tag = clazz.getSimpleName();
        d(tag, buildMessage(format, args));
    }
	
	/**
	 * info日志
	 * @param tag
	 * @param message
	 */
	public static void i(String tag, String message) {
		Log.i(tag, message);
	}
	
	/**
	 * info日志
	 * @param context
	 * @param message
	 */
	public static void i(Context context, String message) {
		String tag = context.getClass().getSimpleName();
		i(tag, message);
	}
	
	/**
	 * info日志
	 * @param clazz
	 * @param message
	 */
	public static void i(Class<?> clazz, String message) {
		String tag = clazz.getSimpleName();
		i(tag, message);
	}
	
	/**
	 * info日志
	 * @param context
	 * @param format
	 * @param args
	 */
	public static void i(Context context, String format, Object... args) {
		String tag = context.getClass().getSimpleName();
        i(tag, buildMessage(format, args));
    }
	
	/**
	 * info日志
	 * @param clazz
	 * @param format
	 * @param args
	 */
	public static void i(Class<?> clazz, String format, Object... args) {
		String tag = clazz.getSimpleName();
        i(tag, buildMessage(format, args));
    }
	
	
	
	/**
	 * error日志
	 * @param tag
	 * @param message
	 */
	public static void e(String tag, String message) {
		Log.e(tag, message);
	}
	
	/**
	 * error日志
	 * @param context
	 * @param message
	 */
	public static void e(Context context, String message) {
		String tag = context.getClass().getSimpleName();
		e(tag, message);
	}
	
	/**
	 * error日志
	 * @param clazz
	 * @param message
	 */
	public static void e(Class<?> clazz, String message) {
		String tag = clazz.getSimpleName();
		e(tag, message);
	}
	
	
	/**
	 * error日志
	 * @param context
	 * @param format
	 * @param args
	 */
	public static void e(Context context, String format, Object... args) {
		String tag = context.getClass().getSimpleName();
        e(tag, buildMessage(format, args));
    }
	
	/**
	 * error日志
	 * @param clazz
	 * @param format
	 * @param args
	 */
	public static void e(Class<?> clazz, String format, Object... args) {
		String tag = clazz.getSimpleName();
        e(tag, buildMessage(format, args));
    }
	
	/**
	 * 描述：记录当前时间毫秒.
	 * 
	 */
	public static void prepareLog(String tag) {
		Calendar current = Calendar.getInstance();
		startLogTimeInMillis = current.getTimeInMillis();
		Log.d(tag,"日志计时开始："+startLogTimeInMillis);
	}
	
	/**
	 * 描述：记录当前时间毫秒.
	 * 
	 */
	public static void prepareLog(Context context) {
		String tag = context.getClass().getSimpleName();
		prepareLog(tag);
	}
	
	/**
	 * 描述：记录当前时间毫秒.
	 * 
	 */
	public static void prepareLog(Class<?> clazz) {
		String tag = clazz.getSimpleName();
		prepareLog(tag);
	}
	
	/**
	 * 描述：打印这次的执行时间毫秒，需要首先调用prepareLog().
	 *
	 * @param tag 标记
	 * @param message 描述
	 * @param printTime 是否打印时间
	 */
	public static void d(String tag, String message, boolean printTime) {
		Calendar current = Calendar.getInstance();
		long endLogTimeInMillis = current.getTimeInMillis();
		Log.d(tag,message+":"+(endLogTimeInMillis-startLogTimeInMillis)+"ms");
	}
	
	
	/**
	 * 描述：打印这次的执行时间毫秒，需要首先调用prepareLog().
	 *
	 * @param context 标记
	 * @param message 描述
	 * @param printTime 是否打印时间
	 */
	public static void d(Context context, String message, boolean printTime) {
		String tag = context.getClass().getSimpleName();
	    d(tag,message,printTime);
	}
	
	/**
	 * 描述：打印这次的执行时间毫秒，需要首先调用prepareLog().
	 *
	 * @param clazz 标记
	 * @param message 描述
	 * @param printTime 是否打印时间
	 */
	public static void d(Class<?> clazz, String message, boolean printTime) {
		String tag = clazz.getSimpleName();
		d(tag,message,printTime);
	}

	/**
	 * debug日志的开关
	 * @param d
	 */
	public static void debug(boolean d) {
		DEBUG  = d;
	}
	
	/**
	 * info日志的开关
	 * @param i
	 */
	public static void info(boolean i) {
		INFO  = i;
	}
	
	/**
	 * error日志的开关
	 * @param e
	 */
	public static void error(boolean e) {
		ERROE  = e;
	}
	
	/**
	 * 设置日志的开关
	 * @param e
	 */
	public static void setVerbose(boolean d,boolean i,boolean e) {
		DEBUG  = d;
		INFO  = i;
		ERROE  = e;
	}
	
	/**
	 * 打开/关闭所有日志，默认全打开
	 * 
	 */
	public static void openAll(boolean bool) {
		DEBUG  = bool;
		INFO  = bool;
		ERROE  = bool;
	}
	
	/**
	 * format日志
	 * @param format
	 * @param args
	 * @return
	 */
	private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtils.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }

}
